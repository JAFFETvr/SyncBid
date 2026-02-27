package com.jaffetvr.syncbid.features.admin.data.datasource.remote.api

import com.jaffetvr.syncbid.features.admin.data.datasource.remote.model.* // Asegúrate de importar los DTOs y ApiResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AdminApi {

    @POST("api/v1/auctions")
    suspend fun createAuction(
        @Body request: CreateAuctionRequestDto
    ): Response<ApiResponseDto<CreateAuctionResponseDto>> // Agregado wrapper

    @Multipart
    @POST("api/v1/auctions/{id}/image")
    suspend fun uploadAuctionImage(
        @Path("id") auctionId: Long,
        @Part file: MultipartBody.Part
    ): Response<ApiResponseDto<CreateAuctionResponseDto>>

    @GET("api/v1/auctions/active") // Verifica que esta ruta sea la correcta en tu API
    suspend fun getInventory(): Response<ApiResponseDto<List<InventoryItemDto>>> // Agregado wrapper

    @GET("api/v1/admin/stats") // Verifica que esta ruta sea la correcta en tu API
    suspend fun getStats(): Response<ApiResponseDto<AdminStatsDto>> // Agregado wrapper
}