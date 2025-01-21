package com.sf.cwms.util
import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.sf.cwms.databinding.ActivityMainBinding

class ActivityLifeCycleCallback  : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is ActivityBindingLayout) {
            val binding: ActivityMainBinding = DataBindingUtil.setContentView(
                activity,
                layoutId(activity)
            )
            binding.setLifecycleOwner(activity as LifecycleOwner)
            setBinding(activity, binding)

            UserLog.userLog("onActivityCreated ActivityBindingLayout");

        }

        if (activity is ActivityPermission) {
            UserLog.userLog("onActivityCreated ActivityPermission");
            (activity as ActivityPermission).checkPermission(activity)
        }
    }

    override fun onActivityStarted(activity: Activity) {
        UserLog.userLog("onActivityStarted");

    }

    override fun onActivityResumed(activity: Activity) {
        UserLog.userLog("onActivityResumed");

    }

    override fun onActivityPaused(activity: Activity) {
        UserLog.userLog("onActivityPaused");

    }

    override fun onActivityStopped(activity: Activity) {
        UserLog.userLog("onActivityStopped");

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        UserLog.userLog("onActivitySaveInstanceState");

    }

    override fun onActivityDestroyed(activity: Activity) {
        UserLog.userLog("onActivityDestroyed");

    }


    fun layoutId(activity: Activity): Int {
        return (activity as ActivityBindingLayout).getLayout()
    }

    fun setBinding(activity: Activity, binding: ViewDataBinding) {
        (activity as ActivityBindingLayout).setBinding(binding)
    }

}