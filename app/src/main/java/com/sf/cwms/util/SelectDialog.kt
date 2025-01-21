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
import com.sf.cwms.databinding.DialogSelectBinding
import com.sf.cwms.util.UserLog.Companion.userLog

class SelectDialog: DialogFragment()
{
    lateinit var binding: DialogSelectBinding
    lateinit var adapterList:SFRvJSONAdapter
    var arrayList:ArrayList<SFData> = ArrayList<SFData>();
    var gridType:Int = SFData.GRID_COL3;
    var gridTType:Int = SFData.GRID_COL3T;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_select, container, false)
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        //  val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        //  val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        //  dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = DialogSelectBinding.inflate(getLayoutInflater());
        init();
    }

    fun init(){
        var dlgTitle = (getActivity() as MainActivity).dlgTitle
        binding.tvTitle.text =dlgTitle
        val obj1 = object: OnClickInterface {
            override fun onClick(sfData: SFData, position: Int) {
                userLog("rv click=>" + position)
            }
        }
        val obj2 = object: OnLongClickInterface {
            override fun onLongClick(sfData: SFData, position: Int) {
                userLog("rv Long click=>" + position)
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
            arrayList.add(sfData!!)
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
                if (adapterList.selectedPosition > -1) {
                    var data = arrayList.get(adapterList.selectedPosition );
                    buttonClickListener.onButton1Clicked(data)
                    dismiss()
                }
                else{

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