package org.stepik.android.exams.core


import android.app.Activity
import android.content.Context
import android.content.Intent
import org.stepik.android.exams.App
import org.stepik.android.exams.adaptive.ui.activity.AdaptiveCourseActivity
import org.stepik.android.exams.core.services.ViewPushService
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.ViewAssignment
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.exams.ui.activity.*
import org.stepik.android.exams.util.AppConstants
import javax.inject.Inject

@AppSingleton
class ScreenManagerImpl
@Inject
constructor(
        private val context: Context
) : ScreenManager {
    override fun showStepsList(topicId: String, lessonTheory: LessonTheoryWrapper, context: Context) {
        val intent = Intent(context, StepsListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(StepsListActivity.EXTRA_LESSON, lessonTheory)
        intent.putExtra(StepsListActivity.EXTRA_TOPIC_ID, topicId)
        context.startActivity(intent)
    }

    override fun showLessons(context: Context, topic: Topic) {
        val intent = Intent(context, TopicLessonsActivity::class.java)
        intent.putExtra(TopicLessonsActivity.EXTRA_TOPIC, topic)
        context.startActivity(intent)
    }

    override fun continueAdaptiveCourse(topicId: String, activity: Activity) {
        val adaptiveCourseIntent = Intent(activity, AdaptiveCourseActivity::class.java)
        adaptiveCourseIntent.putExtra(AppConstants.topicId, topicId)
        activity.startActivity(adaptiveCourseIntent)
    }

    override fun showMainMenu() {
        val intent = Intent(context, MainMenuActivity::class.java)
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

    override fun pushToViewedQueue(viewAssignment: ViewAssignment) {
        val context = App.getAppContext()
        val intent = Intent(context, ViewPushService::class.java)
        intent.putExtra(AppConstants.viewPush, viewAssignment)
        context.startService(intent)
    }

    override fun showLessonsList(context: Context, type: GraphLesson.Type) {
        val intent = Intent(context, LessonListActivity::class.java)
        intent.putExtra(AppConstants.TYPE_LESSONS_LIST, type)
        context.startActivity(intent)
    }
}
