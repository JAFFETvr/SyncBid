package com.jaffetvr.syncbid.features.admin.data.datasource.remote.api

import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AdminApi {

    @POST("api/v1/auctions")
    suspend fun createAuction(
        @Body request: CreateAuctionRequestDto
    ): Response<ApiResponseDto<CreateAuctionResponseDto>>

    @Multipart
    @POST("api/v1/auctions/{id}/image")
    suspend fun uploadAuctionImage(
        @Path("id") auctionId: Long,
        @Part file: MultipartBody.Part
    ): Response<ApiResponseDto<CreateAuctionResponseDto>>

    // Ruta corregida según tu AuctionController.java
    @GET("api/v1/auctions/my-auctions")
    suspend fun getInventory(): Response<ApiResponseDto<List<InventoryItemDto>>>

    // Ruta corregida (usando /active porque no tienes un /stats en el backend aún)
    @GET("api/v1/auctions/active")
    suspend fun getStats(): Response<ApiResponseDto<AdminStatsDto>>
}