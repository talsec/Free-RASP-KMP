package com.freeraspkmp.android.handlers

import android.app.Activity
import app.talsec.rasp.security.api.Talsec
import java.lang.ref.WeakReference

// Keeps the ongoing call detection registered for one activity at a time; called on the main thread only
internal object OngoingCallDetector {

    private var activity: WeakReference<Activity>? = null
    private var restartOnResume = false

    fun start(current: Activity) {
        restartOnResume = false
        if (activity?.get() === current) return
        stop()
        Talsec.startOngoingCallDetection(current)
        activity = WeakReference(current)
    }

    fun stop() {
        restartOnResume = false
        activity?.get()?.let { Talsec.stopOngoingCallDetection(it) }
        activity = null
    }

    fun onActivityResumed(resumed: Activity) {
        if (restartOnResume) start(resumed)
    }

    // A recreated activity (for example after rotation) takes over the detection when it resumes
    fun onActivityDestroyed(destroyed: Activity) {
        if (activity?.get() !== destroyed) return
        val isRecreated = destroyed.isChangingConfigurations
        stop()
        restartOnResume = isRecreated
    }
}
