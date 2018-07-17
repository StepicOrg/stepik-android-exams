package org.stepik.android.exams.data.model

import com.google.gson.annotations.SerializedName
import org.stepic.droid.model.Actions
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
        var maxSubmissionCount: Int = 0
) {
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
        return result
    }
}



