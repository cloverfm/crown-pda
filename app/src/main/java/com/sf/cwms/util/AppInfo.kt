package com.sf.cwms.util

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import com.sf.cwms.R
import com.sf.cwms.util.UserLog.Companion.userLog
import java.net.Inet4Address
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

public class AppInfo (val context: Context)  {
    companion object{
        const val VERSION = "1.3.7"
        const val MSG_SCAN = "SCAN"
        const val MSG_RFID = "RFID"
        const val MSG_KEY = "KEY"
        const val MSG_COMM = "COMM"
        const val MSG_VIEW = "VIEW"
        const val MSG_SAVE = "SAVE"
        const val MSG_BOX = "BOX"
        const val MSG_CONFIRM = "MSG_CONFIRM"
        const val MSG_INFO = "INFO"
        const val MSG_VER = "MSG_VER"
        const val MSG_APK = "MSG_APK"
        const val sync_copy = "copy"
        const val sync_comm = "comm"

        const val login = "login"
        const val home = "home"
        const val io1100 = "io1100"
        const val io1200 = "io1200"
        const val io2000 = "io2000"
        const val io2100 = "io2100"
        const val io2101 = "io2101"
        const val io2102 = "io2102"
        const val io2200 = "io2200"
        const val io2201 = "io2201"
        const val io2202 = "io2202"
        const val io3100 = "io3100"
        const val io3200 = "io3200"
        const val io3300 = "io3300"
        const val setting = "setting"

        const val scan_field_plt_bar = "plt_bar"
        const val scan_field_loc_cd = "loc_cd"

        const val action_login = "login"
        const val action_plt_in = "plt_in"  // 입고대상 파렛정보
        const val action_plt_in_rlt = "plt_in_rlt" // 입고품목별 진행 수량
        const val action_plt_bar = "plt_bar"

        const val mode_plt = "PLT"
        const val mode_auto = "AUTO"
        const val mode_hold = "HOLD"

        const val action_auto_plt_bar = "auto_plt_bar"

        const val action_plt_loc = "plt_loc"
        const val action_save = "save"
        const val action_list = "list"
        const val action_qty = "qty"
        const val action_cancel = "cancel"
        const val action_delete = "delete"
        const val action = "action";
        const val action_combo = "combo";
        const val action_loc_cd = "loc_cd";
        const val action_loc_info = "loc_info";
        const val action_item = "item";
        const val action_cust = "cust";
        const val action_update = "update";
        const val action_confirm = "confirm";
        const val action_create = "create";
        const val action_reg = "reg";
        const val action_print = "print";
        const val action_id_no = "id_no";
        const val action_plan = "plan"
        const val action_plt_bars = "plt_bars"
        const val action_mega = "mega"
        const val action_major = "major"
        const val action_item_class = "item_class"
        const val action_cust_group = "cust_group"
        const val action_car_no = "car_no"
        const val action_sub_cd1 = "sub_cd1"
        const val action_pk_no = "pk_no"
        const val action_oub_plt_no = "oub_plt_no"
        const val action_result_plt_bar = "result_plt_bar"

        const val api_InbLocList = "InbLocList"
        const val api_GetLogin = "GetLogin"
        const val api_GetLocation = "GetLocation"
        const val api_GetCombo = "GetCombo"
        const val api_StockList = "StockList"
        const val api_MoveReg = "MoveReg"
        const val api_InbPltInfo = "InbPltInfo"
        const val api_InbReg = "InbReg"
        const val api_PickList = "PickList"
        const val api_PKList = "PKList"
        const val api_PickReg = "PickReg"
        const val api_OubList = "OubList"
        const val api_OubPList = "OubPList"
        const val api_OubPltList= "OubPltList"
        const val api_OubReg = "OubReg"
        const val api_OubCancel = "OubCancel"
        const val api_OubConfirm = "OubConfirm"

        const val bar_type_plt = "P"
        const val bar_type_loc_cd = "L"

        const val DEV = "DEV"
        const val OLD = "OLD"
        const val DEV_MODE = DEV // OLD

        const val DBMS = " "
        const val START_DATA = "JSON>"
        const val CALL = "EXECUTE "
        const val result_cd = "result_cd"
        const val result_msg = "result_msg"
        const val table = "table"

        const val RESULT_CD = "result_cd"
        const val RESULT_MSG = "result_msg"
        const val TABLE = "table"
        const val INFTYPE = "inftype"
        const val MST = "mst"
        const val DAT = "dat"
        const val ROW = "row"
        const val SROW = "srow"
        const val COUNT = "count"

        const val O_ERR = "0"
        const val O_OK = "1"
        const val RESULT_OK = "1"
        const val RESULT_INFO = "2"
        const val RESULT_ERR = "0"

        const val _TEST_ = 1
        const val _LIVE_ = 0
        const val _DEBUG_ = 1
    }

    fun getSystemLanguage(): String {
        val systemLocale: Locale

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            systemLocale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            systemLocale = context.getResources().getConfiguration().locale;
        }
        return systemLocale.language // ko
    }

    fun getAppVersion():Int{

        return 0;
        //return BuildConfig.VERSION_CODE
    }

    fun getAppVersionName():String{

        return "1.0"
            //return BuildConfig.VERSION_NAME
    }

    fun CurrentDate(): String {

        val mSimpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val currentTime = Date()
        return mSimpleDateFormat.format(currentTime)

    }

    fun currentDateTime():String{

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val formatted = current.format(formatter)
        return formatted

    }


    fun CurrentTime(): String {
        val mSimpleDateFormat =
            SimpleDateFormat("HH:mm:ss", Locale.KOREA)
        val currentTime = Date()
        return mSimpleDateFormat.format(currentTime)
    }

    public fun ErrSound()
    {
        val player: MediaPlayer
        player = MediaPlayer.create(context, R.raw.err)
        player.start()
    }
    public fun OKSound()
    {
        val player: MediaPlayer
        player = MediaPlayer.create(context, R.raw.info)
        player.start()
    }
    public fun getLocalIPAddress(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface = en.nextElement()
                val enu = networkInterface.inetAddresses
                while (enu.hasMoreElements()) {
                    val inetAddress = enu.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }
}