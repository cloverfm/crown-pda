package com.sf.cwms.ui

import android.app.DatePickerDialog
import android.graphics.Color
import android.text.InputType
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
import com.sf.cwms.databinding.FragmentIo2000Binding
import com.sf.cwms.databinding.FragmentIo2201Binding
import com.sf.cwms.util.*
import retrofit2.Call
import java.util.*
import kotlin.collections.ArrayList

class Io2000Fragment : BaseFragment<FragmentIo2000Binding>(R.layout.fragment_io2000){
    lateinit var adapterList: SFRvJSONAdapter
    var arrayList: ArrayList<SFData> = ArrayList<SFData>()
    var fr_dat:String = ""
    var to_dat:String = ""
    var car_no:String = ""
    var mode:Int = 1

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        binding.run {
            // txTitle.text =(getActivity() as MainActivity).selData1.getFieldString(DataInfo.loc_nm) + " 배송처 선택"
            txTitle.text ="피킹 선택"
            fr_dat =  CurrentDate()
            to_dat =  CurrentDate()
            if (CWmsApp.to_date.length > 0 ){
                fr_dat = CWmsApp.to_date
                to_dat = CWmsApp.to_date
            }

            edDate.tvDate.setText(fr_dat)
            edDate.txCap.text = "일자"
            btReg.text = "새로고침"
        }
        textFieldInit()
        ViewFlag()
    }

    override fun initListener() {
        super.initListener()

        val obj1 = object: OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                CWmsApp.to_date = sfData.getFieldString(DataInfo.pk_dat)

                (getActivity() as MainActivity).selData1 = sfData
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2101, "피킹")

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
            edDate.tvDate.setOnClickListener { v -> UserDatePicker2() }

            rv.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if (hasFocus) {
                        //EditFocus()
                    }
                }
            })
        }
        var currentMenu = (getActivity() as MainActivity).currentMenu
        binding.run {
            btTot.setOnClickListener {
                //(getActivity() as MainActivity).SelectMenu(AppInfo.io2200, "출고")
                (getActivity() as MainActivity).isMode = "1"
                ViewFlag()
                init()
            }
            btOrd.setOnClickListener {
                //(getActivity() as MainActivity).SelectMenu(AppInfo.io2200, "출고")
                (getActivity() as MainActivity).isMode = "2"
                ViewFlag()
                init()
            }
            bt1.setOnClickListener {
                //(getActivity() as MainActivity).SelectMenu(AppInfo.io2200, "출고")
                CWmsApp.pk_grp_no  = "1"
                ViewFlag()
                init()
            }
            bt2.setOnClickListener {
                //(getActivity() as MainActivity).SelectMenu(AppInfo.io2200, "출고")
                CWmsApp.pk_grp_no  = "2"
                ViewFlag()
                init()
            }
            bt3.setOnClickListener {
                //(getActivity() as MainActivity).SelectMenu(AppInfo.io2200, "출고")
                CWmsApp.pk_grp_no  = "3"
                ViewFlag()
                init()
            }
            btPrev.setOnClickListener {
                //(getActivity() as MainActivity).SelectMenu(AppInfo.io2200, "출고")
                (getActivity() as MainActivity).ReturnMenu()
            }
            btReg.setOnClickListener {
                //   CWmsApp.to_date = fr_dat
                init()
                //   (getActivity() as MainActivity).SelectMenu(AppInfo.io2100, "피킹장선택")
            }
        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        userLog("io2201 AfterOnCreate " + CWmsApp.appStart)
        init()
        if ( CWmsApp.appStart){
            //binding.spRackNo.edItem.setSelection(RapidApp.loc_pos)
        }


    }

    fun textFieldInit()
    {
        binding.run {
            if ( CWmsApp.to_date.length > 0){
                fr_dat = CWmsApp.to_date
                to_dat = CWmsApp.to_date
            }
            edDate.tvDate.setText(fr_dat)
        }
    }

    fun ViewFlag(){
        binding.run{
            var sSelColor ="#a93e67"
            var sNonColor = "#93a3b4"
            btTot.setTextColor(Color.parseColor(sNonColor))
            btOrd.setTextColor(Color.parseColor(sNonColor))
            if ( (getActivity() as MainActivity).isMode.equals("1")) {
                btTot.setTextColor(Color.parseColor(sSelColor))
                txTitle.text ="토탈 피킹"

            }
            else {
                btOrd.setTextColor(Color.parseColor(sSelColor))
                txTitle.text ="오더 피킹"

            }
            bt1.setTextColor(Color.parseColor(sNonColor))
            bt2.setTextColor(Color.parseColor(sNonColor))
            bt3.setTextColor(Color.parseColor(sNonColor))
            if ( CWmsApp.pk_grp_no.equals("1")) {
                bt1.setTextColor(Color.parseColor(sSelColor))
            }
            if ( CWmsApp.pk_grp_no.equals("2")) {
                bt2.setTextColor(Color.parseColor(sSelColor))
            }
            if ( CWmsApp.pk_grp_no.equals("3")) {
                bt3.setTextColor(Color.parseColor(sSelColor))
            }

        }
    }

    fun init()
    {
        getList()
    }

    fun getList()
    {
        var view_flag = (getActivity() as MainActivity).isMode
        var action = AppInfo.action_pk_no
        if (view_flag.equals("1")) action = AppInfo.action_loc_cd
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? = apiService.PKUserList(getHeader(), action, CWmsApp.cent_cd, fr_dat, CWmsApp.pk_grp_no, "", "", "N", CWmsApp.user_cd);
        callApi(call, AppInfo.io2000, AppInfo.api_PKList, action);
    }

    fun update( )
    {

    }
    fun cancel()
    {
    }

    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
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
            if (CWmsApp.action.equals(AppInfo.action_pk_no)) {
                val jList = SFDataList( SFData.GRID_2000, msg, "table")
                userLog("list_size->" + jList.getSize())
                arrayList.clear()
                if (jList.getSize() == 0){
                    bDataExist = false
                    val sfData = SFData(SFData.TEXT_MSG, "{}");
                    sfData.setField("result_msg", "대상이 존재하지 않습니다.")
                    arrayList.add(sfData!!)
                    adapterList.updateList(arrayList)
                     return;
                }
                userLog("list_add")
                for (row in 0 until jList.getSize()) {
                    var sfData = jList.getRecord(SFData.GRID_2000, row)
                    arrayList.add(sfData!!)
                }
                adapterList.updateList(arrayList)
            }
            if (CWmsApp.action.equals(AppInfo.action_loc_cd)) {
                val jList = SFDataList( SFData.GRID_2000, msg, "table")
                userLog("list_size->" + jList.getSize())
                arrayList.clear()
                if (jList.getSize() == 0){
                    bDataExist = false
                    val sfData = SFData(SFData.TEXT_MSG, "{}");
                    sfData.setField("result_msg", "대상이 존재하지 않습니다.")
                    arrayList.add(sfData!!)
                    adapterList.updateList(arrayList)
                    return;
                }
                userLog("list_add")
                for (row in 0 until jList.getSize()) {
                    var sfData = jList.getRecord(SFData.GRID_2100, row)
                    arrayList.add(sfData!!)
                }
                adapterList.updateList(arrayList)
            }
        }
    }
    fun UserDatePicker2(){
        val cal = Calendar.getInstance()    //캘린더뷰 만들기
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            //dateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
            //result.text = "날짜/시간 : "+dateString + " / " + timeString

            val mDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
            set_date(mDate)
        }
        DatePickerDialog(binding.root.context, dateSetListener, cal.get(Calendar.YEAR),cal.get(
            Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()

    }
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val mDate = String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
            set_date(mDate)
    }

    fun UserDatePicker() {
        binding.apply {
            val sDate: String = edDate.tvDate.getText().toString()
            val arrDate = sDate.split("-").toTypedArray()
            DatePickerDialog(
                root.context, dateSetListener, arrDate[0].toInt(),
                arrDate[1].toInt() -1, arrDate[2].toInt()
            ).show()

        }
    }
    fun set_date(mDate: String?) {
        binding.edDate.tvDate.setText(mDate)
        fr_dat = mDate!!.replace("-", "")
        to_dat = mDate!!.replace("-", "")
        getList()
    }

}