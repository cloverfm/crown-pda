package com.sf.cwms

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
import com.sf.cwms.base.BaseActivity
import com.sf.cwms.data.DataInfo
import com.sf.cwms.databinding.ActivityMainBinding
import com.sf.cwms.ui.*
import com.sf.cwms.util.*
import java.io.*
import java.lang.Exception
import java.net.URL

// 각 화면으로 분기 처리
// 버전별 프로그램 업데이트
class MainActivity : BaseActivity<ActivityMainBinding>( R.layout.activity_main ){
    var rackList: ArrayList<SFData> =ArrayList<SFData>()
    var arrayList: ArrayList<SFData> = ArrayList<SFData>()
    var commMsg:String = ""
    val READ_REQUEST_CODE = 42
    val REQUEST_WRITE = 43
    var loginFragment: LoginFragment = LoginFragment()
    var homeFragment: HomeFragment = HomeFragment()
    var io1100Fragment: Io1100Fragment = Io1100Fragment()  // 입고
    var io1200Fragment: Io1200Fragment = Io1200Fragment()
    var io2000Fragment: Io2000Fragment = Io2000Fragment()
    var io2100Fragment: Io2100Fragment = Io2100Fragment()
    var io2101Fragment: Io2101Fragment = Io2101Fragment()
    var io2102Fragment: Io2102Fragment = Io2102Fragment()
    var io2200Fragment: Io2200Fragment = Io2200Fragment()
    var io2201Fragment: Io2201Fragment = Io2201Fragment()
    var io2202Fragment: Io2202Fragment = Io2202Fragment()
    var io3100Fragment: Io3100Fragment = Io3100Fragment()
    var io3200Fragment: Io3200Fragment = Io3200Fragment()
    var io3300Fragment: Io3300Fragment = Io3300Fragment()
    var settingFragment: SettingFragment = SettingFragment()
    var selData:SFData = SFData(SFData.GRID_2000)
    var selData1:SFData = SFData(SFData.GRID_COL1)
    var selData2:SFData = SFData(SFData.GRID_COL1)
    var findOk:Boolean = false
    var oDialog :CustomProgressDialog? = null
    var preMenu: String = ""
    var currentMenu: String = ""
    var currentTitle:String = ""
    var dlgTitle :String = ""
    var step : String = ""
    var dlgGridColSel:Int = SFData.GRID_COLSEL
    var setForm: Boolean = false
    var isLogin:Boolean = false
    var isMode : String = ""


    fun progressShow(msg :String??) {
        oDialog!!.show()
    }

    fun progressHide() {
        try {
            if (oDialog != null) oDialog!!.hide()
        } catch (ex: java.lang.Exception) {
            userLog( "progressHide")
        }
    }
    fun GetStatusEnv()
    {

    }
    fun SetStatusEnv()
    {

    }

    // 서버 address 설정
    fun GetEnv(context: Context) {
        var sTemp = ""
        val prefs = context.getSharedPreferences("RSHARE", Context.MODE_PRIVATE)
        CWmsApp.http = prefs.getString("SVR_HTTP", "http://").toString() //키값, 디폴트값
        CWmsApp.hostAddress = prefs.getString("SVR_IP", "123.140.106.172").toString() //키값, 디폴트값
        if (!CWmsApp.isTest) UserLog.userLog("getEnv=>ServerIP" + CWmsApp.hostAddress)
        CWmsApp.hostPort = prefs.getString("SVR_PORT", "20001").toString() //키값, 디폴트값
        if (!CWmsApp.isTest) UserLog.userLog("getEnv=>serverPort" + CWmsApp.hostPort)
        CWmsApp.hostPath = prefs.getString("SVR_PATH", "PdaApi").toString() //키값, 디폴트값
        if (!CWmsApp.isTest) UserLog.userLog("getEnv=>serverPath" + CWmsApp.hostPath)
        CWmsApp.api_url = CWmsApp.http +  CWmsApp.hostAddress
        CWmsApp.api_apkurl = CWmsApp.http +  CWmsApp.hostAddress
        if (CWmsApp.hostPort.length> 0){
            CWmsApp.api_url += ":"
            CWmsApp.api_apkurl += ":"
            CWmsApp.api_url += CWmsApp.hostPort
            CWmsApp.api_apkurl += CWmsApp.hostPort
        }
        if (CWmsApp.hostPath.length> 0){
            CWmsApp.api_url += "/"
            CWmsApp.api_url += CWmsApp.hostPath
        }

        CWmsApp.api_url += "/"
        CWmsApp.api_apkurl += "/Install/apk/pda.apk"
        if (!CWmsApp.isTest) UserLog.userLog("getEnv=>URl" + CWmsApp.api_url)
        if (!CWmsApp.isTest) UserLog.userLog("getEnv=>APKURl" + CWmsApp.api_apkurl)
    }

    fun SetEnv(context: Context) {
        val pref = context.getSharedPreferences("RSHARE", Context.MODE_PRIVATE)
        val editor = pref.edit()
        if (!CWmsApp.isTest) UserLog.userLog("setEnv=>SVR_HTTP" + CWmsApp.http)
        if (!CWmsApp.isTest) UserLog.userLog("setEnv=>ServerIP" + CWmsApp.hostAddress)
        if (!CWmsApp.isTest) UserLog.userLog("setEnv=>ServerPort" +  CWmsApp.hostPort)
        if (!CWmsApp.isTest) UserLog.userLog("setEnv=>ServerPath" +  CWmsApp.hostPath)
        editor.putString("SVR_HTTP", CWmsApp.http) //키값, 저장값
        editor.putString("SVR_IP", CWmsApp.hostAddress) //키값, 저장값
        editor.putString("SVR_PORT", CWmsApp.hostPort.toString()) //키값, 저장값
        editor.putString("SVR_PATH", CWmsApp.hostPath.toString()) //키값, 저장값
        editor.commit()
    }

    fun GetEnv(context: Context, key:String, defValue:String):String {
        var sTemp = ""
        val prefs = context.getSharedPreferences("RSHARE", Context.MODE_PRIVATE)
        if (!CWmsApp.isTest) UserLog.userLog("GetSyncType=>" + key)
        var tmp = prefs.getString(key, defValue).toString() //키값, 디폴트
        return tmp
    }
    fun GetEnv(context: Context, key:String, defValue:Boolean):Boolean {
        var sTemp = ""
        val prefs = context.getSharedPreferences("RSHARE", Context.MODE_PRIVATE)
        if (!CWmsApp.isTest) UserLog.userLog("GetSyncType=>" + key)
        var tmp = prefs.getBoolean(key, defValue) //키값, 디폴트
        return tmp
    }

    fun SetEnv(context: Context, key:String, savValue:String) {
        val pref = context.getSharedPreferences("RSHARE", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(key, savValue) //키값, 저장값
        editor.commit()
    }
    fun SetEnv(context: Context, key:String, savValue:Boolean) {
        val pref = context.getSharedPreferences("RSHARE", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(key, savValue)
        editor.commit()
    }

    fun setPdaNumber()
    {
        SetEnv(this, DataInfo.pda_no, CWmsApp.pda_no)
        SetEnv(this, DataInfo.pda_nm, CWmsApp.pda_nm)
    }

    override fun initView() {
        super.initView()
        GetEnv(this)
        oDialog = CustomProgressDialog(this@MainActivity)

        if ( CWmsApp.appStart){
            GetStatusEnv()
        }

        userLog("MainActivity InitView")
    }

    override fun initViewModel() {
        super.initViewModel()
        imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if ( CWmsApp.isTest){
            userLog("Test Mode Access")
            setDefaultFragment(io1100Fragment, "TEST")
        }
        else {
            if (isLogin) {
                userLog("isLogin=" + isLogin + "," + currentMenu);
                SelectMenu()
            } else {
                setDefaultFragment(loginFragment, "로그인")
            }
            userLog("MainActivity initViewModel")
        }
    }

    override fun initListener() {
        super.initListener()
        userLog("MainActivity initListener")
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        checkPermission()
        userLog("MainActivity afterOnCreate")
    }

    override fun onDestroy() {
      //  keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
        userLog("MainActivity onDestroy")
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        userLog("뒤로가기 disable")
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        userLog("onUserLeaveHint")
        //finish()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
        }
        userLog("MainActivity onWindowFocusChanged, currentMenu, isLogin =" + hasFocus.toString() + ", " + currentMenu.length.toString() + "," + isLogin.toString())
    }

    public fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun ReturnMenu()
    {
        currentMenu =""
        if ( isLogin == true) {
            CWmsApp.Init()
            showSystemUI()
            replaceFragment(homeFragment, "CROWN WMS");
        }else{
            Logout()
        }
    }

    fun Login()
    {
        currentMenu =""
        CWmsApp.Init()
        showSystemUI()
        replaceFragment(homeFragment, "CROWN WMS");
    }

    fun Logout()
    {
        CWmsApp.Init()
        currentMenu =""
        isLogin = false
        SetStatusEnv()
        hideSystemUI()
        replaceFragment(loginFragment, "로그인");
    }

    public fun CloseApplication(){
        currentMenu =""
        isLogin = false
        SetStatusEnv()
        finishAndRemoveTask()
    }

    public fun CloseApp()
    {
        val dialog = ConfirmDialog()
        dialog.setButtonClickListener(object: ConfirmDialog.OnButtonClickListener{
            override fun onOkClicked() {
                finishAndRemoveTask()
            }
            override fun onCancelClicked() {
            }
        })
        dialog.show(supportFragmentManager, "CustomDialog")
    }

    public fun Confirm(msg:String, title:String, menu:String)
    {
        val dialog = ConfirmDialog()
        dialog.setTitle(msg, title)
        dialog.setButtonClickListener(object: ConfirmDialog.OnButtonClickListener{
            override fun onOkClicked() {
                if ( menu.equals(AppInfo.io1100) ){
                    io1100Fragment.update()
                }
                if ( menu.equals(AppInfo.io1200) ){
                    io1200Fragment.update()
                }
                if ( menu.equals(AppInfo.io2000) ){
                    io2000Fragment.update()
                }
                if ( menu.equals(AppInfo.io2100) ){
                    io2100Fragment.update()
                }
                if ( menu.equals(AppInfo.io2101) ){
                    io2101Fragment.update()
                }
                if ( menu.equals(AppInfo.io2102) ){
                    io2102Fragment.update()
                }
                if ( menu.equals(AppInfo.io2200) ){
                    io2200Fragment.update()
                }
                if ( menu.equals(AppInfo.io2201) ){
                    io2201Fragment.update()
                }
                if ( menu.equals(AppInfo.io2202) ){
                    io2202Fragment.update()
                }
                if ( menu.equals(AppInfo.io3100) ){
                    io3100Fragment.update()
                }
                if ( menu.equals(AppInfo.io3200) ){
                    io3200Fragment.update()
                }
                if ( menu.equals(AppInfo.io3300) ){
                    io3300Fragment.update()
                }
            }
            override fun onCancelClicked() {
                if ( menu.equals(AppInfo.io1100) ){
                    io1100Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io1200) ){
                    io1200Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io2000) ){
                    io2000Fragment.update()
                }
                if ( menu.equals(AppInfo.io2100) ){
                    io2100Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io2101) ){
                    io2101Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io2102) ){
                    io2102Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io2200) ){
                    io2200Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io2201) ){
                    io2201Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io2202) ){
                    io2202Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io3100) ){
                    io3100Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io3200) ){
                    io3200Fragment.cancel()
                }
                if ( menu.equals(AppInfo.io3300) ){
                    io3300Fragment.cancel()
                }

            }
        })
        dialog.show(supportFragmentManager, "CustomDialog")
    }

    fun SelectMenu()
    {
        userLog("SelectMenu====>" + currentMenu)
        setForm = false
        hideSystemUI()
        if (currentMenu.length == 0){
            homeFragment = HomeFragment()
            showSystemUI()

            setDefaultFragment(homeFragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io1100)){
            io1100Fragment = Io1100Fragment()
            setDefaultFragment(io1100Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io1200)){
            io1200Fragment = Io1200Fragment()
            setDefaultFragment(io1200Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io2000)){
            io2000Fragment = Io2000Fragment()
            setDefaultFragment(io2000Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io2100)){
            io2100Fragment = Io2100Fragment()
            setDefaultFragment(io2100Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io2101)){
            io2101Fragment = Io2101Fragment()
            setDefaultFragment(io2101Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io2102)){
            io2102Fragment = Io2102Fragment()
            setDefaultFragment(io2102Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io2200)){
            io2200Fragment = Io2200Fragment()
            setDefaultFragment(io2200Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io2201)){
            io2201Fragment = Io2201Fragment()
            setDefaultFragment(io2201Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io2202)){
            io2202Fragment = Io2202Fragment()
            setDefaultFragment(io2202Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io3100)){
            io3100Fragment = Io3100Fragment()
            setDefaultFragment(io3100Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io3200)){
            io3200Fragment = Io3200Fragment()
            setDefaultFragment(io3200Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.io3300)){
            io3300Fragment = Io3300Fragment()
            setDefaultFragment(io3300Fragment, currentTitle)
        }
        else if (currentMenu.equals(AppInfo.setting)){
            settingFragment = SettingFragment()
            setDefaultFragment(settingFragment, currentTitle)
        }
    }

    fun SelectMenu(mType:String, title:String)
    {
        currentMenu = mType;
        setForm = false
        UserLog.userLog("SelectMenu============>" + mType);
        currentTitle = title
        hideSystemUI()
        if (mType.length == 0){
            SetStatusEnv();
            homeFragment = HomeFragment()
            showSystemUI()
            replaceFragment(homeFragment, currentTitle)
            return;
        }
        else if (mType.equals(AppInfo.io1100)){
            currentMenu = AppInfo.io1100
            SetStatusEnv();
            io1100Fragment = Io1100Fragment()
            replaceFragment(io1100Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io1200)){
            currentMenu = AppInfo.io1200
            SetStatusEnv();
            io1200Fragment = Io1200Fragment()
            replaceFragment(io1200Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io2000)){
            UserLog.userLog("2100 SelectMenu============>mType===" + mType);
            currentMenu = AppInfo.io2000
            SetStatusEnv();
            io2000Fragment = Io2000Fragment()
            replaceFragment(io2000Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io2100)){
            UserLog.userLog("2100 SelectMenu============>mType===" + mType);
            currentMenu = AppInfo.io2100
            SetStatusEnv();
            io2100Fragment = Io2100Fragment()
            replaceFragment(io2100Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io2101)){
            currentMenu = AppInfo.io2101
            SetStatusEnv();
            io2101Fragment = Io2101Fragment()
            replaceFragment(io2101Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io2102)){
            currentMenu = AppInfo.io2102
            SetStatusEnv();
            io2102Fragment = Io2102Fragment()
            replaceFragment(io2102Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io2200)){
            UserLog.userLog("2200SelectMenu============>mType===" + mType);

            currentMenu = AppInfo.io2200
            SetStatusEnv();
            io2200Fragment = Io2200Fragment()
            replaceFragment(io2200Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io2201)){
            currentMenu = AppInfo.io2201
            SetStatusEnv();
            io2201Fragment = Io2201Fragment()
            replaceFragment(io2201Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io2202)){
            currentMenu = AppInfo.io2202
            SetStatusEnv();
            io2202Fragment = Io2202Fragment()
            replaceFragment(io2202Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io3100)){
            userLog("3100 Call")
            currentMenu = AppInfo.io3100
            SetStatusEnv();
            io3100Fragment = Io3100Fragment()
            replaceFragment(io3100Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io3200)){
            currentMenu = AppInfo.io3200
            SetStatusEnv();
            io3200Fragment = Io3200Fragment()
            replaceFragment(io3200Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.io3300)){
            currentMenu = AppInfo.io3300
            SetStatusEnv();
            io3300Fragment = Io3300Fragment()
            replaceFragment(io3300Fragment, currentTitle)
        }
        else if (mType.equals(AppInfo.setting)){
            currentMenu = AppInfo.setting
            SetStatusEnv();
            settingFragment = SettingFragment()
            replaceFragment(settingFragment, currentTitle)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val str = String.format("%d, %d, RESULT_OK %d %s", requestCode, resultCode, RESULT_OK, data)
        userLog(str)
        when (requestCode) {
            READ_REQUEST_CODE -> if (resultCode == RESULT_OK && data != null) {
                data.data?.also { uri ->
                }
            }else{
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun SelectDlg()
    {
        val dialog = SelectDialog()
        dialog.setButtonClickListener(object: SelectDialog.OnButtonClickListener{
            override fun onButton1Clicked(param:SFData) {

            }

            override fun onButton2Clicked() {
            }
            override fun onButton3Clicked() {
            }
        })
        dialog.show(supportFragmentManager, "CustomDialog")

    }
    fun ProgramInstall(mFile:String)
    {
        userLog("programInstall=" + mFile)
        try {
            val apkfile = File(mFile)
            val apkUri = FileProvider.getUriForFile(
                this@MainActivity, BuildConfig.APPLICATION_ID + ".fileprovider", apkfile
            )
            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)

            intent.data = apkUri
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            this@MainActivity.startActivity(intent)

        } catch (e: Exception) {
            Log.d("Download File Async", "error:" + e.message)
        }

    }

    fun ProgramDownload(rcvType:String, api_url:String){
        object : ThreadTask<String, String, Int>() {
            override var mArgument1 = ""
            override var mArgument2 = ""
            override var mResult = 0
            private var mFile: String? = null
            private var bOK: Boolean? = null
            override fun onPreExecute() {
                progressShow("Wait")
            }
            override fun doInBackground(arg1: String, arg2:String ): Int {
                // arg = rcvType json, apk, arg2  = api_url
                var count = 0
                bOK = true
                try {
                    userLog("ProgramDownload Url" + arg2)
                    val url = URL(arg2)
                    val conexion = url.openConnection()
                    conexion.connect()
                    val lenghtOfFile = conexion.contentLength
                    val input: InputStream = BufferedInputStream(url.openStream())
                    val data = ByteArray(1024)
                    var total: Long = 0
                    if ( arg1.equals("json")){
                        var recvData = ""
                        var count = 0
                        while (input.read(data).also { count = it } != -1) {
                            total += count.toLong()
                            val str = String.format("%d", total)
                            UserLog.userLog(str)
                       //     showProgressbar("" + (total * 100 / lenghtOfFile).toInt() + str)
                            recvData += data.toString(Charsets.UTF_8)
                        }
                    }
                    else {
                        mFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
                        mFile += "/PDA.APK"
                        UserLog.userLog("filename>$mFile")
                        val output: OutputStream = FileOutputStream(mFile)
                        while (input.read(data).also { count = it } != -1) {
                            total += count.toLong()
                            val str = String.format("%d", total)
                       //     showProgressbar("" + (total * 100 / lenghtOfFile).toInt() + str)
                            output.write(data, 0, count)
                        }
                        output.flush()
                        output.close()
                    }
                    input.close()

                    userLog("File Received OK")
                } catch (e: InterruptedException) {
                    bOK = false
                    userLog("Download File Async," + e.localizedMessage)
                    e.printStackTrace()
                } catch (e: IOException) {
                    bOK = false
                    e.printStackTrace()
                    userLog("Download File Async," + e.localizedMessage)
                }
                return if (bOK == false) 0 else 1
            }
            override fun onPostExecute(result: Int) {
                progressHide()
                userLog("onPostExecute=" + mFile)
                var bResult = if (result == 1) true else false
                if (bResult){
                    userLog("onPostExecute=" + currentMenu)
                    ProgramInstall(mFile!!)
                }
            }
        }.execute(rcvType, api_url)
    }


}