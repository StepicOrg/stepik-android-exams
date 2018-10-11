package org.stepik.android.exams.data.model

import android.os.Parcel
import android.os.Parcelable
import org.stepik.android.exams.graph.model.GraphLesson
import org.stepik.android.exams.graph.model.Topic
import org.stepik.android.model.Lesson
import org.stepik.android.model.Step

class LessonTheoryWrapper(
        val lesson: Lesson = Lesson(),
        var stepsList: List<Step> = listOf(),
        val topic: Topic = Topic(),
        val graphLesson: GraphLesson = GraphLesson()
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Lesson::class.java.classLoader),
            parcel.createTypedArrayList(Step),
            parcel.readParcelable(Topic::class.java.classLoader),
            parcel.readParcelable(GraphLesson::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(lesson, flags)
        parcel.writeTypedList(stepsList)
        parcel.writeParcelable(topic, flags)
        parcel.writeParcelable(graphLesson, flags)
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