package org.stepik.android.exams.ui.activity

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_menu.*
import org.stepik.android.exams.R
import org.stepik.android.exams.ui.fragment.TestingFragment
import org.stepik.android.exams.ui.fragment.TopicsListFragment
import org.stepik.android.exams.ui.fragment.TrainingFragment

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            setFragment(item.itemId)
            true
        }

        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.study
        }
    }

    private fun setFragment(@IdRes id: Int) {
        val currentFragmentTag: String? = supportFragmentManager.findFragmentById(R.id.frame)?.tag
        val nextFragment: Fragment? = when (id) {
            R.id.study -> {
                getNextFragmentOrNull(currentFragmentTag, TopicsListFragment::class.java.simpleName, TopicsListFragment.Companion::newInstance)
            }
            R.id.training -> {
                getNextFragmentOrNull(currentFragmentTag, TrainingFragment::class.java.simpleName, TrainingFragment.Companion::newInstance)
            }
            R.id.testing -> {
                getNextFragmentOrNull(currentFragmentTag, TestingFragment::class.java.simpleName, TestingFragment.Companion::newInstance)
            }
            else -> {
                null
            }
        }
        if (nextFragment != null) {
            setFragment(R.id.frame, nextFragment)
        }
    }

    private fun setFragment(@IdRes containerId: Int, fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, fragment, fragment.javaClass.simpleName)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private inline fun getNextFragmentOrNull(currentFragmentTag: String?, nextFragmentTag: String, nextFragmentCreation: () -> Fragment) =
            if (currentFragmentTag != nextFragmentTag) {
                supportFragmentManager.findFragmentByTag(nextFragmentTag)
                        ?: nextFragmentCreation.invoke()
            } else {
                null
            }

    override fun onBackPressed() {
        finish()
    }
}