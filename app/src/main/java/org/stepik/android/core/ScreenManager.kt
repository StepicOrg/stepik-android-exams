package org.stepik.android.adaptive.core

import android.app.Activity
import android.content.Context

interface ScreenManager {
    fun showOnboardingScreen()
    fun startStudy()
    fun showEmptyAuthScreen(context: Context)
    fun showImage(context: Context, path: String)
    fun showStatsScreen(context: Context, page: Int)
    fun showQuestionsPacksScreen(context: Context)
    fun showGamificationDescription(context: Context)

    fun showLoginScreen(activity: Activity)
    fun showRegisterScreen(activity: Activity)
}