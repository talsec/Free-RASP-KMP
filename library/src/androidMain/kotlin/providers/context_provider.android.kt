package providers

import android.app.Application
import android.content.Context
import androidx.startup.Initializer


internal object ContextProvider: Initializer<Unit> {
    private lateinit var appContext: Context

    override fun create(context: Context){
        appContext = context.applicationContext
        (appContext as Application).registerActivityLifecycleCallbacks(ActivityProvider)

        return Unit
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> = emptyList()

    fun getApplicationContext(): Context {
        if(!::appContext.isInitialized){
            throw IllegalStateException("Talsec not initialized. Call Talsec.initialize(context) in your Application.onCreate() first.")
        }
        return appContext
    }
}