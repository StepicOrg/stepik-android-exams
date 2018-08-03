package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt

class FreeAnswerDelegate : StringDelegate(){

    override fun onCreateView(parent: ViewGroup)=
            LayoutInflater.from(parent.context).inflate(R.layout.view_free_answer_attempt, parent, false) as EditText

    override fun setAttempt(attempt: Attempt?) =
            answerField.text.clear()

}