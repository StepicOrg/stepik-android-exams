package org.stepik.android.exams.ui.steps

import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.button_container.view.*
import org.stepik.android.exams.App
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.StepAttemptPresenter
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.util.resolvers.text.TextResolver
import javax.inject.Inject

open class StepAttemptDelegate(
         step: Step?
) : StepDelegate(step) {
    protected lateinit var attempt: Attempt
    @Inject
    lateinit var stepAttemptPresenter: StepAttemptPresenter
    @Inject
    lateinit var textResolver: TextResolver

    init {
        App.component().inject(this)
    }

    private fun setTextToActionButton(actionButton : Button, text: String) {
        actionButton.text = text
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        view.buttonsContainer.visibility = View.VISIBLE
        view.stepAttemptSubmitButton.visibility = View.VISIBLE
        setTextToActionButton(view.stepAttemptSubmitButton, view.resources.getString(R.string.submit))
    }

    protected open fun startLoading(step: Step?) {
        stepAttemptPresenter.createNewAttempt(step)
    }

    protected open fun showAttempt(attempt: Attempt) {
        this.attempt = attempt
    }

    protected open fun generateReply(): Reply {
        return Reply()
    }

    protected open fun blockUIBeforeSubmit(needBlock: Boolean) {}

    protected open fun onRestoreSubmission() {}

    protected open fun onPause() {}

}