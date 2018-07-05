package org.stepik.android.exams.api.profile.model

import com.google.gson.annotations.SerializedName
import org.stepik.android.exams.data.model.Meta

class EmailAddressesResponse(
        val meta: Meta,
        @SerializedName("email-addresses")
        val emailAddresses: List<EmailAddress>?
) {
    val emailAddress: EmailAddress?
        get() = emailAddresses?.firstOrNull()
}