package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.stepik.android.model.Reply
import org.stepik.android.model.ReplyWrapper

@Entity
class SubmissionEntity(
        @PrimaryKey(autoGenerate = false)
        val id: Long = 0,
        val status: Status? = null,
        val score: String? = null,
        val hint: String? = null,
        val time: String? = null,
        reply: Reply? = null,
        val attempt: Long = 0,
        val session: String? = null,
        val eta: String? = null
) {
    @SerializedName("reply")
    private val replyWrapper: ReplyWrapper? = reply?.let(::ReplyWrapper)

    val reply: Reply?
        get() = replyWrapper?.reply

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