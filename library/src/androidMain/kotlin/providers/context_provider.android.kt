package com.freeraspkmp.android.providers

import android.app.Application
import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.startup.Initializer
import com.freeraspkmp.android.handlers.FreeraspKMPLifecycleObserver
import com.freeraspkmp.model.exception.FreeraspKMPException

internal object ContextProvider: Initializer<Unit> {
    private lateinit var appContext: Context

    override fun create(context: Context){
        appContext = context.applicationContext
        (appContext as Application).registerActivityLifecycleCallbacks(ActivityProvider)

        ProcessLifecycleOwner.get().lifecycle.addObserver(FreeraspKMPLifecycleObserver)

        return Unit
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    fun getApplicationContext(): Context {
        if(!::appContext.isInitialized){
            throw FreeraspKMPException("freeraspKMP not initialized. It seems the automatic setup via App Startup failed.")
        }
        return appContext
    }
}