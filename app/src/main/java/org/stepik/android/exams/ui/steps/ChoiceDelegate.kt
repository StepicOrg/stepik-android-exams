package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.view_choice_attempt.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.ui.adapter.StepikRadioGroupAdapter

class ChoiceDelegate : AttemptDelegate() {

    private lateinit var choiceAdapter: StepikRadioGroupAdapter

    override var isEnabled: Boolean = false
        set(value) = choiceAdapter.setEnabled(value)

    override var actionButton: Button?
        get() = choiceAdapter.actionButton
        set(value) {
            choiceAdapter.actionButton = value
        }

    override fun onCreateView(parent: ViewGroup): View =
            LayoutInflater.from(parent.context).inflate(R.layout.view_choice_attempt, parent, false)

    override fun onViewCreated(view: View) {
        choiceAdapter = StepikRadioGroupAdapter(view.choice_container)
    }

    override fun setSubmission(submission: Submission?) = choiceAdapter.setSubmission(submission)
    override fun setAttempt(attempt: Attempt?) = choiceAdapter.setAttempt(attempt)
    override fun createReply() = choiceAdapter.reply
}