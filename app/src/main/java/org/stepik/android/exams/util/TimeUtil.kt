package org.stepik.android.exams.util

import android.content.Context
import org.stepik.android.exams.App
import org.stepik.android.exams.R

object TimeUtil {
    private const val SECOND = 1
    private const val MINUTE = SECOND * 60
    fun getTimeToCompleteFormatted(seconds: Long, context: Context = App.getAppContext()): String {
        val time = (seconds / MINUTE).toInt()
        return context.resources.getQuantityString(R.plurals.minutes, time, time)
    }


}
