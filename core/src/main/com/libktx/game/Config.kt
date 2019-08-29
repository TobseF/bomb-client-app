package com.libktx.game

/**
 * Configuration constants
 */
object Config {
    const val ServerPort = 5000
    const val TimerPort = 5001
    /**
     * Time in minutes of a game
     */
    const val countdownTime = 20

    const val appIdentifier = "de.its.game.bomb-client"

    val screenSize = Rect(800, 480)

    data class Rect(val width: Float, val height: Float) {
        constructor(width: Int, height: Int) : this(width.toFloat(), height.toFloat())
    }

}
