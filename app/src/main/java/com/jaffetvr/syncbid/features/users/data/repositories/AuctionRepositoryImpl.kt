package com.jaffetvr.syncbid.features.users.data.repositories

import com.jaffetvr.syncbid.features.users.data.datasource.local.dao.AuctionDao
import com.jaffetvr.syncbid.features.users.data.datasource.remote.AuctionWebSocketDataSource
import com.jaffetvr.syncbid.features.users.data.datasource.remote.api.AuctionApi
import com.jaffetvr.syncbid.features.users.data.datasource.remote.mapper.toDomain
import com.jaffetvr.syncbid.features.users.data.datasource.remote.mapper.toEntity
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidRequestDto
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidUpdateData
import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.entities.Bid
import com.jaffetvr.syncbid.features.users.domain.repositories.AuctionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repositorio con patrón SSOT (Single Source of Truth).
 *
 * Flujo de datos:
 *   API (GET /api/v1/auctions/active) → Room → Flow<Domain> → UI
 *   WebSocket (/topic/auctions/{id}) → Room → (emit automático) → UI
 */
class AuctionRepositoryImpl @Inject constructor(
    private val auctionApi: AuctionApi,
    private val auctionDao: AuctionDao,
    private val webSocketDataSource: AuctionWebSocketDataSource
) : AuctionRepository {

    // ─── Observadores desde Room (SSOT) ─────────────────────────────────────

    override fun observeAuctions(): Flow<List<Auction>> =
        auctionDao.observeAllAuctions().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeAuctionById(id: String): Flow<Auction?> =
        auctionDao.observeAuctionById(id).map { entity ->
            entity?.toDomain()
        }

    // ─── Refresco desde API → Room ───────────────────────────────────────────

    /**
     * Llama a GET /api/v1/auctions/active
     * El servidor devuelve ApiResponse<List<AuctionDto>>
     * donde AuctionDto tiene: id, title, description, currentPrice, endTime, status, etc.
     */
    override suspend fun refreshAuctions(): Result<Unit> =
        try {
            val response = auctionApi.getActiveAuctions()

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    val dtos = body.data ?: emptyList()
                    val entities = dtos.map { it.toEntity() }
                    auctionDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.message ?: "Error del servidor"))
                }
            } else {
                Result.failure(Exception("Error HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }

    // ─── Envío de puja → API ─────────────────────────────────────────────────

    /**
     * POST /api/v1/auctions/{auctionId}/bids
     * Body: { "amount": 150.00 }
     * El servidor devuelve ApiResponse<BidResponseDto>
     */
    override suspend fun placeBid(auctionId: String, amount: Double): Result<Bid> =
        try {
            val response = auctionApi.placeBid(auctionId, BidRequestDto(amount))

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    val bidDto = body.data
                    // Actualizamos Room con la puja confirmada
                    auctionDao.updateBidInfo(
                        auctionId = auctionId,
                        price = bidDto.amount,
                        bidCount = 0,   // el servidor no devuelve bidCount en BidResponse
                        leaderId = bidDto.bidderUsername,
                        leaderName = bidDto.bidderUsername,
                        isUserWinning = true
                    )
                    Result.success(bidDto.toDomain(auctionId))
                } else {
                    Result.failure(Exception(body?.message ?: "Puja rechazada"))
                }
            } else {
                // El servidor devuelve 400 con un ApiResponse de error
                Result.failure(Exception("Error al pujar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }

    // ─── WebSocket: actualizaciones en tiempo real → Room ───────────────────

    /**
     * Se conecta al WebSocket para una subasta específica.
     * Escribe los eventos recibidos en Room → la UI se actualiza sola.
     *
     * Para usar: llamar con el auctionId de la pantalla actual.
     */
    override fun startRealTimeUpdates(): Flow<Unit> = flow {
        // Esta función es un fallback sin auctionId específico.
        // Preferir startRealTimeUpdatesForAuction(auctionId)
    }

    /**
     * Versión mejorada que escucha eventos para una subasta concreta.
     * El ViewModel debe llamar a esta función al entrar a la pantalla de detalle.
     */
    fun startRealTimeUpdatesForAuction(auctionId: String): Flow<Unit> = flow {
        webSocketDataSource.observeAuction(auctionId).collect { update ->
            when (update.type) {
                "NEW_BID" -> {
                    // data es BidUpdateData
                    val bidData = update.data as? BidUpdateData
                    if (bidData?.amount != null) {
                        auctionDao.updateBidInfo(
                            auctionId = auctionId,
                            price = bidData.amount,
                            bidCount = 0,
                            leaderId = bidData.bidderUsername,
                            leaderName = bidData.bidderUsername,
                            isUserWinning = false   // el servidor no nos dice si es el usuario actual
                        )
                    }
                }
                "AUCTION_FINISHED" -> {
                    // data es String (winnerUsername)
                    val winner = update.data as? String
                    auctionDao.updateBidInfo(
                        auctionId = auctionId,
                        price = 0.0,   // mantenemos el precio actual
                        bidCount = 0,
                        leaderId = winner,
                        leaderName = winner,
                        isUserWinning = false
                    )
                }
            }
            emit(Unit)
        }
    }
}