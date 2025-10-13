package protector

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.RequiresApi
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import api.freeraspKMP
import model.FreeRASPEvent
import java.util.function.Consumer
import android.view.WindowManager.SCREEN_RECORDING_STATE_VISIBLE

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
internal object ScreenProtector {

    private const val TAG = "freeraspKMPScreenProtector"
    private const val SCREEN_CAPTURE_PERMISSION = "android.permission.DETECT_SCREEN_CAPTURE"
    private const val SCREEN_RECORDING_PERMISSION = "android.permission.DETECT_SCREEN_RECORDING"
    private var registerdActivites = mutableSetOf<Int>()

    private val screenCaptureCallback = Activity.ScreenCaptureCallback{
        freeraspKMP.emitEvent(FreeRASPEvent.Screenshot)
    }

    private val screenRecordCallback: Consumer<Int> = Consumer<Int> { state ->
        if(state == SCREEN_RECORDING_STATE_VISIBLE) {
            freeraspKMP.emitEvent(FreeRASPEvent.ScreenRecording)
        }
    }

    internal fun register(activity: Activity){
        val activityHash = activity.hashCode()
        if(registerdActivites.contains(activityHash)) return

        registerScreenCapture(activity)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM){
            registerScreenRecording(activity)
        }

        registerdActivites.add(activityHash)
    }

    internal fun unregister(activity: Activity){
        val activityHash = activity.hashCode()
        if(!registerdActivites.contains(activityHash)) return

        unregisterScreenCapture(activity)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM){
            unregisterScreenRecording(activity)
        }

        registerdActivites.remove(activityHash)
    }

    @SuppressLint("MissingPermission")
    internal fun registerScreenCapture(activity: Activity){
        if(!hasPermission(activity.applicationContext, SCREEN_CAPTURE_PERMISSION)){
            reportMissingPermission("screenshot", SCREEN_CAPTURE_PERMISSION)
            return
        }

        activity.registerScreenCaptureCallback(activity.mainExecutor, screenCaptureCallback)
    }

    @SuppressLint("MissingPermission")
    internal fun unregisterScreenCapture(activity: Activity){
        if(!hasPermission(activity.applicationContext, SCREEN_CAPTURE_PERMISSION))
        {
            return
        }

        activity.unregisterScreenCaptureCallback(screenCaptureCallback)
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    internal fun registerScreenRecording(activity: Activity){
        if(!hasPermission(activity.applicationContext, SCREEN_RECORDING_PERMISSION)){
            reportMissingPermission("screen record", SCREEN_RECORDING_PERMISSION)
            return
        }

        val initialState = activity.windowManager.addScreenRecordingCallback(activity.mainExecutor, screenRecordCallback)
        screenRecordCallback.accept(initialState)
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    internal fun unregisterScreenRecording(activity: Activity){
        if(!hasPermission(activity.applicationContext, SCREEN_RECORDING_PERMISSION)){
            return
        }
        activity.windowManager.removeScreenRecordingCallback(screenRecordCallback)
    }

    private fun hasPermission(context: Context, permission: String): Boolean{
        return ContextCompat.checkSelfPermission(
            context, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    internal fun reportMissingPermission(protectionType: String, permission: String)
    {
        Log.e(
            TAG,
            "Failed to register $protectionType callback. Check if $permission permission is granted in AndroidManifest.xml"

        )
    }
}