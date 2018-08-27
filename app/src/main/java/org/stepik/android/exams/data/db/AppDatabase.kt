package org.stepik.android.exams.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import org.stepik.android.exams.data.db.converters.GsonConverter
import org.stepik.android.exams.data.db.dao.*
import org.stepik.android.exams.data.db.data.LessonInfo
import org.stepik.android.exams.data.db.data.StepInfo
import org.stepik.android.exams.data.db.data.TopicInfo
import org.stepik.android.exams.data.db.entity.AttemptEntitiy
import org.stepik.android.exams.data.db.entity.SubmissionEntity

@Database(entities =
[StepInfo::class,
    LessonInfo::class,
    TopicInfo::class,
    AttemptEntitiy::class,
    SubmissionEntity::class],
        version = 1, exportSchema = false)
@TypeConverters(GsonConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
    abstract fun navigationDao(): LessonDao
    abstract fun topicDao(): TopicDao
    abstract fun attemptEntitiyDao() : AttemptEntitiyDao
    abstract fun submissionEntityDao() : SubmissionEntityDao

    companion object {
        private const val DB_NAME = "app-db"

        fun createPersistentDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                        .build()
    }
}