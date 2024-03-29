package com.libktx.game.lib

import kotlin.math.max

class Countdown(private val minutes: Int = 0, private val seconds: Int = 0) : Resetable {

    private var finish = getEndTime()
    private var stoppedTime: Long? = null

    fun getCountdownTime(): ms {
        return getCountdownTime(stoppedTime ?: System.currentTimeMillis())
    }

    fun getContdownTimeSeconds() = getCountdownTime() / 1000

    private fun getCountdownTime(currentTime: ms): Long {
        val current = finish - currentTime
        return max(current, 0)
    }

    fun getTime(): Long = stoppedTime ?: System.currentTimeMillis()

    private fun getEndTime() = System.currentTimeMillis() + (minutes * 60 + seconds) * 1000

    fun isFinished() = getCountdownTime() == 0L

    fun isNotFinished() = !isFinished()

    fun stop() {
        stoppedTime = System.currentTimeMillis()
    }

    override fun reset() {
        finish = getEndTime()
        stoppedTime = null
    }

}
