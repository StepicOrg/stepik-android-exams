package org.stepik.android.exams.data.model

import android.os.Parcel
import android.os.Parcelable
import org.stepik.android.model.Lesson
import org.stepik.android.model.Step

class LessonTheoryWrapper(val lesson: Lesson = Lesson(), var stepsList: List<Step> = listOf(), val topicId: String = "", val courseId : Long = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Lesson::class.java.classLoader),
            parcel.createTypedArrayList(Step),
            parcel.readString(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(lesson, flags)
        parcel.writeTypedList(stepsList)
        parcel.writeString(topicId)
        parcel.writeLong(courseId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LessonTheoryWrapper> {
        override fun createFromParcel(parcel: Parcel): LessonTheoryWrapper {
            return LessonTheoryWrapper(parcel)
        }

        override fun newArray(size: Int): Array<LessonTheoryWrapper?> {
            return arrayOfNulls(size)
        }
    }

}