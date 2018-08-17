package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.stepik.android.exams.R
import org.stepik.android.exams.util.initCenteredToolbar
import org.stepik.android.model.Block
import org.stepik.android.model.Lesson
import org.stepik.android.model.Step

class AdaptiveOnboardingFragment : Fragment() {
    private val adapter = OnboardingQuizCardsAdapter {
        if (it == 0) onOnboardingCompleted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initOnboardingCards()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater?.inflate(R.layout.fragment_adaptive_onboarding, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardsContainer.setAdapter(adapter)
        initCenteredToolbar(R.string.adaptive_onboarding_title, showHomeButton = true, homeIndicatorRes = getCloseIconDrawableRes())
    }

    private fun initOnboardingCards() {
        adapter.add(createMockCard(-1, R.string.adaptive_onboarding_card_title_1, R.string.adaptive_onboarding_card_question_1))
        adapter.add(createMockCard(-2, R.string.adaptive_onboarding_card_title_2, R.string.adaptive_onboarding_card_question_2))
        adapter.add(createMockCard(-3, R.string.adaptive_onboarding_card_title_3, R.string.adaptive_onboarding_card_question_3))
    }

    private fun createMockCard(id: Long, @StringRes title_id: Int, @StringRes question_id: Int) : Card {
        val lesson = Lesson()
        lesson.title = getString(title_id)

        val step = Step()
        val block = Block()
        block.text = getString(question_id)
        step.block = block

        return Card(id, 0, lesson, step, Attempt())
    }

    private fun onOnboardingCompleted() {
        activity?.onBackPressed()
    }

    override fun onDestroyView() {
        adapter.detach()
        super.onDestroyView()
    }

    override fun onDestroy() {
        adapter.destroy()
        super.onDestroy()
    }
}