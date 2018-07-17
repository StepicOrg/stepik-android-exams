package org.stepik.android.exams.ui.dialog

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

class ProgressDialogFragment : DialogFragment() {
    companion object {
        private const val TITLE_ARG = "title"
        private const val MESSAGE_ARG = "message"

        fun newInstance(title: String = "", message: String = ""): ProgressDialogFragment {
            val dialogFragment = ProgressDialogFragment()
            val args = Bundle()
            args.putString(TITLE_ARG, title)
            args.putString(MESSAGE_ARG, message)
            dialogFragment.arguments = args
            return dialogFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val progressDialog = ProgressDialog(context, theme)
        progressDialog.setTitle(arguments?.getString(TITLE_ARG, ""))
        progressDialog.setMessage(arguments?.getString(MESSAGE_ARG, ""))
        progressDialog.isIndeterminate = true
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        return progressDialog
    }
}