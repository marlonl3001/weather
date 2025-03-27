package br.com.mdr.weather.commons

import android.content.Context
import android.util.Log
import androidx.core.R
import java.lang.reflect.Field

fun Context.getResId(resName: String): Int {
    try {
        return resources.getIdentifier(
            "r$resName",
            "drawable",
            packageName
        )
    } catch (e: Exception) {
        e.printStackTrace()
        return -1
    }
}