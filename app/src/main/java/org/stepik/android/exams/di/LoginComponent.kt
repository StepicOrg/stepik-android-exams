package org.stepik.android.exams.di

import dagger.Subcomponent
import org.stepik.android.exams.ui.activity.EmptyAuthActivity
import org.stepik.android.exams.ui.activity.LoginActivity
import org.stepik.android.exams.ui.activity.RegisterActivity
import org.stepik.android.exams.ui.dialog.RemindPasswordDialog

@Subcomponent
interface LoginComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): LoginComponent
    }

    fun inject(activity: LoginActivity)
    fun inject(activity: RegisterActivity)
    fun inject(activity: EmptyAuthActivity)
    fun inject(dialog: RemindPasswordDialog)
}