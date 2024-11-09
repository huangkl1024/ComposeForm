package com.github.huangkl1024.composeform.formatter

import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern


private const val timeFormatPattern = "HH:mm"

@OptIn(FormatStringsInDatetimeFormats::class)
private val timeFormat = LocalTime.Format {
    byUnicodePattern(timeFormatPattern)
}

fun formatTime(r: LocalTime?): String {
    return if (r != null) timeFormat.format(r) else ""
}