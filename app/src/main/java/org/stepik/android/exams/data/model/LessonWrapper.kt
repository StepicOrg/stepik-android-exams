package org.stepik.android.exams.data.model

import android.os.Parcel
import android.os.Parcelable
import org.stepik.android.model.Lesson
import org.stepik.android.model.Step

class LessonWrapper(val lesson: Lesson, var stepsList: List<Step>) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Lesson::class.java.classLoader),
            parcel.createTypedArrayList(Step)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(lesson, flags)
        parcel.writeTypedList(stepsList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LessonWrapper> {
        override fun createFromParcel(parcel: Parcel): LessonWrapper =
                LessonWrapper(parcel)


        override fun newArray(size: Int): Array<LessonWrapper?> =
                arrayOfNulls(size)
    }

}