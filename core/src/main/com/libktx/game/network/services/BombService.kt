package com.libktx.game.network.services

import com.libktx.game.Config
import com.libktx.game.Preferences
import com.libktx.game.lib.TimeFormatter
import com.libktx.game.network.NetworkEvent.EventType
import com.libktx.game.network.NetworkEvent.EventType.POST
import kotlinx.coroutines.launch
import ktx.async.HttpRequestResult
import ktx.async.KtxAsync
import ktx.async.httpRequest
import ktx.async.newSingleThreadAsyncContext
import ktx.log.logger

private val log = logger<BombService>()

/**
 * Access the external 7-segment timer.
 */
class BombService {
    companion object {
        const val CONTENT_TYPE_TEXT = "text/plain; charset=utf-8"
    }

    /**
     * Connection timeout in milliseconds
     */
    private val timeOut = 5_000

    private val executor = newSingleThreadAsyncContext()

    private fun getPath(endpoint: String) = "http://${Preferences.bombIp}:${Config.ServerPort}/$endpoint"

    fun reset() {
        val path = getPath("reset")
        val content = "TURN IT OFF AND ON AGAIN;" + Preferences.bombTimer
        log.info { "Reset bomb... ($path):\n$content" }
        asyncHttpRequest(path, POST, content)
    }

    fun stop() {
        val path = getPath("stop")
        val content = "BACK TO THE FUTURE"
        log.info { "Stopping bomb... ($path):\n$content" }
        asyncHttpRequest(path, POST, content)
    }


    fun info(info: (BombInfo?) -> Unit) {
        val path = getPath("info")
        val content = "TELL ME SWEET LITTLE LIES"
        log.info { "Reading info from bomb... ($path):\n$content" }

        KtxAsync.launch(executor) {
            val response = httpRequestResult(url = path, content = content, type = POST)
            if (response.statusCode != 200) {
                log.error { "Failed access bomb on url (code: ${response.statusCode}): $path" }
            }
            val responseData = response.contentAsString
            val values = responseData.split(";")
            if (values.size != 2 || !values[0].contains("screen") || !values[1].contains("finish")) {
                log.error { "Wrong data format of info: `$responseData`" }
                info.invoke(null)
            } else {
                val screen = values[0].substringAfter("screen:")
                val finish = values[1].substringAfter("finish:").toLongOrNull()
                if (finish == null || screen.isEmpty()) {
                    log.error { "Wrong data format of info: `$responseData`" }
                    info.invoke(null)
                } else {
                    log.info { "Bomb State: $screen - time to explode: " + TimeFormatter.getFormattedTimeAsString(finish) }
                    info.invoke(BombInfo(screen, finish))
                }
            }
        }

    }

    data class BombInfo(val screen: String, val time: Long) {
        override fun toString(): String {
            return "$screen - " + TimeFormatter.getFormattedTimeAsString(time)
        }
    }

    private fun asyncHttpRequest(url: String, type: EventType, content: String? = null) {
        KtxAsync.launch(executor) {
            try {
                val response = httpRequestResult(url, content, type)
                if (response.statusCode != 200) {
                    log.error { "Failed access bomb on url (code: ${response.statusCode}): $url" }
                }
            } catch (e: Exception) {
                log.error(e) { "Failed access bomb on url: $url[$content]" }
            }
        }
    }

    private suspend fun httpRequestResult(url: String, content: String?, type: EventType): HttpRequestResult {
        val headers = mapOf(Pair("Content-Type", CONTENT_TYPE_TEXT))
        return httpRequest(url = url, content = content, method = type.toString(), headers = headers, timeout = timeOut)
    }

}