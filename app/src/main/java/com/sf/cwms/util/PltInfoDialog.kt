package com.sf.cwms.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sf.cwms.MainActivity
import com.sf.cwms.R
import com.sf.cwms.data.DataInfo
import com.sf.cwms.databinding.DialogPltInfoBinding

class PltInfoDialog : DialogFragment()
{
    lateinit var binding: DialogPltInfoBinding
    lateinit var adapterList:SFRvJSONAdapter
    var arrayList:ArrayList<SFData> = ArrayList<SFData>();
    var gridType:Int = SFData.GRID_COL3;
    var gridTType:Int = SFData.GRID_COL3T;
    var step:String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_plt_info, container, false)
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.97).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogPltInfoBinding.inflate(getLayoutInflater());
        init();
    }

    fun init(){
        var dlgTitle = (getActivity() as MainActivity).dlgTitle
        binding.tvTitle.text =dlgTitle

        binding.run{
            gridTitle.tv1.text = "위치"
            gridTitle.tv2.text = "생산일"
            gridTitle.tv3.text = "순번"
            gridTitle.tv4.text = "수량"

            gridTitle.lin.setBackgroundColor( ContextCompat.getColor(binding.root.context, R.color.caption_stoke))
            gridTitle.tv1.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv2.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv3.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            gridTitle.tv4.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.caption))
            step =(getActivity() as MainActivity).step
            if (step.equals("40")|| step.equals("50")){
                binding.btMenu1.text = "취소불가"
            }
            else{
                binding.btMenu1.text = "출고취소"
            }
        }
        val obj1 = object: OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                UserLog.userLog("rv click=>" + position)
            }
        }
        val obj2 = object: OnLongClickInterface {
            override fun onLongClick(sfData: SFData, position: Int) {
                UserLog.userLog("rv Long click=>" + position)
            }
        }

        var msg = (getActivity() as MainActivity).commMsg
        var sel = (getActivity() as MainActivity).dlgGridColSel
        var jList = SFDataList(sel, msg, "table")

        if (jList.getSize() == 0){
            return;
        }
        arrayList.clear()
        for (row in 0 until jList.getSize()) {
            var sfData = jList.getRecord(sel, row)
            if (row == 0) step = sfData!!.getFieldString(DataInfo.step)
            arrayList.add(sfData!!)
        }
        if (step.equals("40")|| step.equals("50")){
            binding.btMenu1.text = "취소불가"
        }
        else{
            binding.btMenu1.text = "출고취소"
        }

        binding.rv.apply{
            adapterList = SFRvJSONAdapter(obj1, obj2)
            layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterList
            val customDecoration = CustomDecoration(2f, 1f,   ContextCompat.getColor(binding.root.context, R.color.grid_line))
            addItemDecoration(customDecoration)
            adapterList.updateList(arrayList);
            adapterList.notifyDataSetChanged()
            adapterList.selectedPosition = -1
        }
        binding.apply{
            btMenu1.setOnClickListener {
                UserLog.userLog( "PLTINFO=> step value:" + step)
                if (step.equals("20")|| step.equals("30")){
                    if (adapterList.selectedPosition > -1) {
                        var data = arrayList.get(adapterList.selectedPosition );
                        buttonClickListener.onButton1Clicked(data)
                        dismiss()
                    }
                    else{
                    }
                }
            }
            btMenu2.setOnClickListener {
                buttonClickListener.onButton2Clicked()
                dismiss()
            }
        }
    }

    interface OnButtonClickListener {
        fun onButton1Clicked(param:SFData)
        fun onButton2Clicked()
        fun onButton3Clicked()
    }

    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener

}