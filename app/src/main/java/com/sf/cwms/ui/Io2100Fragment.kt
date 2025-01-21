package com.sf.cwms.ui

import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sf.cwms.CWmsApp
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.databinding.FragmentIo2100Binding
import com.sf.cwms.data.DataInfo
import com.sf.cwms.data.InbViewModel
import com.sf.cwms.util.*
import retrofit2.Call

/**
 * A simple [Fragment] subclass.
 * Use the [Io2100Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Io2100Fragment : BaseFragment<FragmentIo2100Binding>(R.layout.fragment_io2100) {
    lateinit var adapterList: SFRvJSONAdapter
    var arrayList: ArrayList<SFData> = ArrayList<SFData>()

    override fun initView() {
        super.initView()
        binding.run{
            btReg.text = "새로고침"
        }
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        textFieldInit();
    }

    override fun initListener() {
        super.initListener()

        val obj1 = object : OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                if ( bDataExist == true) {
                    userLog("rv click=>" + position)
                    (getActivity() as MainActivity).selData1 = sfData
                    (getActivity() as MainActivity).SelectMenu(AppInfo.io2101, "피킹")
                }
            }
        }

        val obj2 = object : OnLongClickInterface {
            override fun onLongClick(sfData: SFData, position: Int) {
                userLog("rv Long click=>" + position)
            }
        }
        binding.rv.run {
            adapterList = SFRvJSONAdapter(obj1, obj2)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterList
            val customDecoration =
                CustomDecoration(2f, 1f, ContextCompat.getColor(context, R.color.grid_line))
            addItemDecoration(customDecoration)
        }

        binding.apply {
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
            btPrev.setOnClickListener {
                (getActivity() as MainActivity).ReturnMenu()
            }
            btReg.setOnClickListener {
                init();
            }
        }
    }

    override fun afterOnCreate() {
        super.afterOnCreate()
        userLog("io1100 AfterOnCreate " + CWmsApp.appStart)
        init()
        if (CWmsApp.appStart) {
            //binding.spRackNo.edItem.setSelection(RapidApp.loc_pos)
        }
    }

    fun textFieldInit() {
        binding.run {

        }
    }

    fun init() {
        var pk_no =  (getActivity() as MainActivity).selData.getFieldString(DataInfo.pk_no)
        var pk_dat =  (getActivity() as MainActivity).selData.getFieldString(DataInfo.pk_dat)
        val action = AppInfo.action_loc_cd
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.PickUserList(getHeader(), action, CWmsApp.cent_cd, "", pk_no, "", "","", "N", CWmsApp.user_cd);
        callApi(call, AppInfo.io2101, AppInfo.api_PickList, action);
    }

    fun update() {

    }

    fun cancel()
    {
    }

    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
        if ( msgtype.equals(AppInfo.MSG_COMM)) {
            val resultMsg: ResultMsg2? = getResultRData(msg!!)
            if (resultMsg == null) {
                SoundPlay(context, R.raw.err)
                setCustomToast(context!!,"Error Json Format Type");
                return
            }
            if (resultMsg.result_cd.equals(AppInfo.O_ERR)) {
                SoundPlay(context, R.raw.err)
                setCustomToast(context!!,resultMsg.result_msg);
                return
            }
            if (CWmsApp.action.equals(AppInfo.action_loc_cd)) {
                arrayList.clear()
                val jList = SFDataList( SFData.GRID_COL, msg, "table")
                bDataExist = true
                if (jList.getSize() == 0){
                    bDataExist = false
                    val sfData = SFData(SFData.TEXT_MSG, "{}");
                    sfData.setField("result_msg", "피킹지시가 없습니다")
                    arrayList.add(sfData!!)
                    adapterList.updateList(arrayList)
                    return;
                }
                for (row in 0 until jList.getSize()) {
                    var sfData = jList.getRecord(SFData.GRID_2100, row)
                    arrayList.add(sfData!!)
                }
                adapterList.updateList(arrayList)
            }
        }
    }
}