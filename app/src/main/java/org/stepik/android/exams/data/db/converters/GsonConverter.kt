package org.stepik.android.exams.data.db.converters


import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import org.stepik.android.exams.data.model.LessonWrapper
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.model.Reply
import org.stepik.android.model.Submission
import org.stepik.android.model.attempts.Attempt
import org.stepik.android.model.attempts.Dataset


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

    @TypeConverter
    fun stringToDatasetWrapper(data: String): Dataset =
            gson.fromJson<Dataset>(data, Dataset::class.java)

    @TypeConverter
    fun datasetWrapperToString(obj: Dataset): String =
            gson.toJson(obj)

    @TypeConverter
    fun stringToReply(data: String): Reply =
            gson.fromJson<Reply>(data, Reply::class.java)

    @TypeConverter
    fun replyToString(obj: Reply): String =
            gson.toJson(obj)

    @TypeConverter
    fun submissionStatusToString(status: Submission.Status): String =
            gson.toJson(status)

    @TypeConverter
    fun submissionStatusFromString(status: String): Submission.Status =
            gson.fromJson<Submission.Status>(status, Submission.Status::class.java)
}