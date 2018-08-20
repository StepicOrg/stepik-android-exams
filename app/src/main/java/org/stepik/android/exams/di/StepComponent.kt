package org.stepik.android.exams.di

import dagger.Subcomponent
import org.stepik.android.exams.core.interactor.NavigationInteractorImpl
import org.stepik.android.exams.ui.activity.StepsListActivity
import org.stepik.android.exams.ui.adapter.StepPagerAdapter
import org.stepik.android.exams.ui.adapter.StepikRadioGroupAdapter
import org.stepik.android.exams.ui.custom.LatexSupportableEnhancedFrameLayout
import org.stepik.android.exams.ui.custom.LatexSupportableWebView
import org.stepik.android.exams.ui.fragment.AttemptFragment
import org.stepik.android.exams.ui.fragment.StepFragment

@Subcomponent
interface StepComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): StepComponent
    }

    fun inject(latexSupportableWebView: LatexSupportableWebView)
    fun inject(latexSupportableEnhancedFrameLayout: LatexSupportableEnhancedFrameLayout)
    fun inject(stepPagerAdapter: StepPagerAdapter)
    fun inject(stepikRadioGroupAdapter: StepikRadioGroupAdapter)
    fun inject(attemptFragment: AttemptFragment)
    fun inject(stepFragment: StepFragment)
    fun inject(stepsListActivity: StepsListActivity)
    fun inject(navigationInteractorImpl: NavigationInteractorImpl)
}