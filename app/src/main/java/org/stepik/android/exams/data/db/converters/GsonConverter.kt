package org.stepik.android.exams.data.db.converters


import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.model.Submission
import org.stepik.android.model.attempts.Attempt


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

    @TypeConverter
    fun stringToLesson(data: String?): LessonWrapper? {
        return gson.fromJson<LessonWrapper>(data, LessonWrapper::class.java)
    }

    @TypeConverter
    fun lessonToString(obj: LessonWrapper?): String? {
        return gson.toJson(obj)
    }

    @TypeConverter
    fun stringToArray(data: String?): LongArray {
        return gson.fromJson<LongArray>(data, LongArray::class.java)
    }

    @TypeConverter
    fun arrayToString(obj: LongArray?): String? {
        return gson.toJson(obj)
    }

    @TypeConverter
    fun graphLessonTypeToString(type: GraphLesson.Type): String =
            type.name

    @TypeConverter
    fun graphLessonTypefromString(type: String): GraphLesson.Type =
            GraphLesson.Type.valueOf(type)
}