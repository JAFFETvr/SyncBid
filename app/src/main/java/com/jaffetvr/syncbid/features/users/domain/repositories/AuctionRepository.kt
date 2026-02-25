package com.jaffetvr.syncbid.features.users.domain.repositories

import com.jaffetvr.syncbid.features.users.domain.entities.Auction
import com.jaffetvr.syncbid.features.users.domain.entities.Bid
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de subastas.
 * El dominio NO conoce ningún framework (Retrofit, Room, etc.).
 */
interface AuctionRepository {

    /** Observa todas las subastas desde la fuente de verdad (Room). */
    fun observeAuctions(): Flow<List<Auction>>

    /** Observa una subasta específica por ID. */
    fun observeAuctionById(id: String): Flow<Auction?>

    /** Refresca las subastas desde el servidor y las guarda en Room. */
    suspend fun refreshAuctions(): Result<Unit>

    /** Envía una puja al servidor. */
    suspend fun placeBid(auctionId: String, amount: Double): Result<Bid>

    /** Inicia la conexión WebSocket para actualizaciones en tiempo real. */
    fun startRealTimeUpdates(): Flow<Unit>
}
