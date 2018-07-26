package org.stepik.android.exams.core


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import org.stepik.android.exams.R
import org.stepik.android.exams.data.model.Lesson
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.ui.activity.*
import javax.inject.Inject

@AppSingleton
class ScreenManagerImpl
@Inject
constructor(
        private val context: Context
) : ScreenManager {
    override fun showStepsList(lesson: Lesson, context: Context) {
        val intent = Intent(context, StepsActivity::class.java)
        intent.putExtra("lesson", lesson)
        context.startActivity(intent)
    }

    override fun showCourse(id: String, context: Context) {
        val intent = Intent(context, LessonsActivity::class.java)
        intent.putExtra("id", id)
        context.startActivity(intent)
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

    override fun openComments(context: Activity, discussionProxyId: String, stepId: Long, needOpenForm: Boolean) {
        if (discussionProxyId.isEmpty()) {
            Toast.makeText(context, R.string.comment_denied, Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(context, CommentsActivity::class.java)
            val bundle = Bundle()
            bundle.putString(CommentsActivity.Companion.keyDiscussionProxyId, discussionProxyId)
            bundle.putLong(CommentsActivity.Companion.keyStepId, stepId)
            bundle.putBoolean(CommentsActivity.Companion.keyNeedInstaOpenForm, needOpenForm)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}
