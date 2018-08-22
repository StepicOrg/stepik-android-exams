package org.stepik.android.exams.adaptive.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import org.stepik.android.exams.R
import org.stepik.android.exams.adaptive.core.presenter.CardPresenter
import org.stepik.android.exams.adaptive.listeners.AdaptiveReactionListener
import org.stepik.android.exams.adaptive.listeners.AnswerListener
import org.stepik.android.exams.adaptive.model.Card
import org.stepik.android.exams.adaptive.ui.custom.QuizCardsContainer
import java.util.*


open class QuizCardsAdapter(
        private val listener: AdaptiveReactionListener?,
        private val answerListener: AnswerListener?
) : QuizCardsContainer.CardsAdapter<QuizCardViewHolder>() {

    companion object {
        @JvmStatic
        private fun changeVisibilityOfAllChildrenTo(viewGroup: ViewGroup, visibility: Int, exclude: List<Int>?) {
            val count = viewGroup.childCount
            (0 until count)
                    .map { viewGroup.getChildAt(it) }
                    .filterNot { exclude != null && exclude.contains(it.id) }
                    .forEach { it.visibility = visibility }
        }
    }

    private val presenters = ArrayList<CardPresenter>()

    fun destroy() = presenters.forEach(CardPresenter::destroy)

    /**
     * Method that detaches adapter from container
     */
    fun detach() {
        container = null // detach from container
        presenters.forEach(CardPresenter::detachView)
    }

    fun isCardExists(lessonId: Long) =
            presenters.any { it.card.lessonId == lessonId }

    override fun onCreateViewHolder(parent: ViewGroup) =
            QuizCardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adaptive_quiz_card_view, parent, false))

    override fun getItemCount() =
            presenters.size

    override fun onBindViewHolder(holder: QuizCardViewHolder, pos: Int) =
            holder.bind(presenters[pos])

    override fun onBindTopCard(holder: QuizCardViewHolder, pos: Int) =
            holder.onTopCard()

    override fun onPositionChanged(holder: QuizCardViewHolder, pos: Int) {
/*        val p = holder.cardView.layoutParams as FrameLayout.LayoutParams
        if (pos > 1) {
            p.height = QuizCardsContainer.CARD_OFFSET * 2
            changeVisibilityOfAllChildrenTo(holder.cardView, View.GONE, listOf(R.id.curtain))
        } else {
            p.height = FrameLayout.LayoutParams.MATCH_PARENT
            changeVisibilityOfAllChildrenTo(holder.cardView, View.VISIBLE, listOf(R.id.curtain))
        }
        holder.cardView.layoutParams = p*/
    }

    fun add(card: Card) {
        presenters.add(CardPresenter(card, listener, answerListener))
        onDataAdded()
    }

    fun isEmptyOrContainsOnlySwipedCard(lesson: Long) =
            presenters.isEmpty() || presenters.size == 1 && presenters[0].card.lessonId == lesson

    override fun poll() =
            presenters.removeAt(0).destroy()
}