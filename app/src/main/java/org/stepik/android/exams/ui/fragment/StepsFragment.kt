package org.stepik.android.exams.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment

class StepsFragment : Fragment() {
    companion object {
        fun newInstance(): StepsFragment {
            val args = Bundle()
            val fragment = StepsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}