package com.sf.cwms.util

import android.content.Context
import android.content.pm.PackageInfo
import android.os.Build
import android.provider.Settings

// android device 정보를 얻고 저장한다.
class DeviceInfo(val context: Context) {
    companion object{
        const val PM85 = "PM85"
    }
    // android device id 확인
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    // android devcie model 확인
    fun getDeviceModel(): String {
        return Build.MODEL
    }

    // android devcie os 확인
    fun getDeviceOs(): String {
        return Build.VERSION.RELEASE.toString()
    }

    // android app version 확인
    fun getAppVersion(): String {
        val info: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return info.versionName
    }
}