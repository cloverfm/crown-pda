package com.sf.cwms

import android.view.View
import androidx.core.content.ContextCompat
import com.sf.cwms.api.ApiService
import com.sf.cwms.api.ApiServiceGenerator
import com.sf.cwms.base.BaseFragment
import com.sf.cwms.data.DataInfo
import com.sf.cwms.databinding.FragmentHomeBinding
import com.sf.cwms.util.*
import retrofit2.Call

// 메뉴 선택
class HomeFragment  : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home){
    var bTest :Boolean = false
    override fun initView() {
        super.initView()
        userLog("Home ")
        // 타이틀 설정
        binding.run {

            vwCentCd.txCap.text = "센타"
            vwUserNo.txCap.text = "작업자"
            vwLoginDt.txCap.text = "로그인일시"
            vwPdaNo.txCap.text = "PDA 번호";

            vwCentCd.tvValue.text =  CWmsApp.cent_nm
            vwUserNo.tvValue.text = CWmsApp.user_nm
            vwLoginDt.tvValue.text =  CWmsApp.login_dt
            vwPdaNo.tvValue.text =CWmsApp.pda_nm

            vwCentCd.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.darkprimary))
            vwUserNo.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.darkprimary))
            vwLoginDt.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.darkprimary))
            vwPdaNo.txCap.setTextColor(ContextCompat.getColor(binding.root.context, R.color.darkprimary))

            vwCentCd.tvValue.setTextColor(ContextCompat.getColor(root.context, R.color.BlueGray900))
            vwUserNo.tvValue.setTextColor(ContextCompat.getColor(root.context, R.color.BlueGray900))
            vwLoginDt.tvValue.setTextColor(ContextCompat.getColor(root.context, R.color.BlueGray900))
            vwPdaNo.tvValue.setTextColor(ContextCompat.getColor(root.context, R.color.BlueGray900))

            vwCentCd.txCap.background = ContextCompat.getDrawable(binding.root.context, R.drawable.caption_rds_top_left)
            vwCentCd.llValue.background = ContextCompat.getDrawable(binding.root.context, R.drawable.text_rds_top_right)
            vwPdaNo.txCap.background = ContextCompat.getDrawable(binding.root.context, R.drawable.caption_rds_bottom_left)
            vwPdaNo.llValue.background = ContextCompat.getDrawable(binding.root.context, R.drawable.text_rds_bottom_right)

            (getActivity() as MainActivity).findOk = false

        }
    }

    override fun initViewModel() {
        super.initViewModel()
        userLog("Home initViewModel")
    }

    override fun initListener() {
        super.initListener()
        userLog("Home initListener")
        binding.apply {
            vwPdaNo.txCap.setOnClickListener{
                getPdaList()
            }
            vwPdaNo.tvValue.setOnClickListener{
                getPdaList()
            }

            btPrev.setOnClickListener {
                (getActivity() as MainActivity).apply {
                    val dialog = ConfirmDialog()
                    dialog.setButtonClickListener(object: ConfirmDialog.OnButtonClickListener{
                        override fun onOkClicked() {
                            (getActivity() as MainActivity).Logout()
                        }
                        override fun onCancelClicked() {
                        }
                    })
                    dialog.show(supportFragmentManager, "CustomDialog")
                }
            }
            btSetting.setOnClickListener {
                CWmsApp.Init()
                (getActivity() as MainActivity).SelectMenu(AppInfo.setting, "설정")
            }
            cardView1.setOnClickListener {
                CWmsApp.Init()
                (getActivity() as MainActivity).SelectMenu(AppInfo.io1100, "입고")
            }
            cardView2.setOnClickListener {
                CWmsApp.Init()
                (getActivity() as MainActivity).isMode = "2"
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2000, "피킹")
            }
            cardView3.setOnClickListener {
                CWmsApp.Init()
                (getActivity() as MainActivity).SelectMenu(AppInfo.io2201, "출고")
            }
            cardView4.setOnClickListener {
                CWmsApp.Init()
                (getActivity() as MainActivity).preMenu = ""
                (getActivity() as MainActivity).SelectMenu(AppInfo.io1200, "적치")
            }
            cardView5.setOnClickListener {
                CWmsApp.Init()
                (getActivity() as MainActivity).SelectMenu(AppInfo.io3200, "재고조회")
            }
            cardView6.setOnClickListener {
                CWmsApp.Init()
                (getActivity() as MainActivity).preMenu = ""
                (getActivity() as MainActivity).SelectMenu(AppInfo.io3100, "재고이동")
            }
        }
    }


    override fun afterOnCreate() {
        super.afterOnCreate()
     //   if ( CWmsApp.api_down == false) (getActivity() as MainActivity).PDA_APK_Ver()
   }

    fun getPdaList()
    {
        val action = "sub_cd1"
        val apiService = ApiServiceGenerator.createService(ApiService::class.java)
        val call: Call<String?>? =
            apiService.GetCombo(getHeader(), action, CWmsApp.cent_cd, "C0012", "");
        callApi(call, AppInfo.home, AppInfo.api_GetCombo, action);
    }
    override fun notifyMsg(result: Boolean, msgtype: String, msg: String) {
        super.notifyMsg(result, msgtype, msg)
        if( msgtype.equals(AppInfo.MSG_APK)){
            (getActivity() as MainActivity).ProgramDownload("Apk", CWmsApp.api_apkurl)
        }
        if (msgtype.equals(AppInfo.MSG_VER))
        {
            val ver = msg.split(",")
            if (!ver.get(0).equals(AppInfo.VERSION)){
                /*
                CWmsApp.api_apkurl = ver.get(1)
                CWmsApp.api_down = true

                binding.btDown.visibility = View.VISIBLE
                setCustomToast(this.context, "버전이 변경되었습니다. 프로그램 다운받으세요")
                 */
            }
        }

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
                ShowMessageBox(resultMsg.result_msg, "")
                return
            }
            userLog("notifyMsg1")
            (getActivity() as MainActivity).commMsg = msg
            (getActivity() as MainActivity).dlgTitle = "지게차 선택"
            (getActivity() as MainActivity).dlgGridColSel =SFData.GRID_COLSEL
            if (CWmsApp.action.equals(AppInfo.action_sub_cd1)) {
                val dialog = SelectDialog()
                dialog.setButtonClickListener(object: SelectDialog.OnButtonClickListener{
                    override fun onButton1Clicked(param: SFData) {
                        var locData = param;
                        binding.run{
                            CWmsApp.pda_no = param.getFieldString(DataInfo.code_cd)
                            CWmsApp.pda_nm = param.getFieldString(DataInfo.code_nm)
                            vwPdaNo.tvValue.text = param.getFieldString(DataInfo.code_nm)
                        }
                        (getActivity() as MainActivity).setPdaNumber()
                    }
                    override fun onButton2Clicked() {
                    }
                    override fun onButton3Clicked() {
                    }
                })
                dialog.show((getActivity() as MainActivity).supportFragmentManager, "CustomDialog")
            }
        }
    }


}