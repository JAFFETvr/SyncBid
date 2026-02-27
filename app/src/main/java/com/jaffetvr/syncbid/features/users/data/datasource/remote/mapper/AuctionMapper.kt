package com.jaffetvr.syncbid.features.users.data.datasource.remote.mapper

import com.jaffetvr.syncbid.features.users.data.datasource.local.model.AuctionEntity
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.AuctionDto
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidResponseDto
import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.entities.AuctionStatus
import com.jaffetvr.syncbid.features.users.domain.entities.Bid
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

// ─── Helpers ────────────────────────────────────────────────────────────────

/**
 * Convierte el endTime ISO-8601 del servidor a segundos restantes.
 * El servidor devuelve: "2025-12-31T23:59:59"
 */
private fun isoToSecondsRemaining(isoString: String): Long {
    return try {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val endTime = LocalDateTime.parse(isoString, formatter)
        val now = LocalDateTime.now()
        val seconds = java.time.Duration.between(now, endTime).seconds
        if (seconds < 0) 0L else seconds
    } catch (e: Exception) {
        0L
    }
}

/**
 * Mapea el status del servidor al dominio Android.
 * Servidor: "ACTIVE", "FINISHED", "CANCELLED"
 * Dominio:  LIVE, ENDED
 */
private fun mapStatus(serverStatus: String): String = when (serverStatus.uppercase()) {
    "ACTIVE" -> "LIVE"
    "FINISHED" -> "ENDED"
    "CANCELLED" -> "ENDED"
    else -> "LIVE"
}

// ─── DTO → Entity (para Room SSOT) ──────────────────────────────────────────

fun AuctionDto.toEntity(): AuctionEntity = AuctionEntity(
    id = id.toString(),
    name = title,                                           // servidor: "title" → android: "name"
    description = description ?: "",
    category = "General",                                   // servidor no devuelve categoría
    imageUrl = imageUrl,
    basePrice = currentPrice,                               // servidor no devuelve startingPrice en lista
    currentPrice = currentPrice,
    timeRemainingSeconds = isoToSecondsRemaining(endTime),
    bidCount = bidCount ?: 0,                                         // servidor no devuelve bidCount en AuctionResponse
    status = mapStatus(status),
    leaderId = sellerUsername,                              // usamos sellerUsername como referencia
    leaderName = winnerUsername ?: sellerUsername,
    isUserWinning = false
)

// ─── Entity → Domain ────────────────────────────────────────────────────────

fun AuctionEntity.toDomain(): Auction = Auction(
    id = id,
    name = name,
    description = description,
    category = category,
    imageUrl = imageUrl,
    basePrice = basePrice,
    currentPrice = currentPrice,
    timeRemainingSeconds = timeRemainingSeconds,
    bidCount = bidCount,
    status = AuctionStatus.fromString(status),
    leaderId = leaderId,
    leaderName = leaderName,
    isUserWinning = isUserWinning
)

// ─── BidResponseDto → Bid Domain ────────────────────────────────────────────

fun BidResponseDto.toDomain(auctionId: String): Bid = Bid(
    bidId = id.toString(),
    auctionId = auctionId,
    amount = amount,
    userId = bidderUsername ?: "",
    timestamp = 0L,
    isLeader = true
)