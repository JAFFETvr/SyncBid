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

    @Query("DELETE FROM auctions")
    suspend fun clearAll()
}
