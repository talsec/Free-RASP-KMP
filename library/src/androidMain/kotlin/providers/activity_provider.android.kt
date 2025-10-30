package com.freeraspkmp.android.providers

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

internal object ActivityProvider : Application.ActivityLifecycleCallbacks {

    private var currentActivity: WeakReference<Activity>? = null

    fun getCurrentActivity(): Activity? {
        return currentActivity?.get()
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity?.get() == activity){
            currentActivity?.clear()
        }
    }

    override fun onActivityStarted(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {}

}