package org.stepik.android.exams.data.db.converters


import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import org.stepik.android.exams.data.model.LessonTheoryWrapper
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.model.*
import org.stepik.android.model.attempts.Attempt
import org.stepik.android.model.attempts.Dataset
import java.util.*


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
    fun stringToLesson(data: String?): LessonTheoryWrapper? {
        return gson.fromJson<LessonTheoryWrapper>(data, LessonTheoryWrapper::class.java)
    }

    @TypeConverter
    fun lessonToString(obj: LessonTheoryWrapper?): String? {
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

    @TypeConverter
    fun submissionStepStatus(status: Step.Status): String =
            gson.toJson(status)

    @TypeConverter
    fun stepStatusFromString(status: String): Step.Status =
            gson.fromJson<Step.Status>(status, Step.Status::class.java)

    @TypeConverter
    fun blockStatus(status: Block): String =
            gson.toJson(status)

    @TypeConverter
    fun blockFromString(status: String): Block =
            gson.fromJson<Block>(status, Block::class.java)

    @TypeConverter
    fun actionStatus(status: Actions): String =
            gson.toJson(status)

    @TypeConverter
    fun actionFromString(status: String): Actions =
            gson.fromJson<Actions>(status, Actions::class.java)

    @TypeConverter
    fun listStatus(status: List<String>): String =
            gson.toJson(status)

    @TypeConverter
    fun  listFromString(status: String): List<String> =
            gson.fromJson<List<String>>(status, List::class.java)

    @TypeConverter
    fun dateToLong(date: Date?): Long? =
            date?.time

    @TypeConverter
    fun longToDate(time: Long?): Date? =
            time?.let(::Date)
}