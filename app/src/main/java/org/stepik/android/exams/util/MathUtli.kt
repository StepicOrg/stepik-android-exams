package org.stepik.android.exams.util

import java.util.*

object MathUtli {

    private val random = Random()

    fun randomBetween(min: Int, max: Int) = random.nextInt(max - min) + min

}