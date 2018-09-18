package org.stepik.android.exams.analytic

interface AmplitudeAnalytic {
    object Properties {
        const val STEPIK_ID = "stepik_id"
        const val SUBMISSIONS_COUNT = "submissions_count"
        const val SCREEN_ORIENTATION = "screen_orientation"
        const val APPLICATION_ID = "application_id"
        const val PUSH_PERMISSION = "push_permission"
    }
    object Launch {
        const val FIRST_TIME = "Launch first time"
        const val SESSION_START = "Session start"
    }

    object Topic {
        const val TOPIC_OPENED = "Topic opened"
        object Params {
            const val ID = "id"
            const val TITLE = "title"
        }
    }

    object Lesson {
        const val LESSON_OPENED = "Lesson opened"
        object Params {
            const val ID = "id"
            const val TYPE = "type"
            const val COURSE = "id"
            const val TOPIC = "type"
        }
    }

    object Page {
        const val PAGE_OPENED = "Page opened"
        object Params {
            const val POSITION = "position"
            const val LESSON = "lesson"
            const val STEP = "step"
        }
    }

    object Submission {
        const val SUBMISSION_MADE = "Submission made"
        object Params {
            const val TYPE = "type"
            const val STEP = "step"
        }
    }
}