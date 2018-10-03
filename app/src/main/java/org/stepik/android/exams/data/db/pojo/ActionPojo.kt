package org.stepik.android.exams.data.db.pojo

class ActionPojo(
        var vote: Boolean = false,
        var delete: Boolean = false,
        var testSection: String? = null,
        var doReview: String? = null,
        var editInstructions: String? = null
)