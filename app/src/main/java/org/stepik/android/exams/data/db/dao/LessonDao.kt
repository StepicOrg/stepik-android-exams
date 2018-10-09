package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.*
import io.reactivex.Maybe
import io.reactivex.Single
import org.stepik.android.exams.data.db.pojo.LessonTheoryWrapperPojo
import org.stepik.android.exams.data.db.entity.LessonEntity

@Dao
interface LessonDao {
    @Transaction
    @Query("SELECT * FROM TopicInfoEntity JOIN LessonEntity ON TopicInfoEntity.lesson = lessonId WHERE TopicInfoEntity.topicId = :topicId")
    fun findAllLessonsInfoByTopicId(topicId: String): Maybe<List<LessonTheoryWrapperPojo>>

    @Transaction
    @Query("SELECT * FROM TopicInfoEntity JOIN LessonEntity ON TopicInfoEntity.lesson = lessonId WHERE lessonId = :lessonId")
    fun findLessonInfoById(lessonId: Long): Maybe<LessonTheoryWrapperPojo>

    @Query("SELECT topicId FROM TopicInfoEntity WHERE TopicInfoEntity.lesson =:lessonId")
    fun findTopicByLessonId(lessonId: Long) : Single<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(lessons: List<LessonEntity>)
}