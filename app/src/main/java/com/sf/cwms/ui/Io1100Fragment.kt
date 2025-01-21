package com.sf.cwms.ui

import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.sf.cwms.CWmsApp
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.databinding.FragmentIo1100Binding
import com.sf.cwms.data.DataInfo
import com.sf.cwms.data.InbViewModel
import com.sf.cwms.util.AppInfo
import com.sf.cwms.util.SFData
import com.sf.cwms.util.SFDataList
import com.sf.cwms.util.SelectDialog
import retrofit2.Call
import retrofit2.http.HeaderMap
import retrofit2.http.Query
/**
 * A simple [Fragment] subclass.
 * Use the [Io1100Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
// 입고 스캔
class Io1100Fragment : BaseFragment<FragmentIo1100Binding>(R.layout.fragment_io1100) {
    var inputStep:Int = 0    // 0 일때 로케이션 스캔  또는 Loc 선택시 -   2  PLT 바코드만 계속 스캔 ,   3  PLT 바코드 스캔 추천 Rack
    var inb_r:InbViewModel = InbViewModel()
    var loc_cd: String = ""
    var plt_bar: String = ""
    var item_class_cd: String = ""
    var item_cd: String = ""
    var plan_dno : String = ""

    override fun initView() {
        super.initView()
        if (CWmsApp.isTest){
            CWmsApp.cent_cd = "A1"
            CWmsApp.api_url = "http://1.209.110.181/PdaApi/"
            userLog("test mode fix cent code")
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        binding.run {
            edPlt.txCap.text = "바코드"
            vwPltCd.txCap.text = "PLT바코드"
            vwFactCd.txCap.text = "생산처"
            vwItemNm.txCap.text = "품목명"
            vwMfgDat.txCap.text = "생산일"
            vwQty.txCap.text = "수량(BOX)"
            vwLocCd.txCap.text = "입고장"
            edPlt.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            edPlt.txCap.background =
                ContextCompat.getDrawable(binding.root.context, R.drawable.caption_edit)
            tvMsg.text = ""
        }
        textFieldInit();
    }

    override fun initListener() {
        super.initListener()
        binding.apply {
            edPlt.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard()
                    val scanData = binding.edPlt.edItem.text.toString()
                    val chk = ScanDataCheck( scanData)
                    if (chk == true) {
                        bScanFlag = true
                        scanData(scanData)
                    }else{
                        textFieldInit()
                        binding.tvMsg.text = scanMfgMsg
                        EditFocus();
                    }
                }
                false
            })
            edPlt.edItem.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    userLog("edPlt focus changed")
                }
            })
        }
        var currentMenu = (getActivity() as MainActivity).currentMenu
        binding.run {

            btPrev.setOnClickListener {
                if (CWmsApp.isTest){
                    (getActivity() as MainActivity). CloseApplication()

                }else {
                    (getActivity() as MainActivity).ReturnMenu()
                }
            }
            /*
            btReg.setOnClickListener {
                InbReg()
            }
            btLocFind.setOnClickListener {
                // 위치 선택
                getEmptyLocList()
            }
             */
            btPrev.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    EditFocus()
                }
            })
            /*
            btLocFind.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    EditFocus()
                }
            })
            btReg.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    EditFocus()
                }
            })
             */
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
        EditFocus()
    }

    fun textFieldInit() {
        binding.run {
            edPlt.edItem.setText("")
            plt_bar = ""
            loc_cd = ""
            vwPltCd.tvValue.text = ""
            vwFactCd.tvValue.text = ""
            vwItemNm.tvValue.text = ""
            vwMfgDat.tvValue.text = ""
            vwQty.tvValue.text = ""
            vwTqty.tvValue.text = ""
            vwTqty.txCap.text ="입고/대상"
            vwLocCd.tvValue.text = ""
            vwLocCd.txCap.text = "입고장"
            inb_r = InbViewModel()
            inputStep = 0
        }
    }

    fun EditFocus()
    {
         binding.edPlt.edItem.requestFocus();
         binding.edPlt.edItem.selectAll();
    }

    fun init()
    {
        textFieldInit()
    }

    fun update()
    {
    }

    fun cancel()
    {
    }

    //바코드 스캔
    // scan_data 의 첫번째 자리 'P', PLT 바코드,  'L', 로케이션 바코드로 인식
    fun scanData(scan_data:String){
        if (scan_data.length < 2) {
            bScanFlag = false
            SoundPlay(context, R.raw.err)
            binding.tvMsg.text =  "바코드를 스캔해 주세요."
            EditFocus();
            return
        }
        var pltbar = scan_data
        var bar_type = scan_data.substring(0,2)
        userLog("bar_type=>" + bar_type)
        if (!bar_type.equals("P-")) {
            pltbar = "P-" + scan_data
        }
        userLog("센타코드=>" + CWmsApp.cent_cd);
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        var action = AppInfo.action_plt_in
        val call: Call<String?>? =
            apiService.InbPltInfo(getHeader(), action, CWmsApp.cent_cd, pltbar, "", "")
        callApi(call, AppInfo.io1100, AppInfo.api_InbPltInfo, action);
    }

    fun InbReg()
    {
        if ( CWmsApp.isTest){
            binding.tvMsg.text ="test mode 에서는 등록할수 없습니다"

            userLog("TEST MODE = InbReg Cancel")
            return
        }
        if (plt_bar == null || plt_bar.length == 0){
            binding.tvMsg.text = "팔레트 바코드를 스캔해 주세요."
            SoundPlay(context, R.raw.err)
            EditFocus()
            return
        }
        inb_r.action = AppInfo.action_save
        inb_r.loc_cd = loc_cd
        inb_r.plt_bar = plt_bar

        val gson = Gson()
        val json = gson.toJson(inb_r)
        userLog("InbReg=" +json);
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? = apiService.InbReg(getHeader(), inb_r)
        callApi(call, AppInfo.io1100, AppInfo.api_InbReg, inb_r.action);
    }

    fun getResult()
    {
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        var action = AppInfo.action_plt_in_rlt
        val call: Call<String?>? =
            apiService.InbPltInfo(getHeader(), action, CWmsApp.cent_cd, "", "", plan_dno)
        callApi(call, AppInfo.io1100, AppInfo.api_InbPltInfo, action);

    }

    fun showMessage(msg:String, msgType:String){
        binding.run{
            tvMsg.text = msg
            EditFocus()
        }
    }

    // Api 수신 메세지
    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
        if ( msgtype.equals(AppInfo.MSG_BOX)){
        }
        if ( msgtype.equals(AppInfo.MSG_COMM)) {
            bScanFlag = false
            val resultMsg: ResultMsg2? = getResultRData(msg!!)
            if (resultMsg == null) {
                SoundPlay(context!!, R.raw.err)
                setCustomToast(context, "Error Json Format Type")
                return
            }
            if (resultMsg.result_cd.equals(AppInfo.O_ERR)) {
                SoundPlay(context, R.raw.err)
                binding.tvMsg.text =resultMsg.result_msg
                EditFocus()
                return
            }
            userLog("notifyMsg1")
            when (CWmsApp.action) {
                AppInfo.action_plt_in -> notifyMsg_pltBarInfo(msg);
                AppInfo.action_plt_in_rlt -> notifyMsg_pltInResult(msg);
                else -> notifyMsg_inbReg(resultMsg);
            }
        }
    }

    // Api 수신 Plt 바코드 정보
    fun notifyMsg_pltBarInfo(msg:String){
        userLog("notifyMsg2")
        val jList = SFDataList( SFData.GRID_1100, msg, "table")
        if ( jList.getSize() == 0){
            SoundPlay(context, R.raw.err)
            binding.edPlt.edItem.setText("")
            binding.tvMsg.text =  "등록된 파렛번호가 아닙니다."
            EditFocus()
            return;

        }

        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_1100, row)
            binding.run {
                plt_bar = sfData!!.getFieldString(DataInfo.plt_bar)
                plan_dno = sfData!!.getFieldString(DataInfo.plan_dno)
                var item = String.format("%s %s",sfData!!.getFieldString(DataInfo.item_cd), sfData!!.getFieldString(DataInfo.item_nm))
                vwPltCd.tvValue.text = plt_bar
                vwItemNm.tvValue.text = item
                vwFactCd.tvValue.text = sfData!!.getFieldString(DataInfo.make_fact_nm);
                vwMfgDat.tvValue.text = sfData!!.getFieldString(DataInfo.mfg_dat) + " / " + sfData!!.getFieldString(DataInfo.dn_flg);
                vwQty.tvValue.text = sfData!!.getFieldString(DataInfo.qty);
                var inb_yn = sfData!!.getFieldString("inb_yn")
                var oub_yn = sfData!!.getFieldString("oub_yn")
                if (oub_yn.equals("Y")) {
                    loc_cd = ""
                    binding.tvMsg.text =" 출고된 파렛입니다."
                    SoundPlay(context, R.raw.err)
                    EditFocus()
                    return
                }
                item_class_cd = sfData!!.getFieldString(DataInfo.item_class_cd)
                item_cd = sfData!!.getFieldString(DataInfo.item_cd)

                if (inb_yn.equals("Y")) {
                    loc_cd = ""
                    vwLocCd.tvValue.text = sfData!!.getFieldString("s_loc_cd")
                    vwLocCd.txCap.text = "현재위치"
                    binding.tvMsg.text ="이미 입고된 파렛입니다."
                    SoundPlay(context, R.raw.err)
                    EditFocus()
                    return
                } else {
                    loc_cd = sfData!!.getFieldString(DataInfo.loc_cd);
                    var loc_nm = sfData!!.getFieldString(DataInfo.loc_nm);
                    SoundPlay(context, R.raw.info)
                    vwLocCd.txCap.text = "입고장"
                    vwLocCd.tvValue.text = loc_nm
                    inb_r.set(AppInfo.action_save, sfData)
                    InbReg()
                }
            }
        }
    }

    // Api 수신 Plt 바코드 정보
    fun notifyMsg_pltInResult(msg:String){
        userLog("notifyMsg_pltInResult")
        val jList = SFDataList( SFData.GRID_1100, msg, "table")
        if ( jList.getSize() == 0){
            EditFocus()
            return;
        }

        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_1100, row)
            binding.run {
                var p_plt = sfData!!.getFieldString("p_plt")
                var i_plt = sfData!!.getFieldString("i_plt")
                vwTqty.tvValue.text =  i_plt + '/' + p_plt
            }
        }
    }

    fun notifyMsg_inbReg(msg:ResultMsg2){
        if ( msg.result_cd == AppInfo.RESULT_ERR){
            SoundPlay(context, R.raw.err)
            var messg = msg.result_msg
            binding.tvMsg.text =messg
            EditFocus()
        }
        else{
            var messg = String.format( plt_bar + " 파렛이 입고 되었습니다.");
            binding.tvMsg.text =messg
            plt_bar = ""
            SoundPlay(context, R.raw.info)
            EditFocus()
            getResult()
        }
     }
}

