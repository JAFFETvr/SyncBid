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

class AuctionRepositoryImpl @Inject constructor(
    private val auctionApi: AuctionApi,
    private val auctionDao: AuctionDao,
    private val webSocketDataSource: AuctionWebSocketDataSource
) : AuctionRepository {

    override fun observeAuctions(): Flow<List<Auction>> =
        auctionDao.observeAllAuctions().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeAuctionById(id: String): Flow<Auction?> =
        auctionDao.observeAuctionById(id).map { entity ->
            entity?.toDomain()
        }

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

    override suspend fun placeBid(auctionId: String, amount: Double): Result<Bid> =
        try {
            val response = auctionApi.placeBid(auctionId, BidRequestDto(amount))

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    val bidDto = body.data

                    val currentAuction = auctionDao.getAuctionByIdSync(auctionId)
                    val newBidCount = (currentAuction?.bidCount ?: 0) + 1

                    auctionDao.updateBidInfo(
                        auctionId = auctionId,
                        price = bidDto.amount,
                        bidCount = newBidCount,
                        leaderId = bidDto.bidderUsername,
                        leaderName = bidDto.bidderUsername,
                        isUserWinning = true
                    )
                    Result.success(bidDto.toDomain(auctionId))
                } else {
                    Result.failure(Exception(body?.message ?: "Puja rechazada"))
                }
            } else {
                Result.failure(Exception("Error al pujar: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }

    override fun startRealTimeUpdates(): Flow<Unit> = flow { }

    override fun startRealTimeUpdatesForAuction(auctionId: String): Flow<Unit> = flow {
        webSocketDataSource.observeAuction(auctionId).collect { update ->
            when (update.type) {
                "NEW_BID" -> {
                    val bidData = update.data as? BidUpdateData
                    if (bidData?.amount != null) {
                        val currentAuction = auctionDao.getAuctionByIdSync(auctionId)
                        val newBidCount = (currentAuction?.bidCount ?: 0) + 1

                        auctionDao.updateBidInfo(
                            auctionId = auctionId,
                            price = bidData.amount,
                            bidCount = newBidCount,
                            leaderId = bidData.bidderUsername,
                            leaderName = bidData.bidderUsername,
                            isUserWinning = false
                        )
                    }
                }
                "AUCTION_FINISHED" -> {
                    val winner = update.data as? String
                    auctionDao.markAuctionAsFinished(
                        auctionId = auctionId,
                        winner = winner
                    )
                }
            }
            emit(Unit)
        }
    }
}