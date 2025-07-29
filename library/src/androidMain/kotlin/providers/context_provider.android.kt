package providers

import android.content.Context


internal object ContextProvider {
    private lateinit var appContext: Context

    fun initialize(context: Context){
        appContext = context.applicationContext
    }

    fun getApplicationContext(): Context {
        if(!::appContext.isInitialized){
            throw IllegalStateException("Talsec not initialized. Call Talsec.initialize(context) in your Application.onCreate() first.")
        }
        return appContext
    }
}