package com.sf.cwms.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.sf.cwms.R
import com.sf.cwms.databinding.DialogInfoBinding
import com.sf.cwms.databinding.DialogMsgBinding

class InfoDialog : DialogFragment()
{
    lateinit var binding: DialogInfoBinding
    var title:String = "알림"
    var msg:String = "종료하시겠습니까?"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_info, container, false)
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
        val binding = DialogMsgBinding.inflate(getLayoutInflater());
        init();
    }

    fun init(){
        binding.apply{
            binding.tvTitle.setText(title)
            binding.tvMsg.setText(msg)

            btMenu1.setOnClickListener {
                buttonClickListener.onOkClicked()
                dismiss()
            }
        }
    }
    fun setTitle(msg:String, title:String)
    {
        this.title = title
        this.msg = msg
    }


    interface OnButtonClickListener {
        fun onOkClicked()
        fun onCancelClicked()
    }

    fun setButtonClickListener(buttonClickListener: OnButtonClickListener) {
        this.buttonClickListener = buttonClickListener
    }
    // 클릭 이벤트 실행
    private lateinit var buttonClickListener: OnButtonClickListener

}