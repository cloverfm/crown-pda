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
import com.sf.cwms.databinding.FragmentIo2101Binding
import com.sf.cwms.data.DataInfo
import com.sf.cwms.data.InbViewModel
import com.sf.cwms.util.*
import retrofit2.Call

/**
 * A simple [Fragment] subclass.
 * Use the [Io2100Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Io2101Fragment : BaseFragment<FragmentIo2101Binding>(R.layout.fragment_io2101){
    lateinit var adapterList: SFRvJSONAdapter
    var arrayList: ArrayList<SFData> = ArrayList<SFData>()
    var loc_cd : String = ""
    var pk_no : String = ""
    var pk_dat :String = ""

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
        bScanFlag = false

        binding.run {
            txTitle.text = "피킹품목선택"
            gridTitle.tv1.text = "출고장"
            gridTitle.tv2.text = "품목"
            gridTitle.tv3.text = "BOX"
            gridTitle.tv4.text = "PLT"

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

        val obj1 = object: OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                userLog("rv click=>" + position)
                (getActivity() as MainActivity).selData2 = sfData
      //          (getActivity() as MainActivity).SelectMenu(AppInfo.io2102, "피킹")
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
                        //EditFocus()
                    }
                }
            })
        }
        var currentMenu = (getActivity() as MainActivity).currentMenu
        binding.run {
            btPrev.setOnClickListener {
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2000, "피킹")
            }
            btReg.setOnClickListener {
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2102, "피킹")
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
            var sfData = (getActivity() as MainActivity).selData1
            if ((getActivity() as MainActivity).isMode.equals("1")){
                pk_dat = sfData.getFieldString(DataInfo.pk_dat)
                loc_cd = sfData.getFieldString(DataInfo.loc_cd)
            }
            else{
                pk_no = sfData.getFieldString(DataInfo.pk_no)
            }
        }
    }

    fun init()
    {
        val action = AppInfo.action_item
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        if ((getActivity() as MainActivity).isMode.equals("1")) {
            val call: Call<String?>? =
                apiService.PKList(
                    getHeader(),
                    action,
                    CWmsApp.cent_cd,
                    pk_dat,
                    CWmsApp.pk_grp_no,
                    loc_cd,
                    "",
                    "N"
                );
            callApi(call, AppInfo.io2101, AppInfo.api_PickList, action);
        }else{
            val call: Call<String?>? =
                apiService.PickUserList(
                    getHeader(),
                    action,
                    CWmsApp.cent_cd,
                    loc_cd,
                    pk_no,
                    CWmsApp.pk_grp_no,
                    "",
                    "",
                    "N",
                    CWmsApp.user_cd
                );
            callApi(call, AppInfo.io2101, AppInfo.api_PickList, action);
        }
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
            if (CWmsApp.action.equals(AppInfo.action_item)) {
                val jList = SFDataList( SFData.GRID_2101, msg, "table")
                if (jList.getSize() == 0){
                    bDataExist = false
                    val sfData = SFData(SFData.TEXT_MSG, "{}");
                    sfData.setField("result_msg", "피킹대상이 존재하지 않습니다.")
                    arrayList.add(sfData!!)
                    adapterList.updateList(arrayList)
                    return;
                }
                for (row in 0 until jList.getSize()) {
                    var sfData = jList.getRecord(SFData.GRID_2101, row)
                    arrayList.add(sfData!!)
                }
                adapterList.updateList(arrayList)
            }
        }
    }


}