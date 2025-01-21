package com.sf.cwms

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import com.sf.cwms.data.DataInfo
import com.sf.cwms.util.ActivityLifeCycleCallback
import com.sf.cwms.util.SFData
import com.sf.cwms.util.UserLog
import com.sf.cwms.util.UserLog.Companion.userLog


class CWmsApp:   Application() {

    //애플리케이션이 생성될 때 호출된다. 엑티비티나 서비스보다 항상 먼저 호출 되므로 진정한 진입점임.
    //응용프로그램의 시작점임으로 신속히 처리해야함. 여기서 지연되면 시작이 느려짐

    companion object {
        private var instance: Application? = null
        private var sharedPreferences: SharedPreferences? = null
        private var isActive: Boolean = false

        var isTest:Boolean = false   // 장비 제조 업체에게 스캐너 테스트를 위한 프로그램 제공 시 true

        var appStart:Boolean = false;
        var shareData: SFData = SFData(SFData.GRID_COL1)
        var paramData: SFData = SFData(SFData.GRID_COL1)
        var isVerChange:Boolean = false;
        var http : String = "http"
        var hostAddress:String = "1.209.110.181"
        var hostPort:String = ""
        var hostPath:String = "pda"
        var api_url : String = "http://1.209.110.181/pda/"
        var api_apkurl : String = "http://1.209.110.181/Install/apk/pda.apk"
        var api_down : Boolean = false

        var cent_cd : String = ""
        var cent_nm : String = ""
        var user_cd : String = ""
        var user_nm : String = ""
        var user_type : String = ""
        var user_typen : String = ""
        var reg_ip: String = ""
        var login_dt : String = ""
        var prg_id: String = ""
        var api_name: String = ""
        var pda_no: String = ""
        var pda_nm: String = ""
        var action: String = ""
        var loc_cd: String = ""
        var to_date: String = ""
        var car_no: String = ""
        var view_flag:String = "N"
        var pk_grp_no:String = "1"

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun getSharedPreference(): SharedPreferences? {
            return sharedPreferences
        }
        fun setActivityData(sfData:SFData ){
            shareData = sfData
            cent_cd = shareData.getFieldString(DataInfo.cent_cd)
            cent_nm = shareData.getFieldString(DataInfo.cent_nm)
            user_cd = shareData.getFieldString(DataInfo.user_cd)
            user_nm = shareData.getFieldString(DataInfo.user_nm)
            user_type = shareData.getFieldString(DataInfo.user_type)
            user_typen = shareData.getFieldString(DataInfo.user_typen)
            login_dt = shareData.getFieldString(DataInfo.login_dt)
            userLog("CWmsApp cent_cd" + cent_cd)
        }

        fun getActivityData( ):SFData{
            return shareData
        }

        fun isActivityVisible(): Boolean {
            return isActive
        }

        fun Init(){
            prg_id=""
            api_name=""
            action=""
            to_date = ""
            car_no = ""
            view_flag = "20|30"
        }

        fun apiInit(){
            prg_id=""
            api_name=""
            action=""
        }

        fun apiSet(prg_id:String, api_name:String, action:String){
            this.prg_id = prg_id
            this.api_name = api_name
            this.action = action
        }
    }

    override fun onCreate(){
        super.onCreate()
        UserLog.userLog("CrowWms onCreate");
        instance = this
        registerActivityLifecycleCallbacks(  ActivityLifeCycleCallback() )
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        Init()
    }

    //애플리케이션 객체와 모든 컴포넌트가 종료될 때 호출, but,항상 발생되지 않음. 종료처리할 때만 사용됨
    override fun onTerminate() {
        UserLog.userLog("CrowWms onTerminate");
        super.onTerminate()
    }

    // 시스템 메모리 리소스가 부족할때 호출
    override fun onLowMemory() {
        UserLog.userLog("CrowWms onLowMemory");
        super.onLowMemory()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        UserLog.userLog("CrowWms onConfigurationChanged");
        appStart = true
        super.onConfigurationChanged(newConfig)
    }

}