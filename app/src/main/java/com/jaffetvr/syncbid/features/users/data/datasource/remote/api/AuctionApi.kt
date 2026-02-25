package com.jaffetvr.syncbid.features.users.data.datasource.remote.api

import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.AuctionDto
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidRequestDto
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AuctionApi {

    @GET("auctions")
    suspend fun getAuctions(): Response<List<AuctionDto>>

    @GET("auctions/{id}")
    suspend fun getAuctionById(@Path("id") id: String): Response<AuctionDto>

    @POST("auctions/{id}/bids")
    suspend fun placeBid(
        @Path("id") auctionId: String,
        @Body request: BidRequestDto
    ): Response<BidResponseDto>
}
