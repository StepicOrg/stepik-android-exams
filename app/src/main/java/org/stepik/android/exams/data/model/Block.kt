package org.stepik.android.exams.data.model

import android.os.Parcel
import android.os.Parcelable


data class Block(
        var name: String? = null,
        var text: String? = null,
        var video: Video? = null,
        var cachedLocalVideo: Video? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readParcelable(Video::class.java.classLoader),
            parcel.readParcelable(Video::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(text)
        parcel.writeParcelable(video, flags)
        parcel.writeParcelable(cachedLocalVideo, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Block> {
        override fun createFromParcel(parcel: Parcel): Block {
            return Block(parcel)
        }

        override fun newArray(size: Int): Array<Block?> {
            return arrayOfNulls(size)
        }
    }
}
