package com.jaffetvr.syncbid.features.users.data.datasource.remote.api

import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.ApiResponse
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.AuctionDto
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidRequestDto
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuctionApi {

    /**
     * GET /api/v1/auctions/active
     * Devuelve: ApiResponse<List<AuctionDto>>
     */
    @GET("api/v1/auctions/active")
    suspend fun getActiveAuctions(): Response<ApiResponse<List<AuctionDto>>>

    /**
     * GET /api/v1/auctions/{id}
     * Devuelve: ApiResponse<AuctionDto>
     */
    @GET("api/v1/auctions/{id}")
    suspend fun getAuctionById(@Path("id") id: String): Response<ApiResponse<AuctionDto>>

    /**
     * POST /api/v1/auctions/{auctionId}/bids
     * Body: { "amount": 150.00 }
     * Devuelve: ApiResponse<BidResponseDto>
     */
    @POST("api/v1/auctions/{auctionId}/bids")
    suspend fun placeBid(
        @Path("auctionId") auctionId: String,
        @Body request: BidRequestDto
    ): Response<ApiResponse<BidResponseDto>>
}