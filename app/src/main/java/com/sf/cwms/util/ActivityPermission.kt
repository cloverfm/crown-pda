package com.sf.cwms.util
import android.Manifest
import android.content.Context
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

interface ActivityPermission {
    fun checkPermission(context: Context?) {
        Dexter.withContext(context)
            .withPermissions( // 외부 저장소 권한
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            )
            .withListener(object : MultiplePermissionsListener {
                // 권한 여부를 다 묻고 실행되는 메서드
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    }
                }

                // 이전 권한 여부를 거부한 권한이 있으면 실행되는 메서드
                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken?
                ) {
                    // 거부한 권한명이 저장된 List - list

                    // 권한 거부시 앱 정보 설정 페이지를 띄우기 위한 메서드
                    showSettingDialog()
                }
            })
            .check()
    }

    fun showSettingDialog()

    fun openSettings()

}