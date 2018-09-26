package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import io.reactivex.Maybe
import org.stepik.android.exams.data.db.data.TheoryLessonWrapper
import org.stepik.android.exams.data.db.entity.LessonEntity

@Dao
interface LessonDao {
    @Transaction
    @Query("SELECT * FROM TopicInfo JOIN LessonEntity ON TopicInfo.lesson = LessonEntity.lessonId WHERE topicId = :topicId")
    fun findAllLessonsByTopicId(topicId: String): Maybe<List<TheoryLessonWrapper>>

    @Transaction
    @Query("SELECT * FROM TopicInfo JOIN LessonEntity ON TopicInfo.lesson = LessonEntity.lessonId WHERE LessonEntity.lessonId = :lessonId")
    fun findLessonById(lessonId: Long): Maybe<TheoryLessonWrapper>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(lessons: List<LessonEntity>)
}