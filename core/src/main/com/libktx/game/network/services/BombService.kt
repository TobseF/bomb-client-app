package com.libktx.game.network.services

import com.libktx.game.Config
import com.libktx.game.Preferences
import com.libktx.game.lib.TimeFormatter
import com.libktx.game.network.NetworkEvent.EventType
import com.libktx.game.network.NetworkEvent.EventType.POST
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.httpRequest
import ktx.async.newSingleThreadAsyncContext
import ktx.log.logger

private val log = logger<BombService>()

/**
 * Access the external 7-segment timer.
 */
class BombService {

    private val executor = newSingleThreadAsyncContext()

    private fun getPath(endpoint: String) = "http://${Preferences.bombIp}:${Config.ServerPort}/$endpoint"

    fun reset() {
        log.info { "Reset bomb with ${Preferences.bombTimer}min" }
        val path = getPath("reset")
        asyncHttpRequest(path, POST, "TURN IT OFF AND ON AGAIN;" + Preferences.bombTimer)
    }

    fun stop() {
        val path = getPath("stop")
        asyncHttpRequest(path, POST, "BACK TO THE FUTURE")
    }


    fun info(info: (BombInfo) -> Unit) {
        val path = getPath("info")
        log.info { "Reading info from bomb..." }

        KtxAsync.launch(executor) {
            val response = httpRequest(url = path, content = "TELL ME SWEET LITTLE LIES", method = POST.toString())
            if (response.statusCode != 200) {
                log.error { "Failed access timer on url (code: ${response.statusCode}): $path" }
            }
            val values = response.contentAsString.split(";")
            val screen = values[0].substringAfter("screen:")
            val finish = values[1].substringAfter("finish:").toLong()
            log.info { "Bomb State: $screen - time to explode: " + TimeFormatter.getFormattedTimeAsString(finish) }
            info.invoke(BombInfo(screen, finish))
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
                val response = httpRequest(url = url, content = content, method = type.toString())
                if (response.statusCode != 200) {
                    log.error { "Failed access timer on url (code: ${response.statusCode}): $url" }
                }
            } catch (e: Exception) {
                log.error(e) { "Failed access timer on url: $url" }
            }
        }
    }

}