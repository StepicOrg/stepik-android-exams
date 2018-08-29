package org.stepik.android.exams.core

import android.app.Activity
import android.content.Context
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.data.model.ViewAssignment
import org.stepik.android.exams.graph.model.Topic

interface ScreenManager {
    fun showOnboardingScreen()
    fun showTopicsList()
    fun showStepsList(topicId: String, lesson: LessonWrapper, context: Context)
    fun showEmptyAuthScreen(context: Context)
    fun showLoginScreen(activity: Activity)
    fun showRegisterScreen(activity: Activity)

    fun showLessons(context: Context, topic: Topic)

    fun continueAdaptiveCourse(topicId: String, activity: Activity)
    fun openImage(context: Context, path: String)
    fun pushToViewedQueue(viewAssignment: ViewAssignment)
}