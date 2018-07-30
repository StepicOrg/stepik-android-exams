package org.stepik.android.exams.ui.steps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
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
) : StepAttemptDelegate(step), QuizDelegate, AttemptView {
    private lateinit var choiceAdapter: StepikRadioGroupAdapter

    override var isEnabled: Boolean = false
        set(value) = choiceAdapter.setEnabled(value)

    override var actionButton: Button?
        get() = choiceAdapter.actionButton
        set(value) {
            choiceAdapter.actionButton = value
        }

    override fun onCreateView(parent: ViewGroup): View {
        val parentContainer = super.onCreateView(parent) as ViewGroup
        val view: StepikRadioGroup = LayoutInflater.from(parent.context).inflate(R.layout.view_choice_attempt, parent, false) as StepikRadioGroup
        parentContainer.attempt_container.addView(view)
        parentContainer.attempt_container.visibility = View.VISIBLE
        return parentContainer
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        choiceAdapter = StepikRadioGroupAdapter(view.choice_container)
        choiceAdapter.actionButton = view.findViewById(R.id.stepAttemptSubmitButton)
        stepAttemptPresenter.attachView(this)
        startLoading(step)
    }

    override fun setSubmission(submission: Submission?) = choiceAdapter.setSubmission(submission)
    override fun trySetAttempt(attempt: Attempt?) = choiceAdapter.setAttempt(attempt)
    override fun createReply() = choiceAdapter.reply
}