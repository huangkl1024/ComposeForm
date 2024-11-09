package com.github.huangkl1024.composeform.formatter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern


private const val shortDateFormatPattern = "yy/MM/dd"

@OptIn(FormatStringsInDatetimeFormats::class)
private val shortDateFormat = LocalDate.Format {
    byUnicodePattern(shortDateFormatPattern)
}

fun fomatDateShort(r: LocalDate?): String {
    return if (r != null) shortDateFormat.format(r) else ""
}

private const val longDateFormatPattern = "yyyy-MM-dd"

@OptIn(FormatStringsInDatetimeFormats::class)
private val longDateFormat = LocalDate.Format {
    byUnicodePattern(longDateFormatPattern)
}

fun formatDateLong(r: LocalDate?): String {
    return if (r != null) longDateFormat.format(r) else ""
}