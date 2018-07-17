package org.stepik.android.exams.data.model


import com.google.gson.annotations.SerializedName
import java.util.*

data class Lesson(
        var id: Long = 0,
        var steps: LongArray = longArrayOf(),
        var tags: IntArray? = null,
        var playlists: Array<String>? = null,
        var is_featured: Boolean = false,
        var is_prime: Boolean = false,
        var progress: String? = null,
        var owner: Int = 0,
        var subscriptions: Array<String>? = null,
        var viewed_by: Int = 0,
        var passed_by: Int = 0,
        var dependencies: Array<String>? = null,
        var followers: Array<String>? = null,
        var language: String? = null,
        var is_public: Boolean = false,
        var title: String? = null,
        var slug: String? = null,
        var create_date: String? = null,
        var update_date: String? = null,
        var learners_group: String? = null,
        var teacher_group: String? = null,
        var is_cached: Boolean = false,
        var is_loading: Boolean = false,
        var cover_url: String? = null,
        @SerializedName("time_to_complete")
        var timeToComplete: Long = 0
) {
    var stepsList : LinkedList<Step> = LinkedList()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Lesson

        if (id != other.id) return false
        if (!Arrays.equals(steps, other.steps)) return false
        if (!Arrays.equals(tags, other.tags)) return false
        if (!Arrays.equals(playlists, other.playlists)) return false
        if (is_featured != other.is_featured) return false
        if (is_prime != other.is_prime) return false
        if (progress != other.progress) return false
        if (owner != other.owner) return false
        if (!Arrays.equals(subscriptions, other.subscriptions)) return false
        if (viewed_by != other.viewed_by) return false
        if (passed_by != other.passed_by) return false
        if (!Arrays.equals(dependencies, other.dependencies)) return false
        if (!Arrays.equals(followers, other.followers)) return false
        if (language != other.language) return false
        if (is_public != other.is_public) return false
        if (title != other.title) return false
        if (slug != other.slug) return false
        if (create_date != other.create_date) return false
        if (update_date != other.update_date) return false
        if (learners_group != other.learners_group) return false
        if (teacher_group != other.teacher_group) return false
        if (is_cached != other.is_cached) return false
        if (is_loading != other.is_loading) return false
        if (cover_url != other.cover_url) return false
        if (timeToComplete != other.timeToComplete) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + Arrays.hashCode(steps)
        result = 31 * result + (tags?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (playlists?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + is_featured.hashCode()
        result = 31 * result + is_prime.hashCode()
        result = 31 * result + (progress?.hashCode() ?: 0)
        result = 31 * result + owner
        result = 31 * result + (subscriptions?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + viewed_by
        result = 31 * result + passed_by
        result = 31 * result + (dependencies?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (followers?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (language?.hashCode() ?: 0)
        result = 31 * result + is_public.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (slug?.hashCode() ?: 0)
        result = 31 * result + (create_date?.hashCode() ?: 0)
        result = 31 * result + (update_date?.hashCode() ?: 0)
        result = 31 * result + (learners_group?.hashCode() ?: 0)
        result = 31 * result + (teacher_group?.hashCode() ?: 0)
        result = 31 * result + is_cached.hashCode()
        result = 31 * result + is_loading.hashCode()
        result = 31 * result + (cover_url?.hashCode() ?: 0)
        result = 31 * result + timeToComplete.hashCode()
        return result
    }
}
