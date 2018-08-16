package org.stepik.android.exams.core

import android.app.Activity
import android.content.Context
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.model.Lesson

interface ScreenManager {
    fun showOnboardingScreen()
    fun showTopicsList()
    fun showStepsList(id: String, lesson: LessonWrapper, context: Context)
    fun showEmptyAuthScreen(context: Context)
    fun showLoginScreen(activity: Activity)
    fun showRegisterScreen(activity: Activity)
    fun showLessons(id: String, context: Context)
    fun openImage(context: Context, path: String)
}