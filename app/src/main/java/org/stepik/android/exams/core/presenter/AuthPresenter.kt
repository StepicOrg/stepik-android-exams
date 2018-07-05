package org.stepik.android.exams.core.presenter
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.stepik.android.exams.api.Api
import org.stepik.android.exams.api.auth.AuthError
import org.stepik.android.exams.api.auth.AuthRepository
import org.stepik.android.exams.core.presenter.contracts.AuthView
import org.stepik.android.exams.data.model.AccountCredentials
import org.stepik.android.exams.data.preference.ProfilePreferences
import org.stepik.android.exams.di.qualifiers.BackgroundScheduler
import org.stepik.android.exams.di.qualifiers.MainScheduler
import org.stepik.android.exams.util.RxOptional
import org.stepik.android.exams.util.then
import retrofit2.HttpException
import javax.inject.Inject

class AuthPresenter
@Inject
constructor(
        private val api: Api,
        private val authRepository: AuthRepository,
        private val profilePreferences: ProfilePreferences,
        @BackgroundScheduler
        private val backgroundScheduler: Scheduler,
        @MainScheduler
        private val mainScheduler: Scheduler
): PresenterBase<AuthView>() {

    override fun destroy() {
        disposable.clear()
    }

    private val disposable = CompositeDisposable()

    private var isSuccess = false

    fun authFakeUser() {
        view?.onLoading()

        disposable.add(createFakeUserRx()
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                        this::onSuccess
                ) {
                    onError(AuthError.ConnectionProblem)
                })
    }

    fun authWithLoginPassword(login: String, password: String) {
        view?.onLoading()

        disposable.add(loginRx(login, password)
                .doOnComplete {
                    profilePreferences.removeFakeUser() // we auth as normal user and can remove fake credentials
                }
                .subscribeOn(backgroundScheduler)
                .observeOn(mainScheduler)
                .subscribe(this::onSuccess, this::handleLoginError))
    }

    private fun handleLoginError(error: Throwable) {
        val authError = if (error is HttpException) {
            if (error.code() == 429) {
                AuthError.TooManyAttempts
            } else {
                AuthError.EmailPasswordInvalid
            }
        } else {
            AuthError.ConnectionProblem
        }
        onError(authError)
    }

    private fun createAccountRx(credentials: AccountCredentials): Completable =
            authRepository.createAccount(credentials.toRegistrationUser())

    private fun loginRx(login: String, password: String): Completable =
            authRepository.authWithLoginPassword(login, password).toCompletable()

    private fun createFakeUserRx(): Completable =
            Single.fromCallable { RxOptional(profilePreferences.fakeUser) }.flatMapCompletable { optional ->
                val credentials = optional.value ?: api.createFakeAccount()
                if (optional.value == null) {
                    profilePreferences.fakeUser = credentials
                    createAccountRx(credentials) then loginRx(credentials.login, credentials.password)
                } else {
                    loginRx(credentials.login, credentials.password).onErrorResumeNext { error ->
                        if (error is HttpException && error.code() == 401) { // on some reason we cannot authorize user with old fake account credential
                            profilePreferences.fakeUser = null // remove old fake user
                            createFakeUserRx() // retry
                        } else {
                            Completable.error(error)
                        }
                    }
                }
            }

    private fun onError(authError: AuthError) {
        view?.onError(authError)
    }

    private fun onSuccess() {
        isSuccess = true
        view?.onSuccess()
    }
    override fun attachView(view: AuthView) {
        super.attachView(view)
        if (isSuccess) view.onSuccess()
    }

}