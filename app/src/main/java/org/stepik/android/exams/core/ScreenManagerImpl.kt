package org.stepik.android.exams.core


import android.app.Activity
import android.content.Context
import android.content.Intent
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.ui.activity.*

import javax.inject.Inject

@AppSingleton
class ScreenManagerImpl
@Inject
constructor(
        private val context: Context
): ScreenManager {
    override fun showCourse(id : String) {
        val intent = Intent(context, StudyActivity::class.java)
        intent.putExtra("id", id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    override fun startStudy() {
        val intent = Intent(context, ListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    override fun showOnboardingScreen() {
        val intent = Intent(context, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }


    override fun showEmptyAuthScreen(context: Context) {
        val intent = Intent(context, EmptyAuthActivity::class.java)
        context.startActivity(intent)
    }

    override fun showLoginScreen(activity: Activity) {
        activity.startActivityForResult(Intent(activity, LoginActivity::class.java), LoginActivity.REQUEST_CODE)
    }

    override fun showRegisterScreen(activity: Activity) {
        activity.startActivityForResult(Intent(activity, RegisterActivity::class.java), RegisterActivity.REQUEST_CODE)
    }
}
