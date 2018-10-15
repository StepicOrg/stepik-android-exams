package org.stepik.android.exams.analytic

interface Analytic {
    fun reportAmplitudeEvent(eventName: String, params: Map<String, Any>?)
    fun reportAmplitudeEvent(eventName: String)
    fun setScreenOrientation(orientation: Int)
}