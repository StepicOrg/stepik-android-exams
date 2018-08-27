package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import org.stepik.android.exams.data.db.data.LessonInfo
import org.stepik.android.exams.data.model.LessonWrapper

@Dao
interface LessonDao {

    @Query("SELECT lesson FROM LessonInfo WHERE topicId = :id")
    fun findAllLessonsByTopicId(id: String): Maybe<List<LessonWrapper>>

    @Query("SELECT lesson FROM LessonInfo WHERE lessonId = :id")
    fun findLessonById(id: Long): Maybe<LessonWrapper>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(lessonInfos: List<LessonInfo>)
}