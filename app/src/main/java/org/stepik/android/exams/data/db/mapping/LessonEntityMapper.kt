package org.stepik.android.exams.data.db.mapping

import org.stepik.android.exams.data.db.entity.LessonEntity
import org.stepik.android.model.Lesson

fun Lesson.toEntity(): LessonEntity =
        LessonEntity(id, steps, tags, playlists, isFeatured, isPrime, progress, owner, subscriptions, viewedBy, passedBy, dependencies, followers, language, isPublic, title, slug, createDate, updateDate, learnersGroup, teacherGroup, coverUrl, timeToComplete)

fun LessonEntity.toObject(): Lesson =
        Lesson(id, steps, tags, playlists, isFeatured, isPrime, progress, owner, subscriptions, viewedBy, passedBy, dependencies, followers, language, isPublic, title, slug, createDate, updateDate, learnersGroup, teacherGroup, coverUrl, timeToComplete)