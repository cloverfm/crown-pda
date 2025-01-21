package com.sf.cwms.util
import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.sf.cwms.R

class CustomProgressDialog(context: Context?) : Dialog(
    context!!
) {
  //  private var imgLogo: ImageView? = null
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress)
      //  var imgLogo = findViewById<View>(R.id.img_android) as ImageView
      //  val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.animation)
      //  imgLogo!!.setAnimation(anim)
    }
}