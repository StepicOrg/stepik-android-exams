package org.stepik.android.exams.ui.adapter

import android.widget.Button
import org.stepik.android.exams.App
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.ui.custom.StepikCheckBox
import org.stepik.android.exams.ui.custom.StepikOptionView
import org.stepik.android.exams.ui.custom.StepikRadioButton
import org.stepik.android.exams.ui.custom.StepikRadioGroup
import java.lang.Math.min


class StepikRadioGroupAdapter(private val group: StepikRadioGroup) {
    init {
        App.component().inject(this)
    }

    var actionButton: Button? = null
        set(value) {
            field = value
            refreshActionButton()
        }

    private var isMultipleChoice = false

    fun setAttempt(attempt: Attempt?) {
        val dataset = attempt?._dataset
        dataset?.options?.let { options ->
            if (options.isEmpty()) return
            group.removeAllViews()
            group.clearCheck()

            isMultipleChoice = dataset.isMultipleChoice
            options.forEach {
                val item = if (isMultipleChoice) {
                    StepikCheckBox(group.context)
                } else {
                    StepikRadioButton(group.context)
                }
                item.setText(it)
                group.addView(item)
            }

            if (!isMultipleChoice) {
                group.setOnCheckedChangeListener { _, _ ->
                    refreshActionButton()
                    group.setOnCheckedChangeListener(null)
                }
            }

            refreshActionButton()
        }
    }

    fun setSubmission(submission: Submission?) {
        submission?.reply?.choices?.let { choices ->
            setChoices(choices)
        }
    }

    fun setChoices(choices: List<Boolean>) {
        (0 until min(group.childCount, choices.size)).forEach {
            (group.getChildAt(it) as StepikOptionView).isChecked = choices[it]
        }
    }

    val reply: Reply
        get() {
            val selection = (0 until group.childCount)
                    .map {
                        (group.getChildAt(it) as StepikOptionView).isChecked
                    }
            return Reply(choices = selection)
        }

    fun setEnabled(isEnabled: Boolean) {
        (0 until group.childCount).forEach { group.getChildAt(it).isEnabled = isEnabled }
    }

    private fun refreshActionButton() {
        actionButton?.let {
            it.isEnabled = isMultipleChoice || group.checkedRadioButtonId != -1
        }
    }
}