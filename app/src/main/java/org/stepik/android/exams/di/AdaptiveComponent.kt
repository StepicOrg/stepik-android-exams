package org.stepik.android.exams.di

import dagger.BindsInstance
import dagger.Subcomponent
import org.stepik.android.exams.adaptive.core.presenter.CardPresenter
import org.stepik.android.exams.adaptive.model.Card
import org.stepik.android.exams.adaptive.ui.activity.AdaptiveCourseActivity
import org.stepik.android.exams.adaptive.ui.adapter.QuizCardViewHolder
import org.stepik.android.exams.di.qualifiers.CourseId

@Subcomponent
interface AdaptiveComponent {
        @Subcomponent.Builder
        interface Builder {
            fun build(): AdaptiveComponent

            @BindsInstance
            fun courseId(@CourseId courseId: Long): Builder
        }

        fun inject(adaptiveCourseActivity: AdaptiveCourseActivity)
        fun inject(card: Card)
        fun inject(cardPresenter: CardPresenter)
}