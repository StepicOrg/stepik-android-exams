package org.stepik.android.adaptive.core


import android.app.Activity
import android.content.Context
import android.content.Intent

import org.stepik.android.adaptive.data.Analytics
import org.stepik.android.adaptive.di.AppSingleton
import org.stepik.android.adaptive.ui.activity.*
import javax.inject.Inject

@AppSingleton
class ScreenManagerImpl
@Inject
constructor(
        private val analytics: Analytics,
        private val context: Context
): ScreenManager {
    override fun showOnboardingScreen() {
        val intent = Intent(context, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun startStudy() {
        val intent = Intent(context, StudyActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    override fun showEmptyAuthScreen(context: Context) {
        analytics.logEvent(Analytics.Login.SHOW_EMPTY_AUTH_SCREEN)
        val intent = Intent(context, EmptyAuthActivity::class.java)
        context.startActivity(intent)
    }

    override fun showImage(context: Context, path: String) {
        val intent = Intent(context, PhotoViewActivity::class.java)
        intent.putExtra(PhotoViewActivity.PATH_KEY, path)
        context.startActivity(intent)
    }

    override fun showStatsScreen(context: Context, page: Int) {
        analytics.statsOpened()
        val intent = Intent(context, StatsActivity::class.java)
        intent.putExtra(StatsActivity.PAGE_KEY, page)
        context.startActivity(intent)
    }

    override fun showQuestionsPacksScreen(context: Context) {
        analytics.onQuestionsPacksOpened()
        context.startActivity(Intent(context, QuestionsPacksActivity::class.java))
    }

    override fun showGamificationDescription(context: Context) {
        analytics.logEvent(Analytics.EVENT_ON_GAMIFICATION_DESCRIPTION_SHOWN)
        val intent = Intent(context, DescriptionActivity::class.java)
        context.startActivity(intent)
    }

    override fun showLoginScreen(activity: Activity) {
        analytics.logEvent(Analytics.Login.SHOW_LOGIN_SCREEN)
        activity.startActivityForResult(Intent(activity, LoginActivity::class.java), LoginActivity.REQUEST_CODE)
    }

    override fun showRegisterScreen(activity: Activity) {
        analytics.logEvent(Analytics.Registration.SHOW_REGISTRATION_SCREEN)
        activity.startActivityForResult(Intent(activity, RegisterActivity::class.java), RegisterActivity.REQUEST_CODE)
    }
}
