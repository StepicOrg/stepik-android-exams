package org.stepik.android.exams.util

import android.content.res.Configuration
import android.os.Build
import android.text.Html
import android.text.Spanned
import java.util.*

fun fromHtmlCompat(html: String): Spanned = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
    @Suppress("DEPRECATION")
    Html.fromHtml(html)
} else {
    Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
}

val Configuration.defaultLocale: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales.get(0)
    } else {
        @Suppress("DEPRECATION")
        locale
    }