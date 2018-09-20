package org.stepik.android.exams.util

object TimeUtil {
    private const val SECOND = 1000
    private const val MINUTE = SECOND * 60
    private const val HOUR = MINUTE * 60
    fun getTimeToCompleteFormatted(time: Long): String {
        return (time % HOUR / MINUTE).toInt().toString() + " минут"
    }


}
