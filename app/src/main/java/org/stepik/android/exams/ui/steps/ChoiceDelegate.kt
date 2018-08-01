package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.view_choice_attempt.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.ui.adapter.StepikRadioGroupAdapter
import org.stepik.android.exams.ui.custom.StepikRadioGroup

class ChoiceDelegate(
        step: Step?
) : StepAttemptDelegate(step), AttemptView {

    private lateinit var choiceAdapter: StepikRadioGroupAdapter

    override var actionButton: Button?
        get() = choiceAdapter.actionButton
        set(value) {
            choiceAdapter.actionButton = value
        }

    override fun onCreateView(parent: ViewGroup): View {
        val parentContainer = super.onCreateView(parent) as ViewGroup
        val view: StepikRadioGroup = LayoutInflater.from(parent.context).inflate(R.layout.view_choice_attempt, parent, false) as StepikRadioGroup
        attemptContainer.addView(view)
        choiceAdapter = StepikRadioGroupAdapter(view.choice_container)
        choiceAdapter.actionButton = view.findViewById(R.id.stepAttemptSubmitButton)
        return parentContainer
    }

    override fun showAttempt(attempt: Attempt?) = onNeedShowAttempt(attempt)
    override fun setSubmission(submission: Submission?) {
        super.setSubmission(submission)
        choiceAdapter.setSubmission(submission)
    }

    override fun onNeedShowAttempt(attempt: Attempt?) {
        super.onNeedShowAttempt(attempt)
        choiceAdapter.setAttempt(attempt)
    }

    override fun blockUIBeforeSubmit(needBlock: Boolean) {
        choiceAdapter.setEnabled(needBlock)
    }

    override fun generateReply() = choiceAdapter.reply
}