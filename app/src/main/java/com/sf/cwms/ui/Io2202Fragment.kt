package com.sf.cwms.ui

import android.graphics.Color
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sf.cwms.CWmsApp
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.data.DataInfo
import com.sf.cwms.data.OubViewModel
import com.sf.cwms.databinding.FragmentIo2202Binding
import com.sf.cwms.util.*
import retrofit2.Call
import java.util.*
import kotlin.collections.ArrayList
/*
   2200->2201->2202
   출고등록
   거래처선택이후 출고 품목 리스트조회
   스캔시 바코드 정보 수신
   품목리스트의 내용과 매칭
   수량 표시
   등록 버튼 클릭
 */
class Io2202Fragment  : BaseFragment<FragmentIo2202Binding>(R.layout.fragment_io2202){
    lateinit var adapterList: SFRvJSONAdapter
    var arrayList: ArrayList<SFData> = ArrayList<SFData>()
    var step = ""
    var stepn = ""
    var car_no = ""
    var loc_cd =  ""
    var loc_nm =  ""
    var plan_gno =  ""
    var oub_no = ""
    var plan_dno =  ""
    var cust_cd = ""
    var cust_nm =  ""
    var item_cd = ""
    var item_nm =  ""
    var cust_qty =  ""
    var cust_c_qty =  ""
    var item_qty =  ""
    var item_c_qty =  ""
    var plt_bar =  ""
    var plt_qty = ""
    var ori_qty = ""
    var plt_c_qty = ""
    var findRow = -1
    var selectRow = -1
    var mode = "PLT"
    var exp_type = "N"
    var exp_date = "0"
    var exp_text1 = ""
    var exp_text2 = ""
    var hold = ""

    var selSfData:SFData = SFData(SFData.GRID_2202)
    var cnt = 0

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false
        mode = AppInfo.mode_plt
        binding.run {
            txTitle.text = "출고"
            btReg.text = "등록"
            edBar.edItem.setText("")
            edBar.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            edBar.txCap.background = ContextCompat.getDrawable(binding.root.context, R.drawable.caption_edit)

            vwItemNm.txCap.text = "품목"
            vwMfgDat.txCap.text = "생산일"
            vwQty.txCap.text = "출고수량"

            if ( step.equals( "40" )  ||step.equals( "50" )  ) {
                vwQty.edItem.isEnabled = false
                var sNonColor = "#93a3b4"
                btConfirm.setTextColor(Color.parseColor(sNonColor))
            }
            vwItemNm.tvValue.text = ""
            vwMfgDat.tvValue.text = ""
            vwQty.edItem.setText("")
            vwQty.tvValue.text = ""
            vwQty.edItem.inputType = InputType.TYPE_CLASS_NUMBER

            edBar.txCap.text = "바코드"

            gridTitle.lin.setBackgroundColor( ContextCompat.getColor(binding.root.context, R.color.caption_stoke))
            gridTitle.tv1.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv2.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv3.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv4.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))

        }
        textFieldInit();
    }

    override fun initListener() {
        super.initListener()
        binding.run{
            edBar.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //if (bScanFlag == false) {
                        hideKeyboard()
                        val scanData = binding.edBar.edItem.text.toString()
                        val chk = ScanDataCheck( scanData )
                        if (chk == true) {
                            bScanFlag = true
                            scanData(AppInfo.Companion.scan_field_plt_bar, scanData)
                        }else{
                            ShowMessageBox(scanMfgMsg, AppInfo.action_oub_plt_no)
                        }

                    //}
                }
                false
            })
            vwQty.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //if (bScanFlag == false) {
                    hideKeyboard()
                    saveData()

                    //}
                }
                false
            })

        }

        val obj1 = object: OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                userLog("rv click=>" + position)
                selectRow = position
                selSfData = sfData
            }
        }

        val obj2 = object: OnLongClickInterface {
            override fun onLongClick(sfData: SFData, position: Int) {
                userLog("rv Long click=>" + position)
               // getOubPltList(sfData)
            }
        }

        binding.rv.run{
            adapterList = SFRvJSONAdapter(obj1, obj2)
            layoutManager = LinearLayoutManager( this.context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterList
            val customDecoration = CustomDecoration(2f, 1f,   ContextCompat.getColor(context, R.color.grid_line))
            addItemDecoration(customDecoration)
        }

        binding.apply{
            rv.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if (hasFocus) {
                        EditFocus()
                    }
                }
            })

        }
        var currentMenu = (getActivity() as MainActivity).currentMenu
        binding.run {
            btPrev.setOnClickListener {
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2201, "출고")
            }
            btReg.setOnClickListener {
                btReg.isEnabled = false
                saveData()
            }
            btConfirm.setOnClickListener {
                if ( step.equals( "40" )  ||step.equals( "50" )  ){
                    ShowMessageBox("확정된 정보입니다.", AppInfo.action_plt_bar)
                }
                else {
                   // btConfirm.isEnabled = false

                    confirmData()
                }
            }
            btView.setOnClickListener {
                getResultOubPltList()
            }
        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        userLog("io1100 AfterOnCreate " + CWmsApp.appStart)
        init()
        if ( CWmsApp.appStart){
            //binding.spRackNo.edItem.setSelection(RapidApp.loc_pos)
        }
    }

    fun textFieldInit()
    {
        binding.run {
            edBar.edItem.setText("")
            vwItemNm.tvValue.text = ""
            vwMfgDat.tvValue.text = ""
            vwQty.tvValue.text = ""
            vwQty.edItem.setText("")
            txInfo.text = ""
            hold = ""
        }
    }

    fun init()
    {
        (getActivity() as MainActivity).run {
            step = selData2.getFieldString(DataInfo.step)
            stepn =selData2.getFieldString(DataInfo.stepn)
            plan_gno = selData2.getFieldString(DataInfo.plan_gno)
            oub_no = selData2.getFieldString(DataInfo.oub_no)
            car_no = selData2.getFieldString(DataInfo.car_no)
            cust_cd = selData2.getFieldString(DataInfo.cust_cd)
            cust_nm = selData2.getFieldString(DataInfo.cust_nm)
            cust_qty =selData2.getFieldString(DataInfo.qty)
            cust_c_qty =selData2.getFieldString(DataInfo.c_qty)

            exp_type  = selData2.getFieldString(DataInfo.exp_type)
            exp_date  = selData2.getFieldString(DataInfo.exp_date)
            exp_text1 = selData2.getFieldString(DataInfo.exp_text1)
            exp_text2 = selData2.getFieldString(DataInfo.exp_text2)
        }
        // 생산일자 체크
        binding.run{

            hdr.tv1.text = stepn
            hdr.tv2.text = cust_nm
            hdr.tv3.text = cust_qty
            hdr.tv4.text = cust_c_qty

            gridTitle.tv1.text= "품목"
            gridTitle.tv2.text= "출고장"
            gridTitle.tv3.text= "예정"
            gridTitle.tv4.text= "출고"

        }
        getlist()
    }

    fun EditFocus()
    {
        binding.edBar.edItem.requestFocus();
        binding.edBar.edItem.selectAll();
    }

    fun confirmData()
    {

        var qty = adapterList.Sum(DataInfo.qty)
        var c_qty = adapterList.Sum(DataInfo.c_qty)
        /*
        var h_qty = 0

        // 출고 금지된 품목이 있는지 확인합니다.
        for (i in arrayList.indices) {
            val jdata: SFData = (arrayList.get(i) as SFData)
            var hold = jdata.getFieldString("hold")
            if (hold.equals("Y")){
                h_qty += jdata.getFieldInteger(DataInfo.qty)
            }
        }

        userLog("qty, hold" + qty.toString() + "," + h_qty.toString())

        // 원래 지시수량에서 출고금지된 품목의 지시수량을 확인합니다.
        qty = qty - h_qty

        userLog("qty, c_qty" + qty.toString() + "," + c_qty.toString())

         */

        if ( qty > c_qty){
            binding.btConfirm.isEnabled = true
            ShowMessageBox("출고 등록되지 않은 품목이 존재합니다.", AppInfo.action_plt_bar)
            return
        }


        var msg = "출고 확정하시겠습니까?"
        Confirm(msg,"출고확정", AppInfo.io2202)
    }

    fun update()
    {
        userLog("출고 확정 수행 >>>>>>>>>>>>" )
        // 확정처리
        var sndData : OubViewModel = OubViewModel()
        val action = AppInfo.action_confirm
        sndData.cust_cd = cust_cd
        sndData.plan_gno = plan_gno
        sndData.step = "30"
        sndData.next_step = "40"
        sndData.oub_no = oub_no
        sndData.default_set(action)
        val gson = Gson()
        val json = gson.toJson(sndData)
        userLog("update Event =" +json);

        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? = apiService.OubConfirm(getHeader(), sndData);
        callApi(call, AppInfo.io2202, AppInfo.api_OubConfirm, action);

    }

    fun cancel()
    {
       // 취소 처리
        EditFocus()
    }

    // 출고 대상 리스트 조회
    fun getlist()
    {
        val action = AppInfo.action_item
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.OubUserList(getHeader(), action, CWmsApp.cent_cd, "", plan_gno, "", "","20|30", CWmsApp.view_flag, CWmsApp.user_cd);
        callApi(call, AppInfo.io2202, AppInfo.api_OubList, action);
    }
    // 스캔한 파렛트 정보 가져오기
    fun getPltlist()
    {
        val action = AppInfo.action_plt_bar
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.OubUserList(getHeader(), action, CWmsApp.cent_cd, loc_cd, plan_gno, "", plt_bar,"20|30", CWmsApp.view_flag, CWmsApp.user_cd);
        callApi(call, AppInfo.io2202, AppInfo.api_OubList, action);
    }

    // 바코드 스캔 데이타 처리
    fun scanData(scan_field :String, scan_data:String){

        val bar = scan_data.split("-") // P-생산일자-주간야간-순번  순으로 분리
        findRow = -1
        if (bar.size < 4){
            EditFocus()
            return
        }
        if (!bar.get(0).equals("P")){
            // 바코드 첫자가 'P' 가 아닌경우 파렛 바코드가 아님
            EditFocus()
            return
        }
        if ( !exp_type.equals("N")) {
            // 출고 가능 생산일자 체크
            var v_dat = bar.get(2).toInt()
            var v_fr = exp_text1.toInt()
            var v_to = exp_text2.toInt()

            if ( v_dat < v_fr || v_dat > v_to){
                SoundPlay(context!!, R.raw.err)
                var msg = exp_text1 + "~" + exp_text2 + " 사이의 생산 파렛을 스캔하세요."
                ShowMessageBox(msg, AppInfo.action_plt_bar)
                //Confirm(msg,"알림", AppInfo.MSG_CONFIRM, AppInfo.action_plt_bar)
                return
            }
        }
        plt_bar = scan_data   // 전역 변수에 스캔한 데이타 저장
        getPltlist()
    }

    fun saveData()
    {
        if ( findRow == -1) {
            var msg = "출고 품목을 스캔하세요"
            binding.btReg.isEnabled = true
            ShowMessageBox(msg, AppInfo.action_plt_bar)
            return
        }
        var inQty = binding.vwQty.edItem.text.toString()
        if ( inQty.toInt() == 0 ) {
            var msg = "출고수량을 입력하세요"
            binding.btReg.isEnabled = true
            ShowMessageBox(msg, AppInfo.action_qty)

            return
        }
        userLog("TEST1>>>>" + mode)
        if ( mode.equals(AppInfo.mode_plt)){
            if (inQty.toInt() > plt_qty.toInt()){
                var msg = "출고박스가 파렛 박스 수량보다 크게 출고할수 없습니다."
                binding.btReg.isEnabled = true
                ShowMessageBox(msg, AppInfo.action_qty)
                return
            }
        }
        var n1 = item_qty.toInt() - item_c_qty.toInt() // 잔여량
        if ( n1 < inQty.toInt()){
            var msg = "지시수량 초과입니다."
            ShowMessageBox(msg, AppInfo.action_qty)
            return
        }

        plt_c_qty = inQty
        var sfData = arrayList.get(findRow)

        var sndData : OubViewModel = OubViewModel()
        val action = AppInfo.action_reg
        sndData.item_cd = sfData.getFieldString(DataInfo.item_cd)
        sndData.cust_cd = cust_cd
        sndData.plt_bar = plt_bar
        sndData.loc_cd = loc_cd
        sndData.plan_gno = plan_gno
        sndData.plan_dno = sfData.getFieldString(DataInfo.plan_dno)
        sndData.trn_type = sfData.getFieldString(DataInfo.trn_type)
        sndData.trn_sub_type = sfData.getFieldString(DataInfo.trn_sub_type)
        sndData.oub_no =  sfData.getFieldString(DataInfo.oub_no)
        sndData.plt_bar = plt_bar
        sndData.qty = plt_c_qty
        sndData.default_set(action)
        val gson = Gson()
        val json = gson.toJson(sndData)
        if (mode.equals(AppInfo.mode_plt)) {
            userLog("OubViewModel_ PLT 전송=" +json);
            val apiService = ApiServiceGenerator.createService(ApiService::class.java)
            val call: Call<String?>? = apiService.OubReg(getHeader(), sndData);
            callApi(call, AppInfo.io2202, AppInfo.api_OubReg, action);
        }else{
            userLog("OubViewModel_ AUTO 전송=" +json);
            val apiService = ApiServiceGenerator.createService(ApiService::class.java)
            val call: Call<String?>? = apiService.OubAutoReg(getHeader(), sndData);
            callApi(call, AppInfo.io2202, AppInfo.api_OubReg, action);

        }
    }

    fun cancelData(sfData:SFData)
    {
        var sndData : OubViewModel = OubViewModel()
        val action = AppInfo.action_delete
        sndData.item_cd = sfData.getFieldString(DataInfo.item_cd)
        sndData.plt_bar =sfData.getFieldString(DataInfo.plt_bar)
        sndData.plan_gno = plan_gno
        sndData.plan_dno = sfData.getFieldString(DataInfo.plan_dno)
        sndData.trn_type = sfData.getFieldString(DataInfo.trn_type)
        sndData.trn_sub_type = sfData.getFieldString(DataInfo.trn_sub_type)
        sndData.oub_no =  sfData.getFieldString(DataInfo.oub_no)
        sndData.default_set(action)
        val gson = Gson()
        val json = gson.toJson(sndData)
        userLog("OubViewModel=" +json);

        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? = apiService.OubRegCan(getHeader(), sndData);
        callApi(call, AppInfo.io2202, AppInfo.api_OubCancel, action);
    }

    // 출고대상 파렛 조회
    fun getOubPltList(sfData:SFData){
        val plan_dno = sfData.getFieldString(DataInfo.plan_dno)
        val item_cd = sfData.getFieldString(DataInfo.item_cd)
        val loc_cd = sfData.getFieldString(DataInfo.loc_cd)
        val pk_no = sfData.getFieldString(DataInfo.pk_no)
        val action = AppInfo.action_plt_bar
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.OubPltList(getHeader(), action, CWmsApp.cent_cd, plan_dno, item_cd, loc_cd, pk_no);
        callApi(call, AppInfo.io2202, AppInfo.api_OubPltList, AppInfo.action_oub_plt_no);
    }

    fun getResultOubPltList(){
        if ( selectRow == -1){
            var msg  = "조회할 품목을 리스트에서 선택하세요"
            ShowMessageBox(msg, AppInfo.action_qty)
            return
        }
        val sfData = arrayList.get(selectRow)
        val plan_dno = sfData.getFieldString(DataInfo.plan_dno)
        val item_cd = sfData.getFieldString(DataInfo.item_cd)
        val loc_cd = sfData.getFieldString(DataInfo.loc_cd)
        val pk_no = sfData.getFieldString(DataInfo.pk_no)
        val action = AppInfo.action_result_plt_bar
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.OubPltList(getHeader(), action, CWmsApp.cent_cd, plan_dno, item_cd, loc_cd, pk_no);
        callApi(call, AppInfo.io2202, AppInfo.api_OubPltList, AppInfo.action_result_plt_bar);
    }

    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
        userLog("NotifyMsg => msgtype " + msgtype +"msg " +   msg)
        if ( msgtype.equals(AppInfo.MSG_BOX)) {
            if (msg.equals("exit")) {
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2201, "출고")
            }
            else if ( msg.equals(AppInfo.action_plt_bar)) {
                plt_bar = ""
                EditFocus()
            }
            else
            {
                if ( result == true) {
                    if (msg.equals(AppInfo.mode_auto)) {
                        mode = msg
                        userLog("mode_auto set")
                        binding.vwQty.edItem.requestFocus()
                        binding.vwQty.edItem.selectAll()
                    } else {
                        binding.vwQty.edItem.requestFocus()
                        binding.vwQty.edItem.selectAll()
                    }
                }
                else{
                    if (msg.equals(AppInfo.mode_auto) || msg.equals(AppInfo.mode_hold)){
                        textFieldInit()
                        init()
                    }
                    else EditFocus();
                }

            }
            return
        }
        if ( msgtype.equals(AppInfo.MSG_CONFIRM)) {
            userLog("NotifyMsg =>Confirm")
            if ( result == true){
                if (msg.equals("QTY")){
                    //  출고된 파렛일 경우 수량 입력하고
                    mode = "QTY"
                    EditFocus()
                }
                else if (msg.equals("scanData"))
                {
                    getPltlist()
                }
                else {
                    update()
                }
            }
            else{
                binding.btConfirm.isEnabled = true;
                if (msg.equals("scanData"))
                {
                    plt_bar = ""
                }
                EditFocus()
            }
            return
        }

        if ( msgtype.equals(AppInfo.MSG_COMM)) {
            val resultMsg: ResultMsg2? = getResultRData(msg!!)
            if (resultMsg == null) {
                if (CWmsApp.action.equals(AppInfo.action_confirm))
                    binding.btConfirm.isEnabled = true
                SoundPlay(context!!, R.raw.err)
                setCustomToast(context!!,"Error Json Format Type");
                return
            }
            if (resultMsg.result_cd.equals(AppInfo.O_ERR)) {
                if (CWmsApp.action.equals(AppInfo.action_plt_bar)){
                    SoundPlay(context, R.raw.err)
                    ShowMessageBox(resultMsg.result_msg, AppInfo.action_plt_bar)
                    return

                }
                if (CWmsApp.action.equals(AppInfo.action_confirm))
                    binding.btConfirm.isEnabled = true

                SoundPlay(context, R.raw.err)
                setCustomToast(context!!,resultMsg.result_msg);
                return
            }
            if (CWmsApp.action.equals(AppInfo.action_item)){
                notifyMsg_List(msg)
            }
            if (CWmsApp.action.equals(AppInfo.action_plt_bar)) {
                // plt_bar -> auto_plt_bar 로 처리 변경
                // 차후 사용하지 않음
                notifyMsg_PltList(msg)
            }
            if (CWmsApp.action.equals(AppInfo.action_auto_plt_bar)) {
                notifyMsg_PltList(msg)
            }
            if (CWmsApp.action.equals(AppInfo.action_oub_plt_no)){
                notifyMsg_OubPltList(msg)
            }
            if (CWmsApp.action.equals(AppInfo.action_result_plt_bar)){
                notifyMsg_ResultPltList(msg)  // 출고 파렛 조회
            }
            if (CWmsApp.action.equals(AppInfo.action_reg)) {
                notifyMsg_Reg(resultMsg)
            }
            if (CWmsApp.action.equals(AppInfo.action_delete)) {
                notifyMsg_Reg(resultMsg)
            }
            if (CWmsApp.action.equals(AppInfo.action_confirm)) {
                notifyMsg_Confirm(resultMsg)
            }
        }
    }
    // 출고 파렛 조회
    fun notifyMsg_ResultPltList(msg :String){
        val jList = SFDataList( SFData.GRID_PLTBAR, msg, "table")
        if (jList.getSize() == 0){
            SoundPlay(context!!, R.raw.err)
            ShowMessageBox("출고정보가 없습니다.", AppInfo.action_oub_plt_no)
            return
        }

        (getActivity() as MainActivity).step = step
        (getActivity() as MainActivity).commMsg = msg
        (getActivity() as MainActivity).dlgTitle = "출고 파렛트"
        (getActivity() as MainActivity).dlgGridColSel = SFData.GRID_PLTBAR

        val dialog = PltInfoDialog()
        dialog.setButtonClickListener(object: PltInfoDialog.OnButtonClickListener{
            override fun onButton1Clicked(param: SFData) {
                var sfData = param;
                binding.run{
                    plt_bar = sfData.getFieldString(DataInfo.plt_bar);
                    userLog("cancel plt_bar:" + plt_bar)
                    cancelData(sfData)
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

    // 출고대상 PLT 조회
    fun notifyMsg_OubPltList(msg :String){
        val jList = SFDataList( SFData.GRID_PLTBAR, msg, "table")
        if (jList.getSize() == 0){
            SoundPlay(context!!, R.raw.err)
            ShowMessageBox("출고대상이 존재하지 않습니다.  재고이동을 하여주세요.", AppInfo.action_oub_plt_no)
            return
        }
        (getActivity() as MainActivity).commMsg = msg
        (getActivity() as MainActivity).dlgTitle = "파렛트 선택"
        (getActivity() as MainActivity).dlgGridColSel =SFData.GRID_PLTBAR

        val dialog = SelectDialog()
        dialog.setButtonClickListener(object: SelectDialog.OnButtonClickListener{
            override fun onButton1Clicked(param: SFData) {
                var sfData = param;
                binding.run{
                    plt_bar = sfData.getFieldString(DataInfo.plt_bar);
                    userLog("plt_bar:" + plt_bar)
                    edBar.edItem.setText(plt_bar)
                    scanData("", plt_bar)
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

    // 출고 품목 리스트 수신시 리스트에 추가
    fun notifyMsg_List(msg:String){
        binding.btReg.isEnabled = true

        val jList = SFDataList( SFData.GRID_2102, msg, "table")
        arrayList.clear()
        bDataExist = true
        findRow = -1

        if (jList.getSize() == 0){
            if ( cnt == 0 ) {
                SoundPlay(context!!, R.raw.err)
                bDataExist = false
                val sfData = SFData(SFData.TEXT_MSG, "{}");
                sfData.setField("result_msg", "출고대상이 존재하지 않습니다.")
                arrayList.add(sfData!!)
                adapterList.updateList(arrayList)
            }
            else{
                SoundPlay(context!!, R.raw.info)
                bDataExist = false
                val sfData = SFData(SFData.TEXT_MSG, "{}");
                sfData.setField("result_msg", "출고작업이 완료되었습니다.")
                arrayList.add(sfData!!)
                adapterList.updateList(arrayList)
            }
            return;
        }
        findRow = -1
        selectRow = -1
        if (cnt == 0 )  cnt = 1
        var t_qty = 0
        var t_cqty = 0
        var exp_text = "0"
        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_2202, row)
            t_qty += sfData!!.getFieldInteger(DataInfo.qty)
            t_cqty += sfData!!.getFieldInteger(DataInfo.c_qty)
            arrayList.add(sfData!!)
            if ( row == 0){
                exp_type = sfData!!.getFieldString(DataInfo.exp_type)
                exp_text1 = sfData!!.getFieldString(DataInfo.exp_text1)
                exp_text2 = sfData!!.getFieldString(DataInfo.exp_text2)
            }
        }
        adapterList.updateList(arrayList)
        binding.run{
            hdr.tv3.text = t_qty.toString()
            hdr.tv4.text = t_cqty.toString()

            if ( !exp_type.equals("N")) {
                txInfo.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.darkprimary
                    )
                )

                txInfo.text = exp_text1 + "~" + exp_text2 + " 생산 파렛만 출고가능"

            }
            else {
                txInfo.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.colorGray26
                    )
                )
                txInfo.text = "출고 기준 선입선출"

            }
        }

        if ( step == "20" || step == "30") {
            var sfData =  arrayList.get(0)
            var tmp_step = sfData.getFieldString(DataInfo.step)
            if (tmp_step.equals("40")|| tmp_step.equals("50")){
                SoundPlay(context!!, R.raw.info)
                ShowMessageBox("출고확정되었습니다.", "exit")
                return
            }
        }

        binding.edBar.edItem.requestFocus()
    }

    // Plt 바코드의 유효성 체트
    fun notifyMsg_PltList(msg:String){
        val jList = SFDataList( SFData.GRID_2202, msg, "table")
        if (jList.getSize() == 0){
            var msg = "입고된 제품이 아닙니다. 입고대상제품인지 확인하세요"
            userLog(msg)
            SoundPlay(context, R.raw.err)
            ShowMessageBox(msg, AppInfo.action_plt_bar)
            return
        }

        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_2202, row)
            item_cd = sfData!!.getFieldString(DataInfo.item_cd)
            var mfg_dat = sfData!!.getFieldString(DataInfo.mfg_dat)
            plt_qty = sfData!!.getFieldString(DataInfo.qty)
            ori_qty = sfData!!.getFieldString(DataInfo.ori_qty)

            var plt_loc_cd = sfData!!.getFieldString(DataInfo.loc_cd)
            var loc_type = sfData!!.getFieldString(DataInfo.loc_type)

            findRow = adapterList.FindRow(DataInfo.item_cd, item_cd)
            if ( findRow == -1 ){
                var msg = item_nm + " 출고 품목이 아닙니다. "
                userLog(msg)

                SoundPlay(context, R.raw.err)
                ShowMessageBox(msg, AppInfo.action_plt_bar)
                return
            }
            binding.rv.scrollToPosition( findRow)
            item_nm = arrayList.get(findRow).getFieldString(DataInfo.item_nm)
            item_qty = arrayList.get(findRow).getFieldString(DataInfo.qty)
            item_c_qty = arrayList.get(findRow).getFieldString(DataInfo.c_qty)
            hold =  arrayList.get(findRow).getFieldString("hold")

  //          var auto_oub_cnt = arrayList.get(findRow).getFieldString("auto_oub_cnt")
  //          userLog("auto_cnt===>" + auto_oub_cnt)
  //          var dup_oub_cnt = arrayList.get(findRow).getFieldString("dup_cnt")
  //          userLog("dup_cnt==>" + dup_oub_cnt)


            var n1 = item_qty.toInt() - item_c_qty.toInt()  // 남은 잔여량
            if (n1 > ori_qty.toInt() ) n1 = ori_qty.toInt()

            binding.run{
                vwItemNm.tvValue.text = item_nm
                vwMfgDat.tvValue.text = mfg_dat
            }

            mode = AppInfo.mode_plt
/*
            if ( auto_oub_cnt.toInt() > 0){
                var msg =  "중복해서 스캔할수 없습니다 ( 출고된 파렛 )"
                userLog(msg)
                SoundPlay(context, R.raw.err)
                ShowMessageBox(msg, AppInfo.action_plt_bar)
                return
            }
            if (dup_oub_cnt.toInt() > 0){
                var msg =  "중복해서 스캔할수 없습니다 ( 중복 스캔 )"
                userLog(msg)
                SoundPlay(context, R.raw.err)
                ShowMessageBox(msg, AppInfo.action_plt_bar)
                return
            }
*/

            if ( plt_qty.toInt() == 0 ){
                var msg =  "이미 출고된 파렛 바코드입니다. 출고 진행하시겠습니까?"
                if (hold.equals("Y") ){
                    msg = "출고금지된 품목의 이미 출고된 파렛 바코드입니다. 출고 진행하시겠습니까?"
                }
                userLog(msg)
                SoundPlay(context, R.raw.err)
                mode = AppInfo.mode_auto
                binding.vwQty.tvValue.text =  " ( " + n1 + " ) "
                binding.vwQty.edItem.setText(n1.toString())
                Confirm(msg,"알림", AppInfo.MSG_BOX, AppInfo.mode_auto)
                return
            }


            var oub_loc_cd = arrayList.get(findRow).getFieldString(DataInfo.loc_cd)
            var oub_loc_nm= arrayList.get(findRow).getFieldString(DataInfo.loc_nm)
            /*
            if (!loc_type.equals("02")){
                var msg = "피킹된 파렛이 아닙니다. 피킹된 파렛을 스캔해주세요"
                userLog(msg)

                ShowMessageBox(msg, AppInfo.action_plt_bar)
                return

            }
             */
            SoundPlay(context, R.raw.info)

            var n2 = plt_qty.toInt()   // PLT 재고
            plt_c_qty = plt_qty   // 팔레트 출고 확정 수량 Set
            if ( n2 > n1 ){  // 팔레트 수량이 주문 잔량보다 높을 경우
                plt_c_qty = n1.toString()
            }
            binding.run{
                vwItemNm.tvValue.text = item_nm
                vwMfgDat.tvValue.text = mfg_dat
                vwQty.tvValue.text =  " ( " + plt_qty + " ) PLT"
                vwQty.edItem.setText(plt_c_qty)
            }
            if ( hold.equals("Y")){
                var msg =  "출고 금지된 품목입니다.  출고진행하시겠습니까?"
                userLog(msg)
                SoundPlay(context, R.raw.err)
                mode = AppInfo.mode_hold
                Confirm(msg,"알림", AppInfo.MSG_BOX, AppInfo.mode_hold)
                return
            }
        }
        binding.vwQty.edItem.requestFocus()
        binding.vwQty.edItem.selectAll()
    }

    fun notifyMsg_Reg(msg: ResultMsg2){
        if ( msg.result_cd == AppInfo.RESULT_ERR){

            SoundPlay(context, R.raw.err)
            var messg = msg.result_msg

            binding.btReg.isEnabled = true

            EditFocus()
        }
        else{
            SoundPlay(context, R.raw.info)
            textFieldInit()
            init()
        }
    }
    fun notifyMsg_Confirm(msg: ResultMsg2){
        binding.btConfirm.isEnabled = true

        if ( msg.result_cd == AppInfo.RESULT_ERR){
            SoundPlay(context, R.raw.err)
            var messg = msg.result_msg
            EditFocus()
        }
        else{
            SoundPlay(context, R.raw.info)
            (getActivity() as MainActivity).SelectMenu(AppInfo.io2201, "출고")
        }
    }
}