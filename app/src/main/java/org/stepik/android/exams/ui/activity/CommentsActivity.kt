package org.stepik.android.exams.ui.activity

import android.support.v4.app.Fragment
import android.view.MenuItem

class CommentsActivity : SingleFragmentActivity() {

    companion object {
        const val keyDiscussionProxyId = "KEY_DISCUSSION_PROXY_ID"
        const val keyStepId = "KEY_step_id"
        const val keyNeedInstaOpenForm = "key_need_insta_open"
    }

    override fun createFragment(): Fragment {
        val discussionId: String = intent.extras.getString(keyDiscussionProxyId)
        val stepId: Long = intent.extras.getLong(keyStepId)
        val needInstaOpen: Boolean = intent.extras.getBoolean(keyNeedInstaOpenForm)
        return Fragment()
        //return CommentsFragment.newInstance(discussionId, stepId, needInstaOpen)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        // overridePendingTransition(org.stepic.droid.R.anim.no_transition, org.stepic.droid.R.anim.push_down)
    }

}