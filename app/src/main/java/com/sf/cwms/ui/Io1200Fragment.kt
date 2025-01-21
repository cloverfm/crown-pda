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
import com.sf.cwms.data.DataInfo
import com.sf.cwms.data.InbViewModel
import com.sf.cwms.databinding.FragmentIo1200Binding
import com.sf.cwms.util.AppInfo
import com.sf.cwms.util.SFData
import com.sf.cwms.util.SFDataList
import com.sf.cwms.util.SelectDialog
import retrofit2.Call
import retrofit2.http.HeaderMap
import retrofit2.http.Query
/**
 * A simple [Fragment] subclass.
 * Use the [Io1200Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
// 적치 작업
class Io1200Fragment : BaseFragment<FragmentIo1200Binding>(R.layout.fragment_io1200) {
    var inputStep:Int = 0    // 0 일때 로케이션 스캔  또는 Loc 선택시 -   2  PLT 바코드만 계속 스캔 ,   3  PLT 바코드 스캔 추천 Rack
    var inb_r:InbViewModel = InbViewModel()
    var loc_cd: String = ""
    var plt_bar: String = ""
    var item_class_cd: String = ""
    var item_cd: String = ""
    var inb_yn : String = ""
    var oub_yn : String = ""

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
            vwCLocCd.txCap.text = "현재위치"
            vwLocCd.txCap.text = "추천위치"
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
            btReg.setOnClickListener {
                MoveReg()
            }
            btClear.setOnClickListener {
                init()
            }
            btLoc.setOnClickListener {
                // 위치 선택
                getLocList()
            }
            btLocFind.setOnClickListener {
                // 위치 선택
                getEmptyLocList()
            }
            btPrev.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    EditFocus()
                }
            })
            btClear.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    EditFocus()
                }
            })
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
        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        userLog("io1200 AfterOnCreate " + CWmsApp.appStart)
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
            vwCLocCd.tvValue.text = ""
            vwLocCd.tvValue.text = ""
            tvMsg.text = "파렛 ID 를 스캔하세요."
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

    fun getLocList(){
        val action = "loc_cd"
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.GetLocation(getHeader(), action, CWmsApp.cent_cd, "01|02|04", "", "");
        callApi(call, AppInfo.io1200, AppInfo.api_GetLocation, action);
    }

    fun getEmptyLocList(){
        if ( item_class_cd.length == 0 ) {
            binding.tvMsg.text = "팔레트 바코드를 스캔해 주세요."
            SoundPlay(context, R.raw.err)
            EditFocus();
            return
        }

        val action = "list"
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.InbLocList(getHeader(), action, CWmsApp.cent_cd, item_cd, item_class_cd);
        callApi(call, AppInfo.io1200, AppInfo.api_InbLocList, "emptylist");
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

        var bar_type = scan_data.substring(0,2);
        userLog("bar_type=>" + bar_type)
        if (inputStep == 1){
            if (bar_type.equals("P-")) {
                userLog("센타코드=>" + CWmsApp.cent_cd);
                val apiService = ApiServiceGenerator.createService(ApiService::class.java)
                var action = AppInfo.action_plt_bar
                val call: Call<String?>? =
                    apiService.InbPltInfo(getHeader(), action, CWmsApp.cent_cd, scan_data, "", "")
                callApi(call, AppInfo.io1200, AppInfo.api_InbPltInfo, action);
            }
            else
            {
                binding.tvMsg.text =  "팔레트 바코드를 스캔해 주세요."
                SoundPlay(context, R.raw.err)
                EditFocus();
                return;
            }
        }
        else{
            // InputStep == 0
            if ( bar_type.equals("P-")) {
                val apiService = ApiServiceGenerator.createService(ApiService::class.java)
                var action = AppInfo.action_plt_bar
                val call: Call<String?>? =
                    apiService.InbPltInfo(getHeader(), action, CWmsApp.cent_cd, scan_data, "", "")
                callApi(call, AppInfo.io1200, AppInfo.api_InbPltInfo, action);
            }
            else{
                // 로케이션 바코드 일 경우
                if ( plt_bar.length == 0){
                    // 팔레트 바코드 보다 로케이션 바코드가 먼저 읽힌 경우
                    var action = AppInfo.action_loc_info
                    val apiService = ApiServiceGenerator.createService(ApiService::class.java)
                    val call: Call<String?>? =
                        apiService.GetLocation(getHeader(), action, CWmsApp.cent_cd, "", scan_data, "")
                    callApi(call, AppInfo.io1200, AppInfo.api_GetLocation, action);
                }
                else{
                    // 로케이션 바코드 스캔
                    var action = AppInfo.action_plt_loc
                    val apiService = ApiServiceGenerator.createService(ApiService::class.java)
                    val call: Call<String?>? =
                        apiService.InbPltInfo(getHeader(), action, CWmsApp.cent_cd, inb_r.plt_bar, scan_data, inb_r.io_no)
                    callApi(call, AppInfo.io1200, AppInfo.api_InbPltInfo, action);
                }
            }
        }
    }

    fun MoveReg()
    {
        if ( oub_yn.equals("Y")){
            binding.tvMsg.text = "출고된 파렛입니다.."
            SoundPlay(context, R.raw.err)
            EditFocus()
            return
        }
        if (plt_bar == null || plt_bar.length == 0){
            binding.tvMsg.text = "팔레트 바코드를 스캔해 주세요."
            SoundPlay(context, R.raw.err)
            EditFocus()
            return
        }
        if (loc_cd == null || loc_cd.length == 0){
            binding.tvMsg.text = "위치를 스캔해 주세요."
            SoundPlay(context, R.raw.err)
            EditFocus()
            return
        }
        inb_r.action = AppInfo.action_save
        inb_r.loc_cd = loc_cd
        inb_r.plt_bar = plt_bar

        val gson = Gson()
        val json = gson.toJson(inb_r)

        if (inb_yn.equals("Y")){
            userLog("MoveReg=" +json);
            val apiService = ApiServiceGenerator.createService(ApiService::class.java)
            val call: Call<String?>? = apiService.MoveReg(getHeader(), inb_r)
            callApi(call, AppInfo.io1200, AppInfo.api_MoveReg, inb_r.action);

        }else{
            userLog("InbReg=" +json);
            val apiService = ApiServiceGenerator.createService(ApiService::class.java)
            val call: Call<String?>? = apiService.InbReg(getHeader(), inb_r)
            callApi(call, AppInfo.io1200, AppInfo.api_InbReg, inb_r.action);
        }
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
                AppInfo.action_plt_bar -> notifyMsg_pltBarInfo(msg);
                AppInfo.action_plt_loc -> notifyMsg_pltBarLocInfo(msg);
                AppInfo.action_loc_cd -> notifyMsg_loc(msg)
                AppInfo.action_loc_info -> notifyMsg_locinfo(msg)
                "emptylist" -> notifyMsg_emptyloc(msg)
                else -> notifyMsg_moveReg(resultMsg);
            }
        }
    }

    fun notifyMsg_loc(msg :String){
        (getActivity() as MainActivity).commMsg = msg
        (getActivity() as MainActivity).dlgTitle = "위치 선택"
        (getActivity() as MainActivity).dlgGridColSel =SFData.GRID_LOCSEL

        val dialog = SelectDialog()
        dialog.setButtonClickListener(object: SelectDialog.OnButtonClickListener{
            override fun onButton1Clicked(param: SFData) {
                var locData = param;
                binding.run{
                    loc_cd = locData.getFieldString(DataInfo.loc_cd);
                    vwLocCd.tvValue.text = locData.getFieldString(DataInfo.loc_cd) + " " + locData.getFieldString(DataInfo.loc_nm)
                    vwLocCd.txCap.text = "입고위치"
                    tvMsg.text = "위치가 고정되었습니다. 팔레트바코드를 스캔하여 등록하세요."
                    EditFocus()
                    SoundPlay(context, R.raw.info)
                    inputStep = 1
                }
            }
            override fun onButton2Clicked() {
                EditFocus()
            }
            override fun onButton3Clicked() {
                EditFocus()
            }

        })
        dialog.show((getActivity() as MainActivity).supportFragmentManager, "CustomDialog")
        EditFocus()
    }

    fun notifyMsg_emptyloc(msg :String){
        val jList = SFDataList( SFData.GRID_1100, msg, "table")
        if ( jList.getSize() == 0 ){
            binding.tvMsg.text = "비어 있는 랙이 없습니다."
            SoundPlay(context, R.raw.err)
            EditFocus()
            return;
        }

        (getActivity() as MainActivity).commMsg = msg
        (getActivity() as MainActivity).dlgTitle = "위치 선택"
        (getActivity() as MainActivity).dlgGridColSel =SFData.GRID_EMPTY_LOC

        val dialog = SelectDialog()
        dialog.setButtonClickListener(object: SelectDialog.OnButtonClickListener{
            override fun onButton1Clicked(param: SFData) {
                var locData = param;
                binding.run{
                    var scanData = locData.getFieldString(DataInfo.loc_cd);
                    edPlt.edItem.setText(scanData)
                    bScanFlag = true
                    scanData(scanData)
                }
            }
            override fun onButton2Clicked() {
                EditFocus()
            }
            override fun onButton3Clicked() {
                EditFocus()
            }
        })

        dialog.show((getActivity() as MainActivity).supportFragmentManager, "CustomDialog")
        EditFocus()

    }

    fun notifyMsg_locinfo(msg:String){
        val jList = SFDataList( SFData.GRID_1100, msg, "table")
        if ( jList.getSize() == 0){
            binding.edPlt.edItem.setText("")
            binding.tvMsg.text =  "등록된 위치 코드가 아닙니다."
            SoundPlay(context, R.raw.err)
            EditFocus()
            return;
        }

        for (row in 0 until jList.getSize()) {

            var sfData = jList.getRecord(SFData.GRID_1100, row)
            var loc_type =sfData!!.getFieldString("loc_type")
            var in_loc_cd = sfData!!.getFieldString("loc_cd")
            var loc_nm = sfData!!.getFieldString(DataInfo.loc_nm);
            var tmp1 =sfData!!.getFieldString("item_class")
            var tmp2 =sfData!!.getFieldString("v_plt")

            if ( loc_type.equals("05") ){
                binding.edPlt.edItem.setText("")
                SoundPlay(context, R.raw.err)
                binding.tvMsg.text = in_loc_cd + " 사용할수 없는 위치입니다."
                EditFocus()
                return
            }

            if ( loc_type.equals("03")){
                if (tmp2.toInt() < 1) {
                    binding.edPlt.edItem.setText("")
                    binding.tvMsg.text = in_loc_cd + "이미 사용중인 랙입니다."
                    SoundPlay(context, R.raw.err)
                    EditFocus()
                    return
                }
            }
            item_class_cd = tmp1
            loc_cd = sfData!!.getFieldString(DataInfo.loc_cd)

            binding.vwLocCd.txCap.text = "입고 위치"
            binding.vwLocCd.tvValue.text = loc_cd + " " + loc_nm

            inputStep = 1
            EditFocus()
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
                var item = String.format("%s %s",sfData!!.getFieldString(DataInfo.item_cd), sfData!!.getFieldString(DataInfo.item_nm))
                vwPltCd.tvValue.text = plt_bar
                vwItemNm.tvValue.text = item
                vwFactCd.tvValue.text = sfData!!.getFieldString(DataInfo.make_fact_nm);
                vwMfgDat.tvValue.text = sfData!!.getFieldString(DataInfo.mfg_dat) + " / " + sfData!!.getFieldString(DataInfo.dn_flg);
                vwQty.tvValue.text = sfData!!.getFieldString(DataInfo.qty);
                var s_loc_cd =  sfData!!.getFieldString("s_loc_cd") // 현재 위치
                var s_loc_nm =  sfData!!.getFieldString("s_loc_nm") // 현재 위치
                vwCLocCd.tvValue.text = s_loc_cd + " " + s_loc_nm
                inb_yn = sfData!!.getFieldString("inb_yn")
                oub_yn = sfData!!.getFieldString("oub_yn")
                if (oub_yn.equals("Y")) {
                    loc_cd = ""
                    binding.tvMsg.text =" 출고된 파렛입니다."
                    SoundPlay(context, R.raw.err)
                    EditFocus()
                    return
                }
                if ( inputStep == 0) {
                    item_class_cd = sfData!!.getFieldString(DataInfo.item_class_cd)
                    item_cd = sfData!!.getFieldString(DataInfo.item_cd)
                    loc_cd = sfData!!.getFieldString(DataInfo.loc_cd);
                    if (loc_cd.equals("NO BRAND")) {
                        loc_cd = ""
                        vwLocCd.tvValue.text = "브랜드 재고Zone 미 설정.";
                    } else {
                        vwLocCd.tvValue.text = loc_cd;
                    }
                    inb_r.set(AppInfo.action_save, sfData)
                    SoundPlay(context, R.raw.info)
                    if (inb_yn.equals("Y")){
                        binding.tvMsg.text ="적치 위치를 스캔하시거나 또는 등록을 눌러 추천위치로 적치하세요."
                        EditFocus()
                    } else {
                        binding.tvMsg.text = "미 입고 파렛입니다. 입고할 위치를 스캔하시거나 또는 등록을 눌러 추천위치로 입고하세요"
                        EditFocus()
                    }
                    return
                }
                else {
                    inb_r.set(AppInfo.action_save, sfData)
                    MoveReg()
                }
            }
        }
    }

    fun notifyMsg_pltBarLocInfo(msg:String){
        val jList = SFDataList( SFData.GRID_1100, msg, "table")
        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_1100, row)
            var loc_type =sfData!!.getFieldString("loc_type")
            var in_loc_cd = sfData!!.getFieldString("loc_cd")
            var tmp1 =sfData!!.getFieldString("item_class")
            var tmp2 =sfData!!.getFieldString("v_plt")
            if ( loc_type.equals("03")){
                if (tmp2.toInt() < 1) {
                    binding.edPlt.edItem.setText("")
                    SoundPlay(context, R.raw.err)
                    showMessage(in_loc_cd +"이미 사용중인 랙입니다.", AppInfo.scan_field_loc_cd)
                    return
                }
            }
            loc_cd = sfData!!.getFieldString(DataInfo.loc_cd)
            MoveReg()
        }
    }

    fun notifyMsg_moveReg(msg:ResultMsg2){
        if ( msg.result_cd == AppInfo.RESULT_ERR){
            SoundPlay(context, R.raw.err)
            var messg = msg.result_msg
            binding.tvMsg.text =messg
            EditFocus()
        }
        else{
            if ( inputStep == 0) {
                var messg = String.format( plt_bar + " 파렛이 " +  loc_cd + "랙으로 입고");
                binding.tvMsg.text = messg

                plt_bar = ""
                item_class_cd = ""
                loc_cd = ""
                inb_r.inb_no = msg.result_msg  // 부여받은 inb번호를
                SoundPlay(context, R.raw.info )
                textFieldInit()
                showMessage(messg, AppInfo.scan_field_plt_bar);
                EditFocus()
            }
            else{
                var messg = String.format( plt_bar + " 파렛이 입고 되었습니다.");
                binding.tvMsg.text =messg
                plt_bar = ""
                SoundPlay(context, R.raw.info)
                EditFocus()
            }
        }
    }
}

