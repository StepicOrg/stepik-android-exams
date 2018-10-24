package org.stepik.android.exams.data.db.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import io.reactivex.Single

@Dao
interface GraphLessonDao {
    @Query("SELECT distinct(course) FROM GraphLessonEntity")
    fun getUniqueCourses() : Single<List<Long>>
}