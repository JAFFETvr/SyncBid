package com.jaffetvr.syncbid.features.users.data.repositories

import com.jaffetvr.syncbid.features.users.data.datasource.local.dao.AuctionDao
import com.jaffetvr.syncbid.features.users.data.datasource.remote.AuctionWebSocketDataSource
import com.jaffetvr.syncbid.features.users.data.datasource.remote.api.AuctionApi
import com.jaffetvr.syncbid.features.users.data.datasource.remote.mapper.toDomain
import com.jaffetvr.syncbid.features.users.data.datasource.remote.mapper.toEntity
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidRequestDto
import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.entities.Bid
import com.jaffetvr.syncbid.features.users.domain.repositories.AuctionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementación del repositorio con patrón SSOT (Single Source of Truth).
 *
 * Flujo de datos:
 *   API / WebSocket  →  Room (SSOT)  →  Flow<Domain>  →  UI
 *
 * La UI NUNCA lee directamente de la red. Siempre lee de Room.
 */
class AuctionRepositoryImpl @Inject constructor(
    private val auctionApi: AuctionApi,
    private val auctionDao: AuctionDao,
    private val webSocketDataSource: AuctionWebSocketDataSource
) : AuctionRepository {

    /**
     * Observa todas las subastas desde Room (fuente de verdad).
     * Room emite automáticamente cuando los datos cambian.
     */
    override fun observeAuctions(): Flow<List<Auction>> =
        auctionDao.observeAllAuctions().map { entities ->
            entities.map { it.toDomain() }
        }

    /**
     * Observa una subasta por ID desde Room.
     */
    override fun observeAuctionById(id: String): Flow<Auction?> =
        auctionDao.observeAuctionById(id).map { entity ->
            entity?.toDomain()
        }

    /**
     * Refresca datos de la API y los guarda en Room.
     * API → Room → (Room emite cambios automáticamente a los observers)
     */
    override suspend fun refreshAuctions(): Result<Unit> =
        try {
            val response = auctionApi.getAuctions()
            if (response.isSuccessful && response.body() != null) {
                val entities = response.body()!!.map { it.toEntity() }
                auctionDao.insertAll(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al cargar subastas: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Envía una puja al servidor.
     * NO actualiza Room aquí — el ViewModel realiza actualización optimista.
     */
    override suspend fun placeBid(auctionId: String, amount: Double): Result<Bid> =
        try {
            val response = auctionApi.placeBid(auctionId, BidRequestDto(amount))
            if (response.isSuccessful && response.body() != null) {
                val bid = response.body()!!.toDomain()
                // Actualizar Room con datos confirmados del servidor
                auctionDao.updateBidInfo(
                    auctionId = auctionId,
                    price = bid.amount,
                    bidCount = -1, // se actualizará con el WebSocket
                    leaderId = bid.userId,
                    leaderName = null,
                    isUserWinning = bid.isLeader
                )
                Result.success(bid)
            } else {
                Result.failure(Exception("Error al pujar: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Conecta al WebSocket y escribe las actualizaciones en Room.
     * WebSocket → Room (SSOT) → UI (vía observeAuctions/observeAuctionById)
     */
    override fun startRealTimeUpdates(): Flow<Unit> = flow {
        webSocketDataSource.observeAuctionUpdates().collect { update ->
            auctionDao.updateBidInfo(
                auctionId = update.auctionId,
                price = update.currentPrice,
                bidCount = update.bidCount,
                leaderId = update.leaderId,
                leaderName = update.leaderName,
                isUserWinning = false // se determina en el servidor
            )
            emit(Unit)
        }
    }
}
