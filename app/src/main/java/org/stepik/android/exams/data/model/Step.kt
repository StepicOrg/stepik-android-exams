package org.stepik.android.exams.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.stepik.android.exams.util.readBoolean
import org.stepik.android.exams.util.writeBoolean
import org.stepik.android.model.Actions
import org.stepik.android.model.Block
import java.util.*

data class Step(
        var id: Long = 0,
        var lesson: Long = 0,
        var position: Long = 0,
        var status: StepStatus? = null,
        var block: Block? = null,
        var progress: String? = null,
        var subscriptions: Array<String>? = null,
        var viewed_by: Long = 0,
        var passed_by: Long = 0,
        var create_date: String? = null,
        var update_date: String? = null,
        var is_cached: Boolean = false,
        var is_loading: Boolean = false,
        var is_custom_passed: Boolean = false,
        var actions: Actions? = null,
        var discussions_count: Int = 0,
        var discussion_proxy: String? = null,
        @SerializedName("has_submissions_restrictions")
        var hasSubmissionRestriction: Boolean = false,
        @SerializedName("max_submissions_count")
        var maxSubmissionCount: Int = 0,
        var is_last: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readParcelable(StepStatus::class.java.classLoader),
            parcel.readParcelable(Block::class.java.classLoader),
            parcel.readString(),
            parcel.createStringArray(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readParcelable(Actions::class.java.classLoader),
            parcel.readInt(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readBoolean()) {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Step

        if (id != other.id) return false
        if (lesson != other.lesson) return false
        if (position != other.position) return false
        if (status != other.status) return false
        if (block != other.block) return false
        if (progress != other.progress) return false
        if (!Arrays.equals(subscriptions, other.subscriptions)) return false
        if (viewed_by != other.viewed_by) return false
        if (passed_by != other.passed_by) return false
        if (create_date != other.create_date) return false
        if (update_date != other.update_date) return false
        if (is_cached != other.is_cached) return false
        if (is_loading != other.is_loading) return false
        if (is_custom_passed != other.is_custom_passed) return false
        if (actions != other.actions) return false
        if (discussions_count != other.discussions_count) return false
        if (discussion_proxy != other.discussion_proxy) return false
        if (hasSubmissionRestriction != other.hasSubmissionRestriction) return false
        if (maxSubmissionCount != other.maxSubmissionCount) return false
        if (is_last != other.is_last) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + lesson.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (block?.hashCode() ?: 0)
        result = 31 * result + (progress?.hashCode() ?: 0)
        result = 31 * result + (subscriptions?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + viewed_by.hashCode()
        result = 31 * result + passed_by.hashCode()
        result = 31 * result + (create_date?.hashCode() ?: 0)
        result = 31 * result + (update_date?.hashCode() ?: 0)
        result = 31 * result + is_cached.hashCode()
        result = 31 * result + is_loading.hashCode()
        result = 31 * result + is_custom_passed.hashCode()
        result = 31 * result + (actions?.hashCode() ?: 0)
        result = 31 * result + discussions_count
        result = 31 * result + (discussion_proxy?.hashCode() ?: 0)
        result = 31 * result + hasSubmissionRestriction.hashCode()
        result = 31 * result + maxSubmissionCount
        result = 31 * result + is_last.hashCode()
        return result
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(lesson)
        parcel.writeLong(position)
        parcel.writeParcelable(status, flags)
        parcel.writeParcelable(block, flags)
        parcel.writeString(progress)
        parcel.writeStringArray(subscriptions)
        parcel.writeLong(viewed_by)
        parcel.writeLong(passed_by)
        parcel.writeString(create_date)
        parcel.writeString(update_date)
        parcel.writeByte(if (is_cached) 1 else 0)
        parcel.writeByte(if (is_loading) 1 else 0)
        parcel.writeByte(if (is_custom_passed) 1 else 0)
        parcel.writeParcelable(actions, flags)
        parcel.writeInt(discussions_count)
        parcel.writeString(discussion_proxy)
        parcel.writeByte(if (hasSubmissionRestriction) 1 else 0)
        parcel.writeInt(maxSubmissionCount)
        parcel.writeBoolean(is_last)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Step> {
        override fun createFromParcel(parcel: Parcel): Step {
            return Step(parcel)
        }

        override fun newArray(size: Int): Array<Step?> {
            return arrayOfNulls(size)
        }
    }
}



