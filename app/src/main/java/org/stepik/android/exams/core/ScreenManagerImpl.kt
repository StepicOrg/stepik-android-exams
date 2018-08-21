package org.stepik.android.exams.core


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.data.preference.SharedPreferenceHelper
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.ui.activity.*
import org.stepik.android.exams.util.AppConstants
import javax.inject.Inject

@AppSingleton
class ScreenManagerImpl
@Inject
constructor(
        private val context: Context,
        private val sharedPreferenceHelper: SharedPreferenceHelper
) : ScreenManager {
    override fun showStepsList(topicId: String, lesson: LessonWrapper, context: Context) {
        val intent = Intent(context, StepsListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(AppConstants.lesson, lesson)
        intent.putExtra(AppConstants.topicId, topicId)
        context.startActivity(intent)
    }

    override fun showLessons(topicId: String, context: Context) {
        val intent = Intent(context, LessonsActivity::class.java)
        intent.putExtra(AppConstants.topicId, topicId)
        context.startActivity(intent)
    }

    override fun continueAdaptiveCourse(topicId : String, activity: Activity) {
        val taskStackBuilder = TaskStackBuilder.create(activity)
        val adaptiveCourseIntent = Intent(activity, AdaptiveCourseActivity::class.java)
        adaptiveCourseIntent.putExtra("topicId", topicId)
        taskStackBuilder.addNextIntent(adaptiveCourseIntent)
        if (sharedPreferenceHelper.isFirstTimeAdaptive) {
            taskStackBuilder.addNextIntent(Intent(activity, AdaptiveOnboardingActivity::class.java))
        }
        taskStackBuilder.startActivities()
    }

    override fun showTopicsList() {
        val intent = Intent(context, TopicsListActivity::class.java)
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

    override fun openImage(context: Context, path: String) {
    }
}
