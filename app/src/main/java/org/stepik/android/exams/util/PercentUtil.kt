package org.stepik.android.exams.util

object PercentUtil {
    fun formatPercent(first : Float, second : Float) =
            ((first / second) * 100).toInt()
    fun printPercent(percent : Int) =
            percent.toString() + "%"
}