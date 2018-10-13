package org.stepik.android.exams.analytic

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.support.v4.app.NotificationManagerCompat
import org.stepik.android.exams.configuration.Config
import org.stepik.android.exams.di.AppSingleton
import javax.inject.Inject
import com.amplitude.api.Amplitude
import com.amplitude.api.Identify
import com.crashlytics.android.Crashlytics
import org.json.JSONObject
import org.stepik.android.exams.App

@AppSingleton
class AnalyticImpl
@Inject constructor(
        context: Context,
        config: Config
) : Analytic {
    private val amplitude = Amplitude.getInstance()
            .initialize(context, config.amplitudeApiKey)
            .enableForegroundTracking(App.app)
    init {
        amplitude.identify(Identify()
                .set(AmplitudeAnalytic.Properties.APPLICATION_ID, context.packageName)
                .set(AmplitudeAnalytic.Properties.PUSH_PERMISSION, if (NotificationManagerCompat.from(context).areNotificationsEnabled()) "granted" else "not_granted")
        )
        onSessionStart()
    }

    override fun reportAmplitudeEvent(eventName: String) = reportAmplitudeEvent(eventName, null)

    override fun reportAmplitudeEvent(eventName: String, params: Map<String, Any>?) {
        syncAmplitudeProperties()
        val properties = JSONObject()
        params?.let {
            for ((k, v) in it.entries) {
                properties.put(k, v)
            }
        }
        amplitude.logEvent(eventName, properties)
    }

    private fun syncAmplitudeProperties() {
        setScreenOrientation(Resources.getSystem().configuration.orientation)
    }

    override fun setScreenOrientation(orientation: Int) =
            amplitude.identify(Identify().set(AmplitudeAnalytic.Properties.SCREEN_ORIENTATION, if (orientation == Configuration.ORIENTATION_PORTRAIT) "portrait" else "landscape"))

    private fun onSessionStart() {
        reportAmplitudeEvent(AmplitudeAnalytic.Launch.SESSION_START)
    }

    override fun reportError(message: String, throwable: Throwable) {
        Crashlytics.logException(throwable)
    }
}