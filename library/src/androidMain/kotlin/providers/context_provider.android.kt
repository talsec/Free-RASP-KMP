package providers

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import androidx.lifecycle.ProcessLifecycleOwner
import handlers.freeraspKMPLifecycleObserver
import model.exception.FreeRASPException


internal object ContextProvider: Initializer<Unit> {
    private lateinit var appContext: Context

    override fun create(context: Context){
        appContext = context.applicationContext
        (appContext as Application).registerActivityLifecycleCallbacks(ActivityProvider)

        ProcessLifecycleOwner.get().lifecycle.addObserver(freeraspKMPLifecycleObserver)

        return Unit
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    fun getApplicationContext(): Context {
        if(!::appContext.isInitialized){
            throw FreeRASPException("freeraspKMP not initialized. It seems the automatic setup via App Startup failed.")
        }
        return appContext
    }
}