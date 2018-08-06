package org.stepik.android.exams.di

class ComponentManager(
        private val appCoreComponent: AppCoreComponent
) {
    val loginComponent by lazy {
        appCoreComponent.loginComponentBuilder().build()
    }
}