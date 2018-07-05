package org.stepik.android.exams.ui.activity

import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import org.stepik.android.exams.ui.fragment.ProgressDialogFragment

open class BaseFragmentActivity : AppCompatActivity() {
    protected fun showProgressDialogFragment(tag: String, title: String, msg: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            ProgressDialogFragment.newInstance(title, msg).show(supportFragmentManager, tag)
        }
    }
    protected fun hideProgressDialogFragment(tag: String) {
        val dialog = supportFragmentManager.findFragmentByTag(tag)
        (dialog as? DialogFragment)?.dismiss()
    }
}