package org.stepik.android.exams.graph.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class GraphLesson
@JvmOverloads
constructor(
        @SerializedName("id")
        val id: Long = 0,
        @SerializedName("type")
        val type: Type = Type.THEORY,
        @SerializedName("description")
        val description: String = "",
        @SerializedName("course")
        val course: Long = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            Type.valueOf(parcel.readString()),
            parcel.readString(),
            parcel.readLong()) {
    }

    enum class Type {
        @SerializedName("theory")
        THEORY,
        @SerializedName("practice")
        PRACTICE
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(type.toString())
        parcel.writeString(description)
        parcel.writeLong(course)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GraphLesson> {
        override fun createFromParcel(parcel: Parcel): GraphLesson =
                GraphLesson(parcel)

        override fun newArray(size: Int): Array<GraphLesson?> =
                arrayOfNulls(size)
    }
}