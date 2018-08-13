package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import org.stepik.android.exams.data.db.data.NavigationInfo
import org.stepik.android.exams.data.model.Lesson

@Dao
interface NavigationDao {

    @Query("SELECT lesson FROM NavigationInfo WHERE topicId = :id")
    fun findAllLesson(id: String): Maybe<List<Lesson>>

    @Query("SELECT lesson FROM NavigationInfo WHERE lessonId = :id")
    fun findLessonById(id: Long): Maybe<Lesson>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(navigationInfo: NavigationInfo)
}