package org.stepik.android.exams.data.model

import com.google.gson.annotations.SerializedName

class Submission(
        val id: Long = 0,
        val status: Status? = null,
        val score: String? = null,
        val hint: String? = null,
        val time: String? = null,
        val reply: Reply? = null,
        val attempt: Long = 0,
        val session: String? = null,
        val eta: String? = null
) {

    constructor(reply: Reply?, attempt: Long) : this(id = 0, reply = reply, attempt = attempt)

    enum class Status(val scope: String) {
        @SerializedName("correct")
        CORRECT("correct"),

        @SerializedName("wrong")
        WRONG("wrong"),

        @SerializedName("evaluation")
        EVALUATION("evaluation"),

        @SerializedName("local")
        LOCAL("local")
    }
}