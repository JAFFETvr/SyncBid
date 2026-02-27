package com.jaffetvr.syncbid.features.users.data.datasource.remote

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.AuctionUpdateDto
import com.jaffetvr.syncbid.features.users.data.datasource.remote.model.BidUpdateData
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
 * Cliente WebSocket que usa protocolo STOMP para suscribirse a eventos de subastas.
 *
 * El servidor (WebSocketConfig.java) expone:
 *   - Endpoint: ws://10.0.2.2:8080/ws/auctions
 *   - Mensajes publicados en: /topic/auctions/{auctionId}
 *
 * Formato de mensajes (AuctionUpdatePayload.java):
 *   NEW_BID:         { type: "NEW_BID", data: { id, amount, bidderUsername, createdAt }, message: "..." }
 *   AUCTION_FINISHED:{ type: "AUCTION_FINISHED", data: "winnerUsername", message: "..." }
 */
@Singleton
class AuctionWebSocketDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val gson: Gson
) {
    companion object {
        // ws:// porque el servidor NO tiene SSL en desarrollo local
        private const val WS_BASE_URL = "ws://10.0.2.2:8080/ws/auctions"
    }

    private var webSocket: WebSocket? = null

    /**
     * Se suscribe a eventos de una subasta específica.
     * Emite AuctionUpdateDto por cada evento recibido del servidor.
     */
    fun observeAuction(auctionId: String): Flow<AuctionUpdateDto> = callbackFlow {
        val request = Request.Builder()
            .url(WS_BASE_URL)
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Protocolo STOMP: enviar CONNECT luego SUBSCRIBE
                webSocket.send(buildStompConnect())
                webSocket.send(buildStompSubscribe(auctionId))
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    // Los frames STOMP llegan como texto plano
                    if (text.startsWith("MESSAGE")) {
                        val body = extractStompBody(text)
                        if (body.isNotEmpty()) {
                            val json = JsonParser.parseString(body).asJsonObject
                            val update = parseAuctionUpdate(json)
                            if (update != null) trySend(update)
                        }
                    }
                    // CONNECTED, RECEIPT, ERROR — ignoramos silenciosamente
                } catch (e: Exception) {
                    // No rompemos el Flow por errores de parsing
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
            webSocket?.close(1000, "Pantalla cerrada")
            webSocket = null
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Desconectado")
        webSocket = null
    }

    // ─── Helpers STOMP ──────────────────────────────────────────────────────

    private fun buildStompConnect(): String =
        "CONNECT\naccept-version:1.2\nheart-beat:0,0\n\n\u0000"

    private fun buildStompSubscribe(auctionId: String): String =
        "SUBSCRIBE\nid:sub-$auctionId\ndestination:/topic/auctions/$auctionId\n\n\u0000"

    /**
     * Extrae el body JSON de un frame STOMP MESSAGE.
     * Un frame STOMP tiene headers separados del body por una línea en blanco.
     */
    private fun extractStompBody(frame: String): String {
        val bodyStart = frame.indexOf("\n\n")
        if (bodyStart == -1) return ""
        return frame.substring(bodyStart + 2).trimEnd('\u0000')
    }

    /**
     * Parsea el JSON del payload a AuctionUpdateDto.
     * Maneja tanto NEW_BID (data = objeto) como AUCTION_FINISHED (data = string).
     */
    private fun parseAuctionUpdate(json: JsonObject): AuctionUpdateDto? {
        return try {
            val type = json.get("type")?.asString ?: return null
            val message = json.get("message")?.asString

            val data: Any? = when (type) {
                "NEW_BID" -> {
                    val dataElement = json.get("data")
                    if (dataElement != null && dataElement.isJsonObject) {
                        gson.fromJson(dataElement, BidUpdateData::class.java)
                    } else null
                }
                "AUCTION_FINISHED" -> {
                    json.get("data")?.asString
                }
                else -> null
            }

            AuctionUpdateDto(type = type, message = message, data = data)
        } catch (e: Exception) {
            null
        }
    }
}