package org.stepik.android.exams.data.model;

public class EnrollmentWrapper {
    private Enrollment enrollment;

    public EnrollmentWrapper(long courseId) {
        enrollment = new Enrollment(courseId);
    }
}