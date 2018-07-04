package org.stepik.android.exams.util

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.style.URLSpan
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

fun View.changeVisibillity(needShow: Boolean) {
    visibility = if (needShow) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun ViewGroup.hideAllChildren() {
    for (i in 0 until childCount) {
        getChildAt(i).changeVisibillity(false)
    }
}

fun Spanned.stripUnderlinesFromLinks(): Spanned {
    val spannable = SpannableString(this)
    val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)

    spans.forEach {
        val start = spannable.getSpanStart(it)
        val end = spannable.getSpanEnd(it)
        val flags = spannable.getSpanFlags(it)
        spannable.removeSpan(it)
        spannable.setSpan(URLSpanWithoutUnderline(it.url), start, end, flags)
    }
    return spannable
}

private class URLSpanWithoutUnderline(url: String) : URLSpan(url) {
    override fun updateDrawState(textPaint: TextPaint?) {
        super.updateDrawState(textPaint)
        textPaint?.isUnderlineText = false
    }
}

fun Activity.hideSoftKeyboard() {
    val view = currentFocus
    if (view != null) {
        val mgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
