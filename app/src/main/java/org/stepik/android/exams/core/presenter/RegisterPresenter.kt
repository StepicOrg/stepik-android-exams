package org.stepik.android.exams.core.presenter

import com.arellomobile.mvp.InjectViewState
import com.google.gson.Gson
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.adaptive.api.profile.model.ProfileCompositeError
import org.stepik.android.exams.core.presenter.contracts.RegisterView
import org.stepik.android.exams.data.preference.ProfilePreferences
import org.stepik.android.exams.di.AppSingleton
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import retrofit2.HttpException
import javax.inject.Inject
import org.stepik.android.exams.api.profile.ProfileRepository
import org.stepik.android.exams.util.ValidateUtil
import org.stepik.android.exams.util.then

@AppSingleton
@InjectViewState
class RegisterPresenter
@Inject
constructor(
        private val profileRepository: ProfileRepository,
        private val profilePreferences: ProfilePreferences,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler
): BasePresenter<RegisterView>() {
    private val compositeDisposable = CompositeDisposable()
    private val gson = Gson()

    private var state: RegisterView.State = RegisterView.State.Idle
        set(value) {
            field = value
            viewState.setState(value)
        }

    private fun validate(firstName: String, lastName: String, email: String, password: String): Boolean {
        if (!ValidateUtil.isEmailValid(email)) {
            state = RegisterView.State.EmptyEmailError
            return false
        }

        return true
    }


    fun register(firstName: String, lastName: String, email: String, password: String) {
        if (!validate(firstName, lastName, email, password)) return

        state = RegisterView.State.Loading

        compositeDisposable.add(profileRepository.fetchProfile().flatMap { profile ->
            profile.firstName = firstName
            profile.lastName = lastName

            profileRepository.updateProfile(profile) then
                    profileRepository.updateEmail(email) then
                    profileRepository.fetchProfileWithEmailAddresses().doOnSuccess { profilePreferences.profile = it }.map { it.id }
        }.flatMapCompletable { profileId ->
            val oldPassword = profilePreferences.fakeUser?.password ?: ""
            profileRepository.updatePassword(profileId, oldPassword, password)
        }.subscribeOn(backgroundScheduler).observeOn(mainScheduler).doOnComplete {
            profilePreferences.removeFakeUser()
        }.subscribe({
            state = RegisterView.State.Success
        }, {
            state = if (it is HttpException) {
                val error = gson.fromJson(it.response()?.errorBody()?.string(), ProfileCompositeError::class.java)
                val errorMessage = error?.asList?.filterNotNull()?.firstOrNull()
                if (errorMessage != null) {
                    RegisterView.State.Error(errorMessage)
                } else {
                    RegisterView.State.NetworkError
                }
            } else {
                RegisterView.State.NetworkError
            }
        }))
    }
}