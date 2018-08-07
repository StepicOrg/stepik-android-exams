package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import org.stepik.android.exams.data.db.data.NavigationInfo

@Dao
interface NavigationDao {
    @Query("SELECT * FROM NavigationInfo WHERE lessonId = :id")
    fun findLessonById(id: Long): NavigationInfo?

    @Query("SELECT * FROM NavigationInfo WHERE id = (SELECT next_lesson FROM NavigationInfo WHERE lessonId = :id)")
    fun findNextIdByLessonId(id: Long): NavigationInfo?

    @Query("SELECT * FROM NavigationInfo WHERE id = (SELECT prev_lesson FROM NavigationInfo WHERE lessonId = :id)")
    fun findPrevIdByLessonId(id: Long): NavigationInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLessons(navigationInfo: NavigationInfo)
}