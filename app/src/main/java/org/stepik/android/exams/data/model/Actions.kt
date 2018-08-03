package org.stepik.android.exams.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.stepik.android.exams.util.readBoolean
import org.stepik.android.exams.util.writeBoolean

class Actions(
        val vote: Boolean = false,
        val delete: Boolean = false,
        @SerializedName("test_section")
        val testSection: String? = null,
        @SerializedName("do_review")
        val doReview: String? = null,
        @SerializedName("edit_instructions")
        val editInstructions: String? = null
) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeBoolean(vote)
        parcel.writeBoolean(delete)
        parcel.writeString(testSection)
        parcel.writeString(doReview)
        parcel.writeString(editInstructions)
    }

    override fun describeContents(): Int = 0
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Actions

        if (vote != other.vote) return false
        if (delete != other.delete) return false
        if (testSection != other.testSection) return false
        if (doReview != other.doReview) return false
        if (editInstructions != other.editInstructions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vote.hashCode()
        result = 31 * result + delete.hashCode()
        result = 31 * result + (testSection?.hashCode() ?: 0)
        result = 31 * result + (doReview?.hashCode() ?: 0)
        result = 31 * result + (editInstructions?.hashCode() ?: 0)
        return result
    }

    companion object CREATOR : Parcelable.Creator<Actions> {
        override fun createFromParcel(parcel: Parcel): Actions = Actions(
                parcel.readBoolean(),
                parcel.readBoolean(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString()
        )

        override fun newArray(size: Int): Array<Actions?> = arrayOfNulls(size)
    }
}