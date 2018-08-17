package org.stepik.android.exams.di

import dagger.Subcomponent
import org.stepik.android.exams.ui.activity.AdaptiveOnboardingActivity
import org.stepik.android.exams.ui.custom.LatexSupportableWebView

@Subcomponent
interface AdaptiveComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): AdaptiveComponent
    }

    fun inject(adaptiveOnboardingActivity: AdaptiveOnboardingActivity)
}