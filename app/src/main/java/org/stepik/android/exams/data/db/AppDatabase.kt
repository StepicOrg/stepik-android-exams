package org.stepik.android.exams.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import org.stepik.android.exams.data.db.converters.GsonConverter
import org.stepik.android.exams.data.db.dao.*
import org.stepik.android.exams.data.db.entity.*

@Database(
        entities = [
            AssignmentEntity::class,
            VideoEntity::class,
            TopicEntity::class,
            SubmissionEntity::class,
            ProgressEntity::class,
            LessonEntity::class,
            StepEntity::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(GsonConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
    abstract fun lessonDao(): LessonDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun topicDao(): TopicDao
    abstract fun stepDao() : StepDao
    abstract fun submissionEntityDao(): SubmissionEntityDao

    companion object {
        private const val DB_NAME = "app-db"

        fun createPersistentDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                        .build()
    }
}