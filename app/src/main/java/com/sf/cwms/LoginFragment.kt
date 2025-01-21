package com.sf.cwms

import android.view.KeyEvent
import android.view.View
import com.google.gson.Gson
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.data.DataInfo
import com.sf.cwms.data.UserViewModel
import com.sf.cwms.databinding.FragmentLoginBinding
import com.sf.cwms.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// 로그인
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login){
    var nFocused = 0
    var uid:String = ""
    var pwd:String = ""
    var isSave :Boolean = false
    override fun initView() {
        super.initView()
        (getActivity() as MainActivity).hideSystemUI()

    }
    override fun initViewModel() {
        super.initViewModel()
        userLog("Login initViewModel")

        (getActivity() as MainActivity).apply {
            isSave = GetEnv(this, DataInfo.save_user, isSave)
            uid =  GetEnv(this, DataInfo.user_cd, uid)
            pwd =  GetEnv(this, DataInfo.passwd, pwd)
            CWmsApp.pda_no = GetEnv(this, DataInfo.pda_no, "PDA01")
            CWmsApp.pda_nm = GetEnv(this, DataInfo.pda_nm, "지게차#1")

        }
        var util: Utils = Utils()
        CWmsApp.reg_ip = util.getLocalIPAddress()!!
        userLog("Client IP Address=" + CWmsApp.reg_ip)

        binding.apply {
            edId.setText(uid)
            edPwd.setText(pwd)
            ckUserInfo.isChecked = isSave
            btVersion.text = "Version " + AppInfo.VERSION

        }
    }

    override fun initListener() {
        super.initListener()
        userLog("Login initListener")

        binding.edId.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                nFocused = 0
                var scanData =binding.edId.text.toString()

            }
            false
        })
        binding.edPwd.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard()
                nFocused = 0
                var scanData =binding.edId.text.toString()
            }
            false
        })

        binding.run {

            btLogin.setOnClickListener {
                nFocused = 0
                CWmsApp.appStart = false
                loginAuth(edId.text.toString(), edPwd.text.toString() );
            }

            btClose.setOnClickListener {
                (getActivity() as MainActivity).CloseApplication()
            }

            btSetting.setOnClickListener {
                (getActivity() as MainActivity).SelectMenu(AppInfo.setting, "설정")
            }

            btVersion.setOnClickListener {
                nFocused = 0
            }

            btClose.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if ( hasFocus ) edId.requestFocus()
                    userLog("rv.FocusChange" + hasFocus.toString())
                }
            })

            btVersion.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if ( hasFocus ) edId.requestFocus()
                    userLog("btVersion.FocusChange" + hasFocus.toString())
                }
            })
        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
    }

    override fun destoryView() {
        super.destoryView()
    }
    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)

        userLog("login notifyMsg=>" + msgtype + " result=" + result.toString() );
        if( msgtype.equals(AppInfo.MSG_CONFIRM)) {
            if (msg.equals(AppInfo.MSG_APK)) {
                //if (result == true)
                (getActivity() as MainActivity).ProgramDownload("Apk", CWmsApp.api_apkurl)
                //else
                //    LoginAuth()
                return
            }
        }
        if ( msgtype.equals(AppInfo.MSG_COMM)) {
            val resultMsg: ResultMsg2? = getResultRData(msg!!)
            if( showMessage(resultMsg) ) return

            if ( resultMsg!!.result_cd.equals(AppInfo.RESULT_INFO)){
                ShowMessageBox(resultMsg!!.result_msg, AppInfo.MSG_BOX)
                return
            }
            val jList = SFDataList( SFData.GRID_1100, msg, "table")
            for (row in 0 until jList.getSize()) {
                var sfData = jList.getRecord(SFData.GRID_1100, row)
                var ver = sfData!!.getFieldString("PVER")

                CWmsApp.setActivityData( sfData!! )
                CWmsApp.isVerChange = false
                userLog("Rcv Version, " + ver + "," + AppInfo.VERSION)
                if (!ver.equals(AppInfo.VERSION)){
                    CWmsApp.isVerChange = true
                    Confirm("변경된 버전이 존재합니다.  업데이트 시작합니다.", "", AppInfo.MSG_APK)
                    return
                }
                LoginAuth();
            }
        }
        binding.edId.requestFocus()
        binding.edId.selectAll()
        userLog("login notifyMsg  END");
    }

    public fun LoginAuth(){
        val pwd = binding.edPwd.text.toString()
        val isSave = binding.ckUserInfo.isChecked
        //val ipwd = (getActivity() as MainActivity).Sha256encrypt(pwd)
        val rpwd = CWmsApp.getActivityData().getFieldString(DataInfo.passwd)
        userLog("Passwd = " + rpwd + ", pwd= " + pwd)
        if (rpwd.equals(pwd)){
            (getActivity() as MainActivity).run {
                SetEnv(this, DataInfo.user_cd, CWmsApp.user_cd)
                SetEnv(this, DataInfo.passwd, pwd)
                SetEnv(this, DataInfo.save_user, isSave)
                isLogin = true
                Login()
            }
        }
        else{
            ShowMessageBox("비밀번호 오류", "")
        }
    }


    fun loginAuth(uid:String, pwd:String) {
        val userViewModel = UserViewModel()
        userViewModel.action = "login"
        userViewModel.user_cd = uid
        userViewModel.passwd = pwd
        val gson = Gson()
        val json = gson.toJson(userViewModel)
        userLog("loginAuth=" +json);

        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? = apiService.GetLogin(getHeader(), userViewModel)
        callApi(call, AppInfo.login,AppInfo.api_GetLogin, AppInfo.action_login )
    }

    fun showMessage(resultMsg: ResultMsg2?) :Boolean{
        if (resultMsg == null) {
            SoundPlay(context!!, R.raw.err)
            setCustomToast(context, "Error Json Format Type")
            return true
        }
        if (resultMsg.result_cd.equals(AppInfo.O_ERR)) {
            userLog("resultMsg=" + resultMsg)
            SoundPlay(context, R.raw.err)
            ShowMessageBox(resultMsg.result_msg, AppInfo.MSG_BOX);
            return true
        }
        return false;
    }

}