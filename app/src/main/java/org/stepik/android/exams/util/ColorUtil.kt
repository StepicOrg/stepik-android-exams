package org.stepik.android.exams.util

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

import org.stepik.android.exams.App

object ColorUtil {
    @ColorInt
    fun getColorArgb(@ColorRes resourceColor: Int, context: Context = App.getAppContext()): Int {
        return ContextCompat.getColor(context, resourceColor)
    }
}
