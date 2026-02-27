package com.jaffetvr.syncbid.features.users.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jaffetvr.syncbid.features.users.data.datasource.local.model.AuctionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuctionDao {

    @Query("SELECT * FROM auctions ORDER BY timeRemainingSeconds ASC")
    fun observeAllAuctions(): Flow<List<AuctionEntity>>

    @Query("SELECT * FROM auctions WHERE id = :id")
    fun observeAuctionById(id: String): Flow<AuctionEntity?>

    // NUEVO: Permite consultar la subasta de forma directa (síncrona) para hacer cálculos
    @Query("SELECT * FROM auctions WHERE id = :id")
    suspend fun getAuctionByIdSync(id: String): AuctionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(auctions: List<AuctionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(auction: AuctionEntity)

    @Query("UPDATE auctions SET currentPrice = :price, bidCount = :bidCount, leaderId = :leaderId, leaderName = :leaderName, isUserWinning = :isUserWinning WHERE id = :auctionId")
    suspend fun updateBidInfo(
        auctionId: String,
        price: Double,
        bidCount: Int,
        leaderId: String?,
        leaderName: String?,
        isUserWinning: Boolean
    )

    // NUEVO: Método específico para cuando termina la subasta (así no sobrescribe el precio final ni las pujas con 0)
    @Query("UPDATE auctions SET status = :status, leaderId = :winner, leaderName = :winner WHERE id = :auctionId")
    suspend fun markAuctionAsFinished(
        auctionId: String,
        status: String = "ENDED",
        winner: String?
    )

    @Query("DELETE FROM auctions")
    suspend fun clearAll()
}