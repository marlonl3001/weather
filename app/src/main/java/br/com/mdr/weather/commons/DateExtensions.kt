package br.com.mdr.weather.commons

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val ISO8601_FORMAT = "yyyy-MM-dd HH:mm:ss"

object ISO8601Formatter : SimpleDateFormat(ISO8601_FORMAT, Locale.getDefault())

fun String.asIso8601Date(): Date? {
    return try {
        ISO8601Formatter.parse(this)
    } catch (e: ParseException) {
        null
    }
}
