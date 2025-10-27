package handlers

import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import protector.ScreenProtector
import providers.ActivityProvider
import api.FreeraspKMP

internal object FreeraspKMPLifecycleObserver : DefaultLifecycleObserver {
    override fun onResume(owner: LifecycleOwner) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            ActivityProvider.getCurrentActivity()?.let { activity ->
                ScreenProtector.register(activity)
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            ActivityProvider.getCurrentActivity()?.let { activity ->
                ScreenProtector.unregister(activity)
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        FreeraspKMP.cleanup()
    }
}