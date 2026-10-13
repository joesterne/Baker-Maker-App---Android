package com.example

import com.example.ui.util.FastTimeFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class FastTimeFormatterTest {

    @Test
    fun testFormatMmSs() {
        assertEquals("00:00", FastTimeFormatter.formatMmSs(0, 0))
        assertEquals("05:09", FastTimeFormatter.formatMmSs(5, 9))
        assertEquals("45:00", FastTimeFormatter.formatMmSs(45, 0))
        assertEquals("59:59", FastTimeFormatter.formatMmSs(59, 59))
    }

    @Test
    fun testFormatHhMmSs() {
        assertEquals("00:00:00", FastTimeFormatter.formatHhMmSs(0, 0, 0))
        assertEquals("01:05:09", FastTimeFormatter.formatHhMmSs(1, 5, 9))
        assertEquals("02:30:15", FastTimeFormatter.formatHhMmSs(2, 30, 15))
        assertEquals("12:00:00", FastTimeFormatter.formatHhMmSs(12, 0, 0))
    }

    @Test
    fun testFormatSeconds() {
        assertEquals("00:00", FastTimeFormatter.formatSeconds(0))
        assertEquals("00:45", FastTimeFormatter.formatSeconds(45))
        assertEquals("22:30", FastTimeFormatter.formatSeconds(1350))
        assertEquals("45:00", FastTimeFormatter.formatSeconds(2700))
        assertEquals("01:15:00", FastTimeFormatter.formatSeconds(4500))
        assertEquals("03:45:12", FastTimeFormatter.formatSeconds(3 * 3600 + 45 * 60 + 12))
    }
}
