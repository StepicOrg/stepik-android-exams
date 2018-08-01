package org.stepik.android.exams.ui.steps

import android.app.Activity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.attempt_container_layout.view.*
import kotlinx.android.synthetic.main.view_table_quiz_layout.view.*
import org.stepik.android.exams.R
import org.stepik.android.exams.core.presenter.contracts.AttemptView
import org.stepik.android.exams.data.model.Reply
import org.stepik.android.exams.data.model.Step
import org.stepik.android.exams.data.model.TableChoiceAnswer
import org.stepik.android.exams.data.model.attempts.Attempt
import org.stepik.android.exams.ui.adapter.TableChoiceAdapter
import org.stepik.android.exams.ui.decorations.GridDividerItemDecoration
import org.stepik.android.exams.util.DpPixelsHelper
import java.util.*

class TableChoiceDelegate(
        step: Step
) : StepAttemptDelegate(step), AttemptView {

    private lateinit var recyclerContainer: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var adapter: TableChoiceAdapter

    override fun onCreateView(parent: ViewGroup): View {
        val parentContainer = super.onCreateView(parent)
        val tableQuizView = LayoutInflater.from(parent.context).inflate(R.layout.view_table_quiz_layout, parent, false)
        val dp8inPx: Int = DpPixelsHelper.convertDpToPixel(8f).toInt()
        parentContainer.attempt_container.setPadding(0, dp8inPx, 0, dp8inPx)
        parentContainer.attempt_container.addView(tableQuizView)
        recyclerContainer = parentContainer.attempt_container.recyclerTable
        recyclerContainer.isNestedScrollingEnabled = false
        recyclerContainer.addItemDecoration(GridDividerItemDecoration(parent.context))
        return parentContainer
    }

    override fun onViewCreated(view: View) {
        super.onViewCreated(view)
        context = view.context as Activity
    }

    override fun onNeedShowAttempt(attempt: Attempt?) {
        super.onNeedShowAttempt(attempt)
        showAttempt(attempt)
    }


    override fun showAttempt(attempt: Attempt?) {
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

    override fun generateReply(): Reply =
            Reply(tableChoices = adapter.answers)


    override fun blockUIBeforeSubmit(needBlock: Boolean) {
        adapter.setAllItemsEnabled(!needBlock)
    }

    override fun onRestoreSubmission() {
        val choices = submissions?.reply?.tableChoices ?: return
        adapter.answers = choices
    }

}