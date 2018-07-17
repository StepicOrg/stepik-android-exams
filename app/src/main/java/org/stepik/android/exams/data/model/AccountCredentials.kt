package org.stepik.android.exams.data.model

import com.google.gson.annotations.SerializedName

data class AccountCredentials(@SerializedName("login") val login: String,
                              @SerializedName("password") val password: String,
                              @SerializedName("firstName") val firstName: String,
                              @SerializedName("lastName") val lastName: String) {
    fun toRegistrationUser() = RegistrationUser(firstName, lastName, login, password)
}