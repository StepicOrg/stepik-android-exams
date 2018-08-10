package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import io.reactivex.Single
import org.stepik.android.exams.data.db.data.NavigationInfo

@Dao
interface NavigationDao {

    @Query("SELECT * FROM NavigationInfo WHERE topicId = :id")
    fun findAllLesson(id: String): Maybe<List<NavigationInfo>>

    @Query("SELECT * FROM NavigationInfo WHERE lessonId = :id")
    fun findLessonById(id: Long): NavigationInfo?

    @Query("SELECT topicId FROM NavigationInfo WHERE lessonId = :id")
    fun findTopicByLessonId(id: Long): Single<String>

    @Query("SELECT * FROM NavigationInfo WHERE id = (SELECT next_lesson FROM NavigationInfo WHERE lessonId = :id)")
    fun findNextByLessonId(id: Long): Single<NavigationInfo>

    @Query("SELECT * FROM NavigationInfo WHERE id = (SELECT prev_lesson FROM NavigationInfo WHERE lessonId = :id)")
    fun findPrevByLessonId(id: Long): Single<NavigationInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(navigationInfo: NavigationInfo)
}