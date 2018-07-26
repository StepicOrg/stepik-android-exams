package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import org.stepik.android.exams.R

abstract class SingleFragmentActivity : FragmentActivity() {
    abstract override fun createFragment(): Fragment

    open fun getLayoutResId() = R.layout.activity_fragment

    protected var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())

        val fm = supportFragmentManager
        fragment = fm.findFragmentById(R.id.fragment_container)

        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit()
        }
    }

}