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
import com.sf.cwms.databinding.FragmentIo3100Binding
import com.sf.cwms.util.AppInfo
import com.sf.cwms.util.SFData
import com.sf.cwms.util.SFDataList
import com.sf.cwms.util.SelectDialog
import retrofit2.Call

class Io3100Fragment : BaseFragment<FragmentIo3100Binding>(R.layout.fragment_io3100) {
    var inb_r:InbViewModel = InbViewModel()
    var loc_cd: String = ""
    var plt_bar: String = ""
    var item_class_cd: String = ""
    var item_cd: String = ""

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        binding.run {
            edPlt.txCap.text = "PLT바코드"
            vwItemNm.txCap.text = "품목"
            vwMfgDat.txCap.text = "생산일"
            vwDnFlg.txCap.text = "주/야"
            vwQty.txCap.text = "수량(BOX)"
            vwLocCd.txCap.text = "현재위치"
            edLocCd.txCap.text = "이동위치"
            edPlt.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            edLocCd.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            edPlt.txCap.background =
                ContextCompat.getDrawable(binding.root.context, R.drawable.caption_edit)
            edLocCd.txCap.background =
                ContextCompat.getDrawable(binding.root.context, R.drawable.caption_edit)
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
                            scanData(AppInfo.Companion.scan_field_plt_bar, scanData)
                        }else{
                            ShowMessageBox(scanMfgMsg, "edBar")
                        }

                }
                false
            })
            edLocCd.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                        hideKeyboard()
                        val scanData = binding.edLocCd.edItem.text.toString()
                        bScanFlag = true
                        scanData(AppInfo.Companion.scan_field_loc_cd, scanData)
                }
                false
            })

            edPlt.edItem.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                }
            })

        }
        var currentMenu = (getActivity() as MainActivity).currentMenu
        binding.run {
            btPrev.setOnClickListener {
                (getActivity() as MainActivity).run{
                    if ( preMenu == AppInfo.io3200){
                        preMenu = ""
                        SelectMenu(AppInfo.io3200, "재고조회")
                    }
                    else
                        ReturnMenu()
                }
            }
            btReg.setOnClickListener {
                MoveReg()
            }
            btLocFind.setOnClickListener{
                getEmptyLocList()
            }

            btPrev.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if ( plt_bar.length > 0 )
                        EditFocus(AppInfo.scan_field_loc_cd)
                    else
                        EditFocus(AppInfo.scan_field_plt_bar)
                }
            })
            btLocFind.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if ( plt_bar.length > 0 )
                        EditFocus(AppInfo.scan_field_loc_cd)
                    else
                        EditFocus(AppInfo.scan_field_plt_bar)
                }
            })
            btReg.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if ( plt_bar.length > 0 )
                        EditFocus(AppInfo.scan_field_loc_cd)
                    else
                        EditFocus(AppInfo.scan_field_plt_bar)
                }
            })

        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        userLog("io3100 AfterOnCreate " + CWmsApp.appStart)
        init()
        if (CWmsApp.appStart) {
            //binding.spRackNo.edItem.setSelection(RapidApp.loc_pos)
        }
    }

    fun textFieldInit() {
        binding.run {
            edPlt.edItem.setText("")
            vwItemNm.tvValue.text = ""
            vwMfgDat.tvValue.text = ""
            vwDnFlg.tvValue.text = ""
            vwQty.tvValue.text = ""
            vwLocCd.tvValue.text = ""
            edLocCd.edItem.setText("")
            inb_r = InbViewModel()

        }
    }

    fun EditFocus(scan_field :String)
    {
        if ( scan_field.equals(AppInfo.scan_field_plt_bar)){
            binding.edPlt.edItem.requestFocus();
            binding.edPlt.edItem.selectAll();
        }
        else{
            binding.edLocCd.edItem.requestFocus();
            binding.edLocCd.edItem.selectAll();
        }
    }

    fun init()
    {
        if ( (getActivity() as MainActivity).preMenu == AppInfo.io3200) {
            userLog("init")

            plt_bar =  (getActivity() as MainActivity).selData1.getFieldString(DataInfo.plt_bar)

            userLog("PLT_BAR" + plt_bar)

            scanData("", plt_bar)
        }
        else{

            EditFocus(AppInfo.scan_field_plt_bar)

        }

    }

    fun update()
    {
    }
    fun cancel()
    {
    }
    //바코드 스캔
    // scan_data 의 첫번째 자리 'P', PLT 바코드,  'L', 로케이션 바코드로 인식
    fun scanData(scan_field:String, scan_data:String){
        if (scan_data.length == 0) {
            bScanFlag = false
            var msg =  "팔레트 바코드를 스캔해 주세요."
            if (scan_field == AppInfo.scan_field_loc_cd) msg = "위치바코드를 스캔해주세요."
            SoundPlay(context!!, R.raw.err)

            showMessage(msg, "");
            return
        }
        var bar_type = scan_data.substring(0,1);

        if ( bar_type.equals(AppInfo.bar_type_plt)) {
            userLog("센타코드=>" + CWmsApp.cent_cd);
            val apiService = ApiServiceGenerator.createService(ApiService::class.java)
            var action = AppInfo.action_plt_bar
            val call: Call<String?>? =
                apiService.StockList(getHeader(), action, CWmsApp.cent_cd, scan_data, "", "")
            callApi(call, AppInfo.io3100, AppInfo.api_StockList, action);
        }
        else{
            userLog("위치 바코드 스캔")
            var action = AppInfo.action_loc_info
            val apiService = ApiServiceGenerator.createService(ApiService::class.java)
            val call: Call<String?>? =
                apiService.GetLocation(getHeader(), action, CWmsApp.cent_cd, "", scan_data, "")
            callApi(call, AppInfo.io3100, AppInfo.api_GetLocation, action);
        }
    }

    fun getEmptyLocList(){
        if ( item_class_cd.length == 0 ) {
            binding.tvMsg.text = "팔레트 바코드를 스캔해 주세요."
            SoundPlay(context!!, R.raw.ok)
            EditFocus(AppInfo.scan_field_plt_bar);
            return
        }

        val action = "list"
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.InbLocList(getHeader(), action, CWmsApp.cent_cd, item_cd, item_class_cd);
        callApi(call, AppInfo.io1100, AppInfo.api_InbLocList, "emptylist");
    }

    fun MoveReg()
    {
        if (plt_bar == null || plt_bar.length == 0){
            SoundPlay(context, R.raw.err)
            showMessage("팔레트 바코드를 스캔해 주세요.", AppInfo.scan_field_plt_bar);
            return
        }
        if (loc_cd == null || loc_cd.length == 0){
            SoundPlay(context, R.raw.err)
            showMessage("위치를 스캔해 주세요.", AppInfo.scan_field_loc_cd);
            return
        }
        inb_r.action = AppInfo.action_save
        inb_r.loc_cd = loc_cd
        inb_r.plt_bar = plt_bar

        val gson = Gson()
        val json = gson.toJson(inb_r)

        userLog("MoveReg=" +json)

        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? = apiService.MoveReg(getHeader(), inb_r)
        callApi(call, AppInfo.io3100, AppInfo.api_MoveReg, inb_r.action);
    }

    fun showMessage(msg:String, msgType:String){
        binding.run{
            userLog("msg:0" + msg)
            tvMsg.text = msg
            EditFocus(msgType)
        }
    }

    // Api 수신 메세지
    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
        if ( msgtype.equals(AppInfo.MSG_BOX)){
            //  if (msg.equals("plt_bar")){
            //      EditFocus(scan)
            //  }
        }
        if ( msgtype.equals(AppInfo.MSG_COMM)) {
            bScanFlag = false
            val resultMsg: ResultMsg2? = getResultRData(msg!!)
            if (resultMsg == null) {
                SoundPlay(context!!, R.raw.err)
                //  showMessage("Error Json Format Type", AppInfo.RESULT_ERR);
                setCustomToast(context, "Error Json Format Type")
                return
            }
            if (resultMsg.result_cd.equals(AppInfo.O_ERR)) {
                SoundPlay(context, R.raw.err)
                if ( CWmsApp.action.equals( AppInfo.action_plt_bar))
                    showMessage(resultMsg.result_msg, AppInfo.scan_field_plt_bar);
                else showMessage(resultMsg.result_msg, AppInfo.scan_field_loc_cd);
                return
            }
            userLog("notifyMsg1" + CWmsApp.action)
            when (CWmsApp.action) {
                AppInfo.action_plt_bar -> notifyMsg_pltBarInfo(msg);
                AppInfo.action_loc_info -> notifyMsg_pltBarLocInfo(msg);
                "emptylist" -> notifyMsg_emptyloc(msg)
                else -> notifyMsg_MoveReg(resultMsg);
            }
        }
    }

    // Api 수신 Plt 바코드 정보
    fun notifyMsg_pltBarInfo(msg:String){
        userLog("notifyMsg2")
        val jList = SFDataList( SFData.GRID_1100, msg, "table")
        if ( jList.getSize() == 0){
            SoundPlay(context, R.raw.err)
            showMessage("입고된 파렛트가 아닙니다.", AppInfo.scan_field_plt_bar)
            return
        }
        SoundPlay(context, R.raw.info)
        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_1100, row)
            binding.run {
                plt_bar = sfData!!.getFieldString(DataInfo.plt_bar)
                item_class_cd = sfData!!.getFieldString(DataInfo.item_class_cd)
                item_cd = sfData!!.getFieldString(DataInfo.item_cd)
                var item = String.format("%s %s",sfData!!.getFieldString(DataInfo.item_cd)
                    ,sfData!!.getFieldString(DataInfo.item_nm))
                vwItemNm.tvValue.text = item
                vwMfgDat.tvValue.text = sfData!!.getFieldString(DataInfo.mfg_dat);
                vwDnFlg.tvValue.text = sfData!!.getFieldString(DataInfo.dn_flg);
                vwQty.tvValue.text = sfData!!.getFieldString(DataInfo.qty);
                var oub_yn = sfData!!.getFieldString("oub_yn")
                if (oub_yn.equals("Y")) {
                    loc_cd = ""
                    binding.tvMsg.text =" 출고된 파렛입니다."
                    SoundPlay(context, R.raw.err)
                    binding.edPlt.edItem.requestFocus()
                    binding.edPlt.edItem.selectAll()
                    return
                }

                vwLocCd.tvValue.text = sfData!!.getFieldString(DataInfo.loc_cd);
                SoundPlay(context, R.raw.err)
                showMessage("이동할 위치를 스캔하세요", AppInfo.scan_field_loc_cd)
                return
            }
        }
    }

    fun notifyMsg_pltBarLocInfo(msg:String){
        userLog("Log ........ notifyMsg_pltBarLocInfo")
        val jList = SFDataList( SFData.GRID_1100, msg, "table")
        if ( jList.getSize() == 0){
            SoundPlay(context, R.raw.err)
            showMessage("등록된 로케이션이 아닙니다.", AppInfo.scan_field_loc_cd)
            return
        }
        SoundPlay(context, R.raw.info)
        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_1100, row)
            var loc_type =sfData!!.getFieldString("loc_type")
            var in_loc_cd = sfData!!.getFieldString("loc_cd")
            var loc_nm = sfData!!.getFieldString(DataInfo.loc_nm);
            var tmp1 =sfData!!.getFieldString("item_class")
            var tmp2 =sfData!!.getFieldString("v_plt")
            if (  loc_type.equals("05") ){
                binding.edPlt.edItem.setText("")
                binding.tvMsg.text = in_loc_cd + " 사용할수 없는 위치입니다."
                EditFocus(AppInfo.scan_field_loc_cd)
                return
            }
            if ( loc_type.equals("03")){
                if (tmp2.toInt() < 1) {
                    binding.edPlt.edItem.setText("")
                    binding.tvMsg.text = in_loc_cd + " 이미 사용중인 랙입니다."
                    EditFocus(AppInfo.scan_field_loc_cd)
                    return
                }
            }
            loc_cd = sfData!!.getFieldString(DataInfo.loc_cd)
            MoveReg()
        }
    }
    fun notifyMsg_emptyloc(msg :String){
        val jList = SFDataList( SFData.GRID_1100, msg, "table")
        if ( jList.getSize() == 0){
            binding.tvMsg.text = "비어 있는 랙이 없습니다."
            SoundPlay(context, R.raw.err)
            EditFocus(AppInfo.scan_field_loc_cd)
            return;
        }
        SoundPlay(context, R.raw.info)

        (getActivity() as MainActivity).commMsg = msg
        (getActivity() as MainActivity).dlgTitle = "위치 선택"
        (getActivity() as MainActivity).dlgGridColSel =SFData.GRID_EMPTY_LOC

        val dialog = SelectDialog()
        dialog.setButtonClickListener(object: SelectDialog.OnButtonClickListener{
            override fun onButton1Clicked(param: SFData) {
                var locData = param;
                binding.run{
                    var scanData = locData.getFieldString(DataInfo.loc_cd);
                    edLocCd.edItem.setText(scanData)
                    bScanFlag = true
                    scanData(AppInfo.scan_field_loc_cd, scanData)
                }
            }
            override fun onButton2Clicked() {
                EditFocus(AppInfo.scan_field_loc_cd)
            }
            override fun onButton3Clicked() {
                EditFocus(AppInfo.scan_field_loc_cd)
            }
        })
        dialog.show((getActivity() as MainActivity).supportFragmentManager, "CustomDialog")
        EditFocus(AppInfo.scan_field_loc_cd)
    }

    fun notifyMsg_MoveReg(msg:ResultMsg2){
        if ( msg.result_cd == AppInfo.RESULT_ERR){
            SoundPlay(context, R.raw.err)
            var messg = msg.result_msg
            binding.tvMsg.text = messg
            SoundPlay(context, R.raw.err)
            EditFocus(AppInfo.scan_field_loc_cd)
        }
        else{
            var messg = String.format( plt_bar + " 파렛 " +  loc_cd + "랙으로 이동");
            binding.tvMsg.text =messg
            plt_bar = ""
            item_class_cd = ""
            loc_cd = ""
            SoundPlay(context, R.raw.info)
            textFieldInit()
            EditFocus(AppInfo.scan_field_plt_bar)
        }
    }
}