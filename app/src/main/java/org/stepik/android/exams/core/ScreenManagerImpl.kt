package org.stepik.android.exams.core


import android.app.Activity
import android.content.Context
import android.content.Intent
import org.stepik.android.exams.App
import org.stepik.android.exams.adaptive.ui.activity.AdaptiveCourseActivity
import org.stepik.android.exams.analytic.AmplitudeAnalytic
import org.stepik.android.exams.analytic.Analytic
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
        private val context: Context,
        private val analytic: Analytic
) : ScreenManager {

    override fun showStepsList(topicId: String, lessonTheory: LessonTheoryWrapper, context: Context) {
        analytic.reportAmplitudeEvent(AmplitudeAnalytic.Lesson.LESSON_OPENED,
                mapOf(
                        AmplitudeAnalytic.Lesson.Params.ID to lessonTheory.lesson.id,
                        AmplitudeAnalytic.Lesson.Params.TYPE to GraphLesson.Type.THEORY,
                        AmplitudeAnalytic.Lesson.Params.COURSE to lessonTheory.graphLesson.course,
                        AmplitudeAnalytic.Lesson.Params.TOPIC to topicId))
        val intent = Intent(context, StepsListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(StepsListActivity.EXTRA_LESSON, lessonTheory)
        intent.putExtra(StepsListActivity.EXTRA_TOPIC_ID, topicId)
        intent.putExtra(StepsListActivity.EXTRA_COURSE, lessonTheory.graphLesson.course)
        context.startActivity(intent)
    }

    override fun showLessons(context: Context, topic: Topic) {
        analytic.reportAmplitudeEvent(AmplitudeAnalytic.Topic.TOPIC_OPENED,
                mapOf(
                        AmplitudeAnalytic.Topic.Params.ID to topic.id,
                        AmplitudeAnalytic.Topic.Params.TITLE to topic.title))
        val intent = Intent(context, TopicLessonsActivity::class.java)
        intent.putExtra(TopicLessonsActivity.EXTRA_TOPIC, topic)
        context.startActivity(intent)
    }

    override fun continueAdaptiveCourse(topic: Topic, activity: Activity) {
        val adaptiveCourseIntent = Intent(activity, AdaptiveCourseActivity::class.java)
        adaptiveCourseIntent.putExtra(AppConstants.topic, topic)
        activity.startActivity(adaptiveCourseIntent)
    }

    override fun showMainMenu() {
        val intent = Intent(context, MainMenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }

    override fun showOnboardingScreen() {
        analytic.reportAmplitudeEvent(AmplitudeAnalytic.Launch.FIRST_TIME)
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
