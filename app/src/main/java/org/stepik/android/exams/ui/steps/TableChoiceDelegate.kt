package org.stepik.android.exams.ui.steps

import android.app.Activity
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.view_table_quiz_layout.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.TableChoiceAnswer
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.ui.adapter.TableChoiceAdapter
import org.stepik.android.exams.ui.decorations.GridDividerItemDecoration
import org.stepik.android.exams.util.DpPixelsHelper
import java.util.*

class TableChoiceDelegate: AttemptDelegate(){
    override var isEnabled: Boolean
    get() = recyclerContainer.isEnabled
    set(value) {
        recyclerContainer.isEnabled = value
    }
    override var actionButton: Button? = null

    private lateinit var recyclerContainer: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: TableChoiceAdapter
    private lateinit var context : Activity

    override fun onCreateView(parent: ViewGroup): View {
        val tableQuizView = LayoutInflater.from(parent.context).inflate(R.layout.view_table_quiz_layout, parent, false)
        val dp8inPx: Int = DpPixelsHelper.convertDpToPixel(8f).toInt()
        parent.attempt_container.setPadding(0, dp8inPx, 0, dp8inPx)
        recyclerContainer = tableQuizView.recyclerTable
        recyclerContainer.isNestedScrollingEnabled = false
        recyclerContainer.addItemDecoration(GridDividerItemDecoration(parent.context))
        context = parent.context as Activity
        return tableQuizView
    }

    override fun setAttempt(attempt: Attempt?) {
        val dataset = attempt?.dataset ?: return
        val rows = dataset.rows ?: return
        val columns = dataset.columns ?: return
        val description = dataset.description ?: return
        val isCheckbox = dataset.isCheckbox

        val answerList = initAnswerListFromAttempt(rows, columns)

        gridLayoutManager = GridLayoutManager(context, rows.size + 1, GridLayoutManager.HORIZONTAL, false)
        adapter = TableChoiceAdapter(context, rows, columns, description, isCheckbox, answerList)
        recyclerContainer.layoutManager = gridLayoutManager
        recyclerContainer.adapter = adapter
    }

    private fun initAnswerListFromAttempt(rows: List<String>, columns: List<String>): ArrayList<TableChoiceAnswer> {
        val result = ArrayList<TableChoiceAnswer>(rows.size)
        for (nameRow in rows) {
            val oneRowAnswer = ArrayList<TableChoiceAnswer.Cell>(columns.size)
            for (nameColumn in columns) {
                oneRowAnswer.add(TableChoiceAnswer.Cell(nameColumn, false))
            }
            result.add(TableChoiceAnswer(nameRow, oneRowAnswer))
        }
        return result
    }

    override fun createReply() =
    Reply(tableChoices = adapter.answers)

    override fun setSubmission(submission: Submission?) {
        val choices = submission?.reply?.tableChoices ?: return
        adapter.answers = choices
    }
}