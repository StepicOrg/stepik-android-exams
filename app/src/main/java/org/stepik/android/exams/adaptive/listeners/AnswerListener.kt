package org.stepik.android.exams.adaptive.listeners

interface AnswerListener {
    fun onCorrectAnswer(submissionId: Long)
    fun onWrongAnswer(submissionId: Long)
}