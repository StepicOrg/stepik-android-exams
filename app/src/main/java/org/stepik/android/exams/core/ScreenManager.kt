package org.stepik.android.exams.core

import android.app.Activity
import android.content.Context
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.data.model.ViewAssignment

interface ScreenManager {
    fun showOnboardingScreen()
    fun showMainMenu()
    fun showStepsList(topicId: String, lesson: LessonWrapper, context: Context)
    fun showEmptyAuthScreen(context: Context)
    fun showLoginScreen(activity: Activity)
    fun showRegisterScreen(activity: Activity)
    fun showLessons(topicId: String, context: Context)
    fun continueAdaptiveCourse(topicId: String, activity: Activity)
    fun openImage(context: Context, path: String)
    fun pushToViewedQueue(viewAssignment: ViewAssignment)
}