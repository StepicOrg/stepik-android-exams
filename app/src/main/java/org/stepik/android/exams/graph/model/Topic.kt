package org.stepik.android.exams.graph.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Topic(
        @SerializedName("id")
        val id: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("required-for")
        val requiredFor: String?
) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(requiredFor)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Topic> {
        override fun createFromParcel(parcel: Parcel) = Topic(
                parcel.readString()!!,
                parcel.readString()!!,
                parcel.readString()
        )

        override fun newArray(size: Int): Array<Topic?> =
                arrayOfNulls(size)
    }
}