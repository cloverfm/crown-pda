package com.sf.cwms.ui

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.graphics.Color
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sf.cwms.CWmsApp
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.data.DataInfo
import com.sf.cwms.databinding.FragmentIo2201Binding
import com.sf.cwms.util.*
import retrofit2.Call
import java.util.*
import kotlin.collections.ArrayList

/* 출고 리스트 선택  */
class Io2201Fragment : BaseFragment<FragmentIo2201Binding>(R.layout.fragment_io2201){
    lateinit var adapterList: SFRvJSONAdapter
    var arrayList: ArrayList<SFData> = ArrayList<SFData>()
    var fr_dat:String = ""
    var to_dat:String = ""
    var car_no:String = ""

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        binding.run {
           // txTitle.text =(getActivity() as MainActivity).selData1.getFieldString(DataInfo.loc_nm) + " 배송처 선택"
            txTitle.text ="배송처 선택"
            fr_dat =  CurrentDate()
            to_dat =  CurrentDate()
            edCar.edItem.setText("")
            edDate.tvDate.setText(fr_dat)
            edDate.txCap.text = "출고일"
            edCar.txCap.text = "배송처"
            gridTitle.tv1.text = "단계"
            gridTitle.tv2.text = "배송처"
            gridTitle.tv3.text = "지시"
            gridTitle.tv4.text = "출고"

            gridTitle.lin.setBackgroundColor( ContextCompat.getColor(binding.root.context, R.color.caption_stoke))
            gridTitle.tv1.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv2.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv3.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv4.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))

            btReg.text = "새로고침"
        }
        textFieldInit();
        ViewFlag()
    }

    override fun initListener() {
        super.initListener()
        binding.apply {

            edCar.edItem.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    hideKeyboard()
                    car_no = binding.edCar.edItem.text.toString()
                    getList()
                }
                false
            })
            edCar.edItem.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                }
            })

        }
        val obj1 = object: OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                userLog("rv click=>" + position)
                (getActivity() as MainActivity).selData2 = sfData
                CWmsApp.to_date = fr_dat
                CWmsApp.car_no = car_no
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2202, "출고")
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
            btOfind.setOnClickListener {
                CWmsApp.view_flag = "20|30"
                ViewFlag()
                init()
            }
            btCfind.setOnClickListener {
                CWmsApp.view_flag = "40|50"
                ViewFlag()
                init()
            }
            btPrev.setOnClickListener {
                //(getActivity() as MainActivity).SelectMenu(AppInfo.io2200, "출고")
                (getActivity() as MainActivity).ReturnMenu()
            }
            btReg.setOnClickListener {
                init()
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
        binding.edCar.edItem.requestFocus()

    }

    fun textFieldInit()
    {
        binding.run {
            if ( CWmsApp.to_date.length > 0){
                fr_dat = CWmsApp.to_date
                to_dat = CWmsApp.to_date
            }
            if ( CWmsApp.car_no.length > 0){
                car_no = CWmsApp.car_no
            }
            edCar.edItem.setText(car_no)
            edDate.tvDate.setText(fr_dat)
        }
    }

    fun ViewFlag(){
        binding.run {
            var sSelColor ="#a93e67"
            var sNonColor = "#93a3b4"
            btOfind.setTextColor(Color.parseColor(sNonColor))
            btCfind.setTextColor(Color.parseColor(sNonColor))
            if ( CWmsApp.view_flag.equals("20|30")) btOfind.setTextColor(Color.parseColor(sSelColor))
            else btCfind.setTextColor(Color.parseColor(sSelColor))
        }
    }

    fun init()
    {
        getList()
    }

    fun getList()
    {
        val action = AppInfo.action_car_no
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.OubUserPList(getHeader(), action, CWmsApp.cent_cd, fr_dat, to_dat, car_no, CWmsApp.view_flag, CWmsApp.user_cd);
        callApi(call, AppInfo.io2201, AppInfo.api_OubPList, action);
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
            if (CWmsApp.action.equals(AppInfo.action_car_no)) {
                val jList = SFDataList( SFData.GRID_2201, msg, "table")
                userLog("list_size->" + jList.getSize())
                arrayList.clear()
                if (jList.getSize() == 0){
                    SoundPlay(context!!, R.raw.err)
                    bDataExist = false
                    val sfData = SFData(SFData.TEXT_MSG, "{}");
                    sfData.setField("result_msg", "출고대상이 존재하지 않습니다.")
                    arrayList.add(sfData!!)
                    adapterList.updateList(arrayList)
                    return;
                }
                SoundPlay(context!!, R.raw.info)
                userLog("list_add")
                for (row in 0 until jList.getSize()) {
                    var sfData = jList.getRecord(SFData.GRID_2201, row)
                    arrayList.add(sfData!!)
                }
                adapterList.updateList(arrayList)
            }
        }
    }

    fun UserDatePicker2(){
        val cal = Calendar.getInstance()    //캘린더뷰 만들기
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val mDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
            set_date(mDate)
        }
        DatePickerDialog(binding.root.context, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
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