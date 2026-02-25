package com.jaffetvr.syncbid.features.users.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jaffetvr.syncbid.features.users.data.datasource.local.dao.AuctionDao
import com.jaffetvr.syncbid.features.users.data.datasource.local.model.AuctionEntity

@Database(
    entities = [AuctionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun auctionDao(): AuctionDao
}
