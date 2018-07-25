package org.stepik.android.exams.util.resolvers.text

data class TextResult(
        val text: CharSequence,
        val isNeedWebView: Boolean = false,
        val isNeedLaTeX: Boolean = false

)