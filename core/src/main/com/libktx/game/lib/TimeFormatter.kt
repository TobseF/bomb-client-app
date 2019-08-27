package com.libktx.game.lib

import java.text.SimpleDateFormat
import java.util.*

object TimeFormatter {

    private val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH:mm.SSS", Locale.ENGLISH)
    private val timeFormat = SimpleDateFormat("HH:mm ss", Locale.ENGLISH)

    /**
     * @param time in ms. Max is 99:99 [mm:ss]
     * @return formatted time as mm:ss
     */
    fun getFormattedTimeAsString(time: ms): String {
        val totalSeconds = time / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds - (minutes * 60)
        fun twoDigits(number: ms) = if (number < 10) "0$number" else number.toString()
        return twoDigits(minutes) + ":" + twoDigits(seconds)
    }


    /**
     * Formats a [Date] to a detailed console date message.
     * @return e.g. 2019.11.24.16:30.456
     */
    fun getFormattedDateAsConsoleString(date: Date): String = dateFormat.format(date)

    /**
     * Formats a [Date] to a readable time stamp
     * @return e.g. 16:30 45
     */
    fun getFormattedDateAsTimeString(date: Date): String = timeFormat.format(date)

}