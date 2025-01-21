package com.sf.cwms.util
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.sf.cwms.R


class BaseDialog(context: Context, layoutId: Int) : Dialog(context) {
    protected var mContext: Context

    init {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(layoutId)
        mContext = context
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        val window: Window = getWindow()!!
        if (window != null) {
            // 백그라운드 투명
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params: WindowManager.LayoutParams = window.getAttributes()
            // 화면에 가득 차도록
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.height = WindowManager.LayoutParams.MATCH_PARENT

            // 열기&닫기 시 애니메이션 설정
            params.windowAnimations = R.style.AnimationPopupStyle
            window.setAttributes(params)
            // UI 하단 정렬
            window.setGravity(Gravity.BOTTOM)
        }
    }
}