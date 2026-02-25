package com.jaffetvr.syncbid.features.admin.data.datasource.remote.api

import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.AdminStatsDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.CreateAuctionRequestDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.CreateAuctionResponseDto
import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.InventoryItemDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminApi {

    @POST("admin/auctions")
    suspend fun createAuction(@Body request: CreateAuctionRequestDto): Response<CreateAuctionResponseDto>

    @GET("admin/inventory")
    suspend fun getInventory(): Response<List<InventoryItemDto>>

    @GET("admin/stats")
    suspend fun getStats(): Response<AdminStatsDto>
}
