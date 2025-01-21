package com.sf.cwms.ui

import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sf.cwms.CWmsApp
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.data.DataInfo
import com.sf.cwms.databinding.FragmentIo3200Binding
import com.sf.cwms.util.*
import retrofit2.Call

class Io3200Fragment : BaseFragment<FragmentIo3200Binding>(R.layout.fragment_io3200){
    lateinit var adapterList: SFRvJSONAdapter
    var arrayList: ArrayList<SFData> = ArrayList<SFData>()
    var currentAction:String = ""
    var currentSfData:SFData = SFData(SFData.GRID_3200)
    var selectPos = -1

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        binding.run {
            edBar.txCap.text = "바코드"
            vwBar.txCap.text = "ScanInfo"
            vwItemNm.txCap.text = "품목"
            gridTitle.tv1.text = "위치"
            gridTitle.tv2.text = "생산일"
            gridTitle.tv3.text = "순번"
            gridTitle.tv4.text = "수량"
            edBar.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.darkprimary))

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
                    bScanFlag = true
                    scanData(AppInfo.Companion.scan_field_plt_bar, scanData)
                    //}
                }
                false
            })

        }
        val obj1 = object: OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                userLog("rv click=>" + position)
                selectPos = position
                (getActivity() as MainActivity).selData1 = sfData
            }
        }

        val obj2 = object: OnLongClickInterface {
            override fun onLongClick(sfData: SFData, position: Int) {
                userLog("rv Long click=>" + position)
                selectPos = position
                (getActivity() as MainActivity).selData1 = sfData
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
                        //EditFocus()
                        edBar.edItem.requestFocus()
                        edBar.edItem.selectAll()
                    }
                }
            })
        }
        var currentMenu = (getActivity() as MainActivity).currentMenu
        binding.run {
            btPrev.setOnClickListener {
                (getActivity() as MainActivity).ReturnMenu()
            }
            btReg.setOnClickListener {
                userLog("재고이동...")
                if ( selectPos == -1) {
                    SoundPlay(context!!, R.raw.err)
                    ShowMessageBox("재고이동 대상을 선택해주세요", "plt_bar")
                }
                else {
                    (getActivity() as MainActivity).preMenu = AppInfo.io3200
                    (getActivity() as MainActivity).SelectMenu(AppInfo.io3100, "이동")
                }
            }
            /*
            btUpd.setOnClickListener {
                (getActivity() as MainActivity).selData1 = currentSfData
                (getActivity() as MainActivity).preMenu = AppInfo.io3200
                (getActivity() as MainActivity).SelectMenu(AppInfo.io3300, "재고조정")
            }*/
        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        userLog("io1100 AfterOnCreate " + CWmsApp.appStart)
        if ( CWmsApp.appStart){
            //binding.spRackNo.edItem.setSelection(RapidApp.loc_pos)
        }
        binding.edBar.edItem.requestFocus()
    }

    fun textFieldInit()
    {
        binding.run {
            edBar.txCap.text = "바코드"
            vwBar.txCap.text = "ScanInfo"
            vwItemNm.txCap.text = "품목"
            edBar.edItem.setText("")
            vwBar.tvValue.text = ""
            vwItemNm.tvValue.text = ""

        }
    }

    fun getList(action:String, plt_bar:String, loc_cd:String, item_cd:String)
    {
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.StockList(getHeader(), action, CWmsApp.cent_cd, plt_bar, loc_cd, item_cd);
        callApi(call, AppInfo.io3200, AppInfo.api_StockList, action);
    }

    fun update( )
    {

    }

    fun cancel()
    {
    }

    fun scanData(scan_field :String, scan_data:String){
        if ( scan_data.length == 0) {
            SoundPlay(context, R.raw.err)
            setCustomToast(context!!,"바코드 스캔해주세요");

            return
        }

        selectPos = -1
        val bar = scan_data.split("-")
        if (bar.get(0).equals("P")){
            getList(AppInfo.action_plt_bar, scan_data, "", "")
        }
        else{
         //   binding.btReg.isEnabled = false
            getList(AppInfo.action_loc_cd, "", scan_data, "")
        }
    }

    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
        if (msgtype.equals(AppInfo.MSG_BOX)){
            binding.edBar.edItem.requestFocus()
            binding.edBar.edItem.selectAll()
            return
        }
        if ( msgtype.equals(AppInfo.MSG_COMM)) {
            val resultMsg: ResultMsg2? = getResultRData(msg!!)
            if (resultMsg == null) {
                SoundPlay(context!!, R.raw.err)
                setCustomToast(context!!,"Error Json Format Type");
                return
            }
            if (resultMsg.result_cd.equals(AppInfo.O_ERR)) {
                SoundPlay(context, R.raw.err)
                setCustomToast(context!!,resultMsg.result_msg);
                return
            }
            if (CWmsApp.action.equals(AppInfo.action_item)){
                notifyMsg_List(msg)
            }
            if (CWmsApp.action.equals(AppInfo.action_plt_bar)) {
                notifyMsg_PltList(msg)
            }
            if (CWmsApp.action.equals(AppInfo.action_loc_cd)) {
                notifyMsg_LocList(msg)
            }

        }
    }

    fun notifyMsg_LocList(msg:String){
        selectPos = -1
        binding.run{
            vwBar.txCap.text = "위치"
            vwItemNm.txCap.text= "Total"
            gridTitle.tv1.text = "품목"
            gridTitle.tv2.text = "생산일"
            gridTitle.tv3.text = "순번"
            gridTitle.tv4.text = "수량"
        }
        val jList = SFDataList( SFData.GRID_3200, msg, "table")
        arrayList.clear()
        adapterList.updateList(arrayList);
        if (jList.getSize() == 0){
            SoundPlay(context!!, R.raw.err)
            ShowMessageBox("해당 위치에 재고가 존재하지 않습니다.", "loc_cd" )
            return;
        }
        SoundPlay(context!!, R.raw.info)
        var tot = 0
        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_3200, row)
            var loc_type = sfData!!.getFieldString("loc_type")
            if (loc_type.equals("02"))
                binding.vwBar.tvValue.text = sfData!!.getFieldString(DataInfo.loc_nm)
            else
                binding.vwBar.tvValue.text = sfData!!.getFieldString(DataInfo.loc_cd)
            var qty =  sfData!!.getFieldString(DataInfo.qty)
            arrayList.add(sfData!!);
            tot += qty.toInt()
        }
        if ( jList.getSize() > 0 ) {
            selectPos = 0
            var sfData =  arrayList.get(selectPos)
            (getActivity() as MainActivity).selData1 =  sfData
        }

        binding.vwItemNm.tvValue.text = String.format("%d", tot)
        adapterList.updateList(arrayList)
        binding.edBar.edItem.setText("")
        binding.edBar.edItem.requestFocus()
    }


    fun notifyMsg_PltList(msg:String){
        selectPos = -1
        val jList = SFDataList( SFData.GRID_3200, msg, "table")
        arrayList.clear()
        adapterList.updateList(arrayList);
        if (jList.getSize() == 0){
            SoundPlay(context!!, R.raw.err)
            ShowMessageBox("등록된 정보가 아닙니다..", "plt_bar" )
            return;
        }

        SoundPlay(context!!, R.raw.info)

        var item_cd = ""
        var plt_bar = ""
        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_3200, row)
            currentSfData = sfData!!
            sfData.setField("pk", "1")
            arrayList.add(sfData);

            item_cd = sfData!!.getFieldString(DataInfo.item_cd)
            plt_bar = sfData!!.getFieldString(DataInfo.plt_bar)
            var inb_yn = sfData!!.getFieldString("inb_yn")
            var oub_yn = sfData!!.getFieldString("oub_yn")
            if (inb_yn.equals("N")) {
                SoundPlay(context, R.raw.err)
                ShowMessageBox("미입고 제품입니다.", "plt_bar" )
                return;
                return
            }
            if (oub_yn.equals("Y")) {
                SoundPlay(context, R.raw.err)
                ShowMessageBox("출고된 제품입니다.", "plt_bar" )
                return;
            }
            binding.run{
                vwItemNm.tvValue.text = sfData!!.getFieldString(DataInfo.item_nm)
                vwBar.txCap.text = "PLT 바코드"
                vwBar.tvValue.text = sfData!!.getFieldString(DataInfo.plt_bar)

                var loc_type = sfData.getFieldString("loc_type")
                if (loc_type.equals("02")) {
                    // binding.btReg.isEnabled = false
                }
                else {
                    currentAction = AppInfo.action_plt_bar
                    currentSfData = sfData!!
                    //   binding.btReg.isEnabled = true
                }
            }
        }
        if ( jList.getSize() > 0 ) {
            selectPos = 0
            var sfData =  arrayList.get(selectPos)
            (getActivity() as MainActivity).selData1 =  sfData
        }

        adapterList.updateList(arrayList)
        getList(AppInfo.action_item, plt_bar, "", item_cd)
    }

    fun notifyMsg_List(msg:String){
        val jList = SFDataList( SFData.GRID_3200, msg, "table")
        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(SFData.GRID_3200, row)
            arrayList.add(sfData!!);
        }

        adapterList.updateList(arrayList)
        binding.edBar.edItem.setText("")
        binding.edBar.edItem.requestFocus()
    }
}