package com.example.ui.util

/**
 * High-performance time formatting utilities that avoid regex pattern parsing
 * and String.format allocations during high-frequency timer ticks.
 */
object FastTimeFormatter {

    /**
     * Formats minutes and seconds as MM:SS (e.g. "05:09", "45:00")
     */
    fun formatMmSs(minutes: Int, seconds: Int): String {
        val m = if (minutes in 0..9) "0$minutes" else minutes.toString()
        val s = if (seconds in 0..9) "0$seconds" else seconds.toString()
        return "$m:$s"
    }

    /**
     * Formats hours, minutes, and seconds as HH:MM:SS (e.g. "01:25:30")
     */
    fun formatHhMmSs(hours: Int, minutes: Int, seconds: Int): String {
        val h = if (hours in 0..9) "0$hours" else hours.toString()
        val m = if (minutes in 0..9) "0$minutes" else minutes.toString()
        val s = if (seconds in 0..9) "0$seconds" else seconds.toString()
        return "$h:$m:$s"
    }

    /**
     * Formats total seconds into HH:MM:SS (if hours > 0) or MM:SS
     */
    fun formatSeconds(totalSeconds: Int): String {
        val s = totalSeconds.coerceAtLeast(0)
        val hours = s / 3600
        val minutes = (s % 3600) / 60
        val seconds = s % 60
        return if (hours > 0) {
            formatHhMmSs(hours, minutes, seconds)
        } else {
            formatMmSs(minutes, seconds)
        }
    }
}
