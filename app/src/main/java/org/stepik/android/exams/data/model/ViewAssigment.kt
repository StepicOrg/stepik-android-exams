package org.stepik.android.exams.data.model

import android.os.Parcel
import android.os.Parcelable

class ViewAssignment(val assignment: Long?, val step: Long) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(assignment)
        parcel.writeLong(step)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ViewAssignment> {
        override fun createFromParcel(parcel: Parcel): ViewAssignment {
            return ViewAssignment(parcel)
        }

        override fun newArray(size: Int): Array<ViewAssignment?> {
            return arrayOfNulls(size)
        }
    }
}