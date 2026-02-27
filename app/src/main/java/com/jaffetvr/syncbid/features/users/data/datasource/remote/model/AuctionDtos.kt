package com.jaffetvr.syncbid.features.users.data.datasource.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Wrapper genérico de respuesta del servidor.
 * Reutiliza el mismo patrón que auth.
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?,
    @SerializedName("timestamp") val timestamp: String?
)

/**
 * Mapea AuctionResponse.java del servidor:
 * { id(Long), sellerUsername, imageUrl, title, description,
 *   currentPrice(BigDecimal), endTime(LocalDateTime ISO), status(AuctionStatus), winnerUsername }
 *
 * La API NO devuelve: category, basePrice, bidCount, leaderId, leaderName, isUserWinning
 * Esos valores se calculan o defaultean en el mapper.
 */
data class AuctionDto(
    @SerializedName("id") val id: Long,
    @SerializedName("sellerUsername") val sellerUsername: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("currentPrice") val currentPrice: Double,
    @SerializedName("endTime") val endTime: String,       // ISO-8601: "2025-12-31T23:59:59"
    @SerializedName("status") val status: String,          // "ACTIVE", "FINISHED", "CANCELLED"
    @SerializedName("winnerUsername") val winnerUsername: String?,
    @SerializedName("bidCount") val bidCount: Int?
)

/**
 * POST /api/v1/auctions/{auctionId}/bids
 * El servidor espera: { "amount": 150.00 }
 */
data class BidRequestDto(
    @SerializedName("amount") val amount: Double
)

/**
 * Mapea BidResponse.java del servidor:
 * { id(Long), amount(BigDecimal), bidderUsername, createdAt(LocalDateTime) }
 */
data class BidResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("amount") val amount: Double,
    @SerializedName("bidderUsername") val bidderUsername: String?,
    @SerializedName("createdAt") val createdAt: String?
)

/**
 * Evento WebSocket — payload de AuctionUpdatePayload.java:
 * { type: "NEW_BID"|"AUCTION_FINISHED", data: BidResponseDto|String, message: String }
 *
 * Para NEW_BID:       data = BidResponseDto
 * Para AUCTION_FINISHED: data = String (username del ganador)
 */
data class AuctionUpdateDto(
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: Any?          // Gson lo deserializa según el type
)

/**
 * Dato de NEW_BID — misma estructura que BidResponseDto pero viene dentro de AuctionUpdateDto.data
 */
data class BidUpdateData(
    @SerializedName("id") val id: Long?,
    @SerializedName("amount") val amount: Double?,
    @SerializedName("bidderUsername") val bidderUsername: String?,
    @SerializedName("createdAt") val createdAt: String?
)