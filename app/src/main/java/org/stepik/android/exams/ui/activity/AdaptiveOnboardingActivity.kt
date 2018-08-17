package org.stepik.android.exams.ui.activity

import android.view.MenuItem
import org.stepik.android.exams.App
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.ui.fragment.AdaptiveOnboardingFragment
import javax.inject.Inject

class AdaptiveOnboardingActivity
@Inject
constructor(
        private val sharedPreferenceHelper: SharedPreferenceHelper
) : SingleFragmentActivity() {
    init {
        App.componentManager().adaptiveComponent.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        sharedPreferenceHelper.isFirstTimeAdaptive = true
        super.onBackPressed()
    }


    override fun createFragment() = AdaptiveOnboardingFragment()
}