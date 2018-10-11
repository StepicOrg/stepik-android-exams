package org.stepik.android.exams.data.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.stepik.android.exams.graph.model.GraphLesson
@Entity
class GraphLessonEntity (
        @PrimaryKey(autoGenerate = false)
        var lesson: Long = 0,
        var type: GraphLesson.Type? = null,
        var lessonDescription: String = "",
        var course: Long = 0
)