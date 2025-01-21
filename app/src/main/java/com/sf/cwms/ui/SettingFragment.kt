package com.sf.cwms.ui
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.sf.cwms.CWmsApp
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.data.DataInfo
import com.sf.cwms.data.InbViewModel
import com.sf.cwms.databinding.FragmentSettingBinding
import com.sf.cwms.util.AppInfo
import com.sf.cwms.util.SFData
import com.sf.cwms.util.SFDataList
import retrofit2.Call

class SettingFragment : BaseFragment<FragmentSettingBinding>(R.layout.fragment_setting) {
    var inb_r: InbViewModel = InbViewModel()
    var httpType:String = ""
    var serverIp:String = ""
    var serverPort:String = ""
    var serverPath:String = ""

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        binding.run {
            edIp.txCap.text = "서버 IP"
            edIp.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            edIp.txCap.background = ContextCompat.getDrawable(binding.root.context, R.drawable.caption_edit)

            edPort.txCap.text = "서버 포트"
            edPort.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            edPort.txCap.background = ContextCompat.getDrawable(binding.root.context, R.drawable.caption_edit)

            edPath.txCap.text = "경로"
            edPath.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            edPath.txCap.background = ContextCompat.getDrawable(binding.root.context, R.drawable.caption_edit)
        }
        textFieldInit();
    }

    override fun initListener() {
        super.initListener()
        binding.apply {
            edIp.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard()
                    serverIp = binding.edIp.edItem.text.toString()
                    bScanFlag = true
                }
                false
            })

            edPort.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard()
                    serverPort = binding.edIp.edItem.text.toString()
                    bScanFlag = true
                }
                false
            })

            edPath.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard()
                    serverPath = binding.edPath.edItem.text.toString()
                    bScanFlag = true
                }
                false
            })
        }
        var currentMenu = (getActivity() as MainActivity).currentMenu
        binding.run {
            btPrev.setOnClickListener {
                (getActivity() as MainActivity).ReturnMenu()
            }
            btReg.setOnClickListener {
                Confirm("변경한 내용을 저장하시겠습니까?", "알림", AppInfo.MSG_SAVE)
            }
        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        userLog("io1100 AfterOnCreate " + CWmsApp.appStart)
        init()
        inb_r.inb_no = ""
        if (CWmsApp.appStart) {
            //binding.spRackNo.edItem.setSelection(RapidApp.loc_pos)
        }
    }
    // Api 수신 메세지
    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
      //  if ( msgtype.equals(AppInfo.MSG_SAVE)) {
            if ( result == true){
                update()

            }
      //  }
    }

    fun textFieldInit() {
        binding.run {
            if (CWmsApp.http.equals("http://")){
                vwSel.rbHttp.isChecked = true
            }
            if (CWmsApp.http.equals("https://")){
                vwSel.rbHttps.isChecked = true
            }
            edIp.edItem.setText(CWmsApp.hostAddress);
            edPort.edItem.setText(CWmsApp.hostPort);
            edPath.edItem.setText(CWmsApp.hostPath);
        }
    }

    fun init()
    {
    }

    fun update()
    {
        binding.run{
            if ( vwSel.rbHttp.isChecked == true)
                CWmsApp.http = "http://"
            else CWmsApp.http = "https://"
            CWmsApp.hostAddress = edIp.edItem.getText().toString()
            CWmsApp.hostPort = edPort.edItem.getText().toString()
            CWmsApp.hostPath = edPath.edItem.getText().toString()
        }
        (getActivity() as MainActivity).run{
            SetEnv(this )
            GetEnv(this)
            ApiServiceGenerator.rebuild()
        }
        textFieldInit()

    }

    fun cancel()
    {
    }

    fun showMessage(msg:String, msgType:String){
        binding.run{
            tvMsg.text = msg
        }
    }
}