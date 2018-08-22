package org.stepik.android.exams.di

import dagger.Subcomponent
import org.stepik.android.exams.adaptive.core.presenter.CardPresenter
import org.stepik.android.exams.adaptive.model.Card
import org.stepik.android.exams.adaptive.ui.activity.AdaptiveCourseActivity

@Subcomponent
interface AdaptiveComponent {
        @Subcomponent.Builder
        interface Builder {
            fun build(): AdaptiveComponent
        }

        fun inject(adaptiveCourseActivity: AdaptiveCourseActivity)
        fun inject(card: Card)
        fun inject(cardPresenter: CardPresenter)
}