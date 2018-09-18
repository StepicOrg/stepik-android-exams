package org.stepik.android.exams.util.transformets

import org.stepik.android.exams.data.model.ProgressViewModel
import org.stepik.android.exams.util.AppConstants
import org.stepik.android.model.Progress
import java.util.*

fun Progress?.transformToViewModel(): ProgressViewModel? {
    val idOfProgress = this?.id
    if (this == null || idOfProgress == null) return null

    val scoreString: String
    val doubleScore: Double = java.lang.Double.parseDouble(this.score)
    if (doubleScore == Math.floor(doubleScore) && !java.lang.Double.isInfinite(doubleScore)) {
        scoreString = doubleScore.toInt().toString() + ""
    } else {
        scoreString = String.format(Locale.getDefault(), "%.2f", doubleScore)
    }


    val stringBuilder = StringBuilder()
    with(stringBuilder) {
        append(scoreString)
        append(AppConstants.DELIMITER_TEXT_SCORE)
        append(cost)
    }

    val textForScoreAndCost = stringBuilder.toString()


    return ProgressViewModel(idOfProgress, textForScoreAndCost, doubleScore.toInt(), this.cost)
}