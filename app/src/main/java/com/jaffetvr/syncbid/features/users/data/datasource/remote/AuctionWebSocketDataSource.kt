package com.jaffetvr.syncbid.features.users.data.datasource.remote

import com.google.gson.Gson
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.AuctionUpdateDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebSocket Data Source para recibir actualizaciones de subastas en tiempo real.
 *
 * Flujo de datos:
 * WebSocket → SharedFlow<AuctionUpdateDto> → Repository → Room (SSOT) → UI
 */
@Singleton
class AuctionWebSocketDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    companion object {
        private const val WS_URL = "wss://api.syncbid.com/v1/ws/auctions"
    }

    private var webSocket: WebSocket? = null

    /**
     * Conecta al WebSocket y emite actualizaciones como un Flow.
     * El Flow se cierra cuando se desconecta.
     */
    fun observeAuctionUpdates(): Flow<AuctionUpdateDto> = callbackFlow {
        val request = Request.Builder()
            .url(WS_URL)
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val update = gson.fromJson(text, AuctionUpdateDto::class.java)
                    trySend(update)
                } catch (e: Exception) {
                    // Log parsing error - no romper el flow
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                close(t)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                close()
            }
        })

        awaitClose {
            webSocket?.close(1000, "Desconectado")
            webSocket = null
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Desconectado")
        webSocket = null
    }
}
