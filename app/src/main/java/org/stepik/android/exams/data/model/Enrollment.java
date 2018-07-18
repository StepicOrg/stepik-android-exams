package org.stepik.android.exams.data.model;

public class Enrollment {
    private long course_id;
    private boolean has_course;
    private long course;

    public Enrollment(long courseId) {
        course = courseId;
        course_id = courseId;
        has_course = false;
    }
}