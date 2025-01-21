package com.sf.cwms.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.sf.cwms.R
import com.sf.cwms.databinding.SpinnerItemBinding

class SFSpinnerAdapter  (
    context: Context,
    @LayoutRes private val resId: Int,
    private val values: ArrayList<SFData>
): ArrayAdapter<SFData>(context, resId, values) {
    var col_code = "CODE"
    var col_name = "CODE_NAME"

    fun setColumnName(cd:String, name :String){
        col_code = cd
        col_name = name
    }

    override fun getCount() = values.size
    override fun getItem(position: Int) = values[position]
    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }



    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = values[position]
        try {
            //binding.imgSpinner.setImageResource(model.image)
            //binding.imgSpinner.setColorFilter(ContextCompat.getColor(context, R.color.white))
            /*
            binding.tvText.apply {

                if (parent is Spinner){
                    background = context.getDrawable(R.drawable.bg_spinner)
                    if(parent.selectedItemPosition < 0 ){
                      //  parent.setHeight(context, 40)
                        setTextColor(Color.parseColor("#626466"))
                        text = "선택"
                    }
                }

            }

             */

            binding.tvText.apply{
                text = model!!.getFieldString(col_name)
                background = context.getDrawable(R.drawable.bg_spinner) // (ContextCompat.getColor(context, R.color.spinner))
                setTextColor(ContextCompat.getColor(context, R.color.spinner_text))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = SpinnerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val model = values[position]
        try {
            // binding.imgSpinner.setImageResource(model.image)
            binding.tvText.text = model.getFieldString(col_name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

}