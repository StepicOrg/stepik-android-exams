package org.stepik.android.exams.ui.activity
import org.stepik.android.exams.ui.fragment.OnboardingFragment


class IntroActivity : FragmentActivity() {
    override fun createFragment() = OnboardingFragment()
}