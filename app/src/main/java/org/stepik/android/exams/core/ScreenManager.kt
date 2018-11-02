package org.stepik.android.exams.core

import android.app.Activity
import android.content.Context
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.data.model.ViewAssignment
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.graph.model.Topic

interface ScreenManager {
    fun showOnboardingScreen()
    fun showMainMenu()
    fun showStepsList(topicId: String, lessonTheory: LessonTheoryWrapper, context: Context, stepPosition : Long = 0)
    fun showEmptyAuthScreen(context: Context)
    fun showLoginScreen(activity: Activity)
    fun showRegisterScreen(activity: Activity)
    fun showLessons(context: Context, topic: Topic)
    fun continueAdaptiveCourse(topic: Topic, activity: Activity)
    fun openImage(context: Context, path: String)
    fun pushToViewedQueue(viewAssignment: ViewAssignment)
    fun showLessonsList(context: Context, type: GraphLesson.Type)
}