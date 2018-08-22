package org.stepik.android.exams.util

import java.util.Random

object MathUtli {

    private val random = Random()

    fun randomBetween(min: Int, max: Int) = random.nextInt(max - min) + min

}