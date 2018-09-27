package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import io.reactivex.Maybe
import org.stepik.android.exams.data.db.pojo.LessonTheoryWrapperPojo
import org.stepik.android.exams.data.db.entity.LessonEntity

@Dao
interface LessonDao {
    @Transaction
    @Query("SELECT * FROM TopicEntity JOIN LessonEntity ON TopicEntity.lesson = LessonEntity.lessonId WHERE topicId = :topicId")
    fun findAllLessonsByTopicId(topicId: String): Maybe<List<LessonTheoryWrapperPojo>>

    @Transaction
    @Query("SELECT * FROM TopicEntity JOIN LessonEntity ON TopicEntity.lesson = LessonEntity.lessonId WHERE LessonEntity.lessonId = :lessonId")
    fun findLessonById(lessonId: Long): Maybe<LessonTheoryWrapperPojo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(lessons: List<LessonEntity>)
}