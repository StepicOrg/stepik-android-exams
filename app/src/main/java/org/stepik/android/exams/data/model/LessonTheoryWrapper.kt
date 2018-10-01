package org.stepik.android.exams.data.model

import android.os.Parcel
import android.os.Parcelable
import org.stepik.android.model.Lesson
import org.stepik.android.model.Step

class LessonTheoryWrapper
@JvmOverloads
constructor(val lesson: Lesson = Lesson(), var stepsList: List<Step> = listOf(), val topicId: String = "") : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(lesson, flags)
        parcel.writeTypedList(stepsList)
        parcel.writeString(topicId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<LessonTheoryWrapper> {
        override fun createFromParcel(parcel: Parcel) = LessonTheoryWrapper(
                parcel.readParcelable(Lesson::class.java.classLoader)!!,
                parcel.createTypedArrayList(Step)!!,
                parcel.readString()!!
        )

        override fun newArray(size: Int): Array<LessonTheoryWrapper?> =
                arrayOfNulls(size)
    }

}