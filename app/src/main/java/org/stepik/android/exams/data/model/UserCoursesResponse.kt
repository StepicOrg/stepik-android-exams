package org.stepik.android.exams.data.model

import com.google.gson.annotations.SerializedName
import org.stepik.android.model.Meta
import org.stepik.android.model.UserCourse

class UserCoursesResponse(
        val meta: Meta,
        @SerializedName("user-courses")
        val userCourse: List<UserCourse>
)