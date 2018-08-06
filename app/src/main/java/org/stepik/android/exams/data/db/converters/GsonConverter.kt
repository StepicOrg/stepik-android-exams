package org.stepik.android.exams.data.db.converters


import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import org.stepik.android.exams.data.model.Submission
import org.stepik.android.exams.data.model.attempts.Attempt


class GsonConverter {
    var gson = Gson()

    @TypeConverter
    fun stringToAttempt(data: String?): Attempt? {
        return gson.fromJson<Attempt>(data, Attempt::class.java)
    }

    @TypeConverter
    fun attemptToString(obj: Attempt?): String? {
        return gson.toJson(obj)
    }

    @TypeConverter
    fun stringToSubmission(data: String?): Submission? {
        return gson.fromJson<Submission>(data, Submission::class.java)
    }

    @TypeConverter
    fun submissionToString(obj: Submission?): String? {
        return gson.toJson(obj)
    }
}