package org.stepik.android.exams.core.presenter.contracts


interface RegisterView {
    fun setState(state: State)

    sealed class State {
        object Idle : State()
        object Loading : State()
        object Success : State()
        class Error(val message: String) : State()
        object EmptyEmailError : State()
        object NetworkError : State()
    }
}