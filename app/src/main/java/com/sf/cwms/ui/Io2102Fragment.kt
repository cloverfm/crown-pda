package com.sf.cwms.ui

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sf.cwms.CWmsApp
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.databinding.FragmentIo2102Binding
import com.sf.cwms.data.DataInfo
import com.sf.cwms.data.InbViewModel
import com.sf.cwms.data.PickViewModel
import com.sf.cwms.util.*
import com.sf.cwms.util.AppInfo
import com.sf.cwms.util.SFData
import com.sf.cwms.util.SFDataList
import retrofit2.Call


/**
 * A simple [Fragment] subclass.
 * Use the [Io2101Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Io2102Fragment  : BaseFragment<FragmentIo2102Binding>(R.layout.fragment_io2102){
    lateinit var adapterList: SFRvJSONAdapter
    var arrayList: ArrayList<SFData> = ArrayList<SFData>()
    var pk_dat =""
    var loc_cd =  ""
    var loc_nm =  ""
    var pk_no =  ""
    var item_cd = ""
    var item_nm =  ""
    var item_qty =  ""
    var item_plt =  ""
    var plt_bar =  ""
    var findRow = -1

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        binding.run {
            txTitle.text =(getActivity() as MainActivity).selData2.getFieldString(DataInfo.loc_nm) + " 피킹"
            btReg.text = "등록"
            edBar.edItem.setText("")
            edBar.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            edBar.txCap.background =
                ContextCompat.getDrawable(binding.root.context, R.drawable.caption_edit)

            edBar.txCap.text = "바코드"
            gridTitle.tv1.text = "위치"
            gridTitle.tv2.text = "생산일"
            gridTitle.tv3.text = "순번"
            gridTitle.tv4.text = "수량"

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
            bScanFlag = false
            edBar.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //if (bScanFlag == false) {
                        hideKeyboard()
                        val scanData = binding.edBar.edItem.text.toString()
                        bScanFlag = true
                        val chk = ScanDataCheck( scanData)
                        if (chk == true) {
                            bScanFlag = true
                            scanData(AppInfo.Companion.scan_field_plt_bar, scanData)
                        }else{
                            ShowMessageBox(scanMfgMsg, "edBar")
                        }

                    // }
                }
                false
            })

            /*
            edBar.edItem.setOnEditorActionListener { v, actionId, event ->
                var handled = false
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                        hideKeyboard()
                        val scanData = binding.edBar.edItem.text.toString()
                        bScanFlag = true
                        scanData(AppInfo.Companion.scan_field_plt_bar, scanData)

                    handled = true
                }
                handled
            }
            */

        }

        val obj1 = object: OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                userLog("rv click=>" + position)
                var plt_bar = sfData.getFieldString(DataInfo.plt_bar)
                binding.edBar.edItem.setText(plt_bar)
                bScanFlag = true
                findRow = position
                //saveData()
            }
        }

        val obj2 = object: OnLongClickInterface {
            override fun onLongClick(sfData: SFData, position: Int) {
                userLog("rv Long click=>" + position)
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
                        if ( bScanFlag == false)  binding.edBar.edItem.requestFocus()
                    }
                }
            })
            btPrev.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if (hasFocus) {
                        if ( bScanFlag == false) binding.edBar.edItem.requestFocus()
                    }
                }
            })
            btReg.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if (hasFocus) {
                        if ( bScanFlag == false) binding.edBar.edItem.requestFocus()
                    }
                }
            })
        }

        var currentMenu = (getActivity() as MainActivity).currentMenu
        binding.run {
            btPrev.setOnClickListener {
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2101, "피킹")
            }
            btReg.setOnClickListener {
                saveData()
            }
        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        userLog("io2102 AfterOnCreate " + CWmsApp.appStart)
        init()
        if ( CWmsApp.appStart){
            //binding.spRackNo.edItem.setSelection(RapidApp.loc_pos)
        }
    }

    fun textFieldInit()
    {
        binding.run {
        }
    }

    fun init()
    {
        if ((getActivity() as MainActivity).isMode.equals("1")) {
            pk_dat = (getActivity() as MainActivity).selData1.getFieldString(DataInfo.pk_dat)  //출고장
            loc_cd = (getActivity() as MainActivity).selData1.getFieldString(DataInfo.loc_cd)  //출고장
            loc_nm = (getActivity() as MainActivity).selData1.getFieldString(DataInfo.loc_nm)
            item_cd = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.item_cd)
            item_nm = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.item_nm)
            item_qty = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.qty)
            item_plt = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.plt)
        }else {
            loc_cd = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.loc_cd)  //출고장
            loc_nm = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.loc_nm)
            pk_no = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.pk_no)
            item_cd = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.item_cd)
            item_nm = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.item_nm)
            item_qty = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.qty)
            item_plt = (getActivity() as MainActivity).selData2.getFieldString(DataInfo.plt)
        }

        binding.run {
            vwInfo.tv1.text = item_nm
            vwInfo.tv2.text = item_qty
            vwInfo.tv3.text = item_plt
        }
        getlist()

    }

    fun update()
    {
    }
    fun cancel()
    {
    }

    fun getlist()
    {
        val action = AppInfo.action_list
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        if ((getActivity() as MainActivity).isMode.equals("1")) {

            val call: Call<String?>? =
                apiService.PKUserList(
                    getHeader(),
                    action,
                    CWmsApp.cent_cd,
                    pk_dat,
                    CWmsApp.pk_grp_no,
                    loc_cd,
                    item_cd,
                    "N",
                    CWmsApp.user_cd
                );
            callApi(call, AppInfo.io2102, AppInfo.api_PickList, action);
        }
        else{
            val call: Call<String?>? =
                apiService.PickList(
                    getHeader(),
                    action,
                    CWmsApp.cent_cd,
                    loc_cd,
                    pk_no,
                    CWmsApp.pk_grp_no,
                    item_cd,
                    "",
                    "N"
                );
            callApi(call, AppInfo.io2102, AppInfo.api_PickList, action);

        }

    }

    fun scanData(scan_field :String, scan_data:String){
        val bar = scan_data.split("-")
        var action = AppInfo.action_plt_bar
        userLog("scanData = " + scan_data)
        findRow = -1
        bScanFlag = false
        userLog("bar get = " + bar.get(0))
        if (bar.get(0) == "P"){
            findRow = adapterList.FindRow(DataInfo.plt_bar, scan_data)
        }
        else{
            findRow = adapterList.FindRow(DataInfo.loc_cd, scan_data)
        }
        userLog("findRow = " + findRow)
        if ( findRow == -1){
            ShowMessageBox("피킹대상 파렛트가 아닙니다.", "edBar")
            return
        }
        adapterList.selectedPosition = findRow
        binding.rv.scrollToPosition( findRow)
        bScanFlag = false
        binding.edBar.edItem.requestFocus()
        binding.edBar.edItem.selectAll()
    }

    fun saveData()
    {
        if ( findRow == -1) return
        var sfData = arrayList.get(findRow)

        var sndData : PickViewModel = PickViewModel()
        val action = AppInfo.action_plt_bar
        sndData.item_cd = item_cd
        sndData.plt_bar = plt_bar
        sndData.loc_cd = loc_cd
        sndData.pk_no = sfData.getFieldString(DataInfo.pk_no)
        sndData.plt_bar = sfData.getFieldString(DataInfo.plt_bar)
        sndData.default_set(action)

        val gson = Gson()
        val json = gson.toJson(sndData)
        userLog("PickViewModel=" +json);

        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.PickReg(getHeader(), sndData);
        callApi(call, AppInfo.io2102, AppInfo.api_PickReg, action);
    }

    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
        if ( msgtype.equals(AppInfo.MSG_BOX)){
            binding.edBar.edItem.setText("")
            binding.edBar.edItem.requestFocus()
            return
        }
        if ( msgtype.equals(AppInfo.MSG_COMM)) {
            bScanFlag = false
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
            if (CWmsApp.action.equals(AppInfo.action_list)){
                notifyMsg_List(msg)
            }
            if (CWmsApp.action.equals(AppInfo.action_plt_bar)) {
                getlist()
            }
        }
    }

    fun notifyMsg_List(msg:String){

        val jList = SFDataList( SFData.GRID_2102, msg, "table")

        arrayList.clear()
        bDataExist = true

        if (jList.getSize() == 0){
            bDataExist = false

            val sfData = SFData(SFData.TEXT_MSG, "{}");
            sfData.setField("result_msg", "피킹대상이 존재하지 않습니다.")
            arrayList.add(sfData!!)
            adapterList.updateList(arrayList)
            binding.edBar.edItem.requestFocus()
            binding.edBar.edItem.selectAll()
            return;
        }
        var totQty = 0
        var totCnt = 0
        for (row in 0 until jList.getSize()) {
            //var sfData = jList.getRecord(SFData.GRID_2102, row)
            var sfData = jList.getRecord(SFData.GRID_PLTBAR, row)
            totQty += sfData!!.getFieldInteger("qty")
            arrayList.add(sfData!!)
        }
        adapterList.updateList(arrayList)
        binding.run{
            vwInfo.tv2.text =  totQty.toString()
            vwInfo.tv3.text =  jList.getSize().toString()
        }

        binding.edBar.edItem.setText("")
        binding.edBar.edItem.requestFocus()
        binding.edBar.edItem.selectAll()
    }

    fun notifyMsg_Reg(msg:ResultMsg2){
        if ( msg.result_cd == AppInfo.RESULT_ERR){
            SoundPlay(context, R.raw.err)
            var messg = msg.result_msg
        }
        else{
            SoundPlay(context, R.raw.ok)
            textFieldInit()
            init()
        }
    }
}