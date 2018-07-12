package org.stepik.android.exams.core

import android.app.Activity
import android.content.Context

interface ScreenManager {
    fun showOnboardingScreen()
    fun startStudy()
    fun showEmptyAuthScreen(context: Context)
    fun showLoginScreen(activity: Activity)
    fun showRegisterScreen(activity: Activity)
    fun showCourse(id : String)
}