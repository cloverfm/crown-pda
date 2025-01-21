package com.sf.cwms.util

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.sf.cwms.R;
import com.sf.cwms.data.DataInfo
import com.sf.cwms.databinding.*

class SFRvJSONAdapter (onClick: OnClickInterface,
                       onLongClick:OnLongClickInterface):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val select_color = Color.parseColor("#eeeeee")
    private val select_bkcolor = Color.parseColor("#471f71")
    private val noselect_color = Color.parseColor("#471f71")
    private val noselect_bkcolor = Color.parseColor("#eeeeee")
    var total_types = 0
    var selectedPosition = -1
    private val fabstatVolume = false

    var selectedColPosition = -1
    var onClickInterface:OnClickInterface = onClick
    var onLongClickInterface:OnLongClickInterface = onLongClick


    interface OnButtonClickListener {
        fun onListBtnClick(position: Int, btnid: Int)
    }

    // 생성자로부터 전달된 resource id 값을 저장.
    var resourceId = 0

    // 생성자로부터 전달된 ListBtnClickListener  저장.
    private val onButtonClickListener: OnButtonClickListener? = null
    class SFDataViewHolder(parent: ViewGroup,
                           viewType: Int,
                           val gridBinding: ViewDataBinding?,
                           val onClick: OnClickInterface,
                           val onLongClick:OnLongClickInterface): RecyclerView.ViewHolder(gridBinding!!.root){
        //  lateinit var  position : Int
        val itemType = viewType
        fun bind(item: SFData) {
            when(item.type) {
                SFData.TEXT_MSG-> {
                    var grBinding = gridBinding as TextMsgBinding
                    grBinding.run {
                        txMsg.text = item.getFieldString("result_msg")
                    }
                }

                SFData.GRID_COL-> {
                    var grBinding = gridBinding as GridColBinding
                    grBinding.run {
                        tv1.text = item.getFieldString("loc_nm")
                        tv2.text = item.getFieldString("item_cnt")
                        tv3.text = item.getFieldString("qty")
                    }
                }
                SFData.GRID_COLSEL-> {
                    var grBinding = gridBinding as GridColSelBinding
                    grBinding.run {
                        tv1.text = item.getFieldString("code_cd")
                        tv2.text = item.getFieldString("code_nm")
                    }
                }
                SFData.GRID_EMPTY_LOC-> {
                    var grBinding = gridBinding as GridLoccolBinding
                    grBinding.run {
                        tv1.text = item.getFieldString("loc_cd")
                    }
                }
                SFData.GRID_LOCSEL-> {
                    var grBinding = gridBinding as GridCol3Binding
                    grBinding.run {
                        tv1.text = item.getFieldString("loc_cd")
                        tv2.text = item.getFieldString("loc_typen")
                        tv3.text = item.getFieldString("loc_nm")
                    }
                }

                SFData.GRID_2000-> {
                    var grBinding = gridBinding as GridCol2000Binding
                    grBinding.run {
                        tv1.text = item.getFieldString("pk_no")
                        tv2.text = item.getFieldString("loc_nm")
                        tv3.text = item.getFieldString("cust_nm")
                    }
                }

                SFData.GRID_2100-> {
                    var grBinding = gridBinding as GridCol2Binding
                    grBinding.run {
                        tv1.text = item.getFieldString("loc_nm")
                        tv2.text = item.getFieldString("plt")
                    }
                }
                SFData.GRID_PLTBAR-> {
                    var grBinding = gridBinding as GridColOubPltBinding
                    grBinding.run {
                        tv1.text = item.getFieldString(DataInfo.loc_nm)
                        var mfg_dat =item.getFieldString(DataInfo.mfg_dat)
                        if (mfg_dat.length == 8){
                            var smfg_dat = mfg_dat.substring(0,4) + "-" + mfg_dat.substring(4 ,6) + "-" + mfg_dat.substring(6, 8)
                            mfg_dat = smfg_dat
                        }
                        tv2.text = item.getFieldString(DataInfo.mfg_dat)
                        tv3.text = item.getFieldString(DataInfo.seq)
                        tv4.text = item.getFieldString(DataInfo.qty)
                        //      tv4.text = item.getFieldString(DataInfo.plt_bar)
                    }
                }
                SFData.GRID_2200-> {
                    var grBinding = gridBinding as GridCol2Binding
                    grBinding.run {
                        tv1.text = item.getFieldString("loc_nm")
                        tv2.text = item.getFieldString("cnt")
                    }
                }

                SFData.GRID_2101-> {
                    var grBinding = gridBinding as GridCol5Binding
                    grBinding.run {
                        tv2.text = item.getFieldString(DataInfo.item_nm)
                        tv3.text = item.getFieldString(DataInfo.qty)
                        tv4.text = item.getFieldString(DataInfo.plt)
                        tv1.text = item.getFieldString(DataInfo.loc_nm)
                    }
                }

                SFData.GRID_2201-> {
                    var grBinding = gridBinding as GridCol5Binding
                    grBinding.run {
                        tv1.text = item.getFieldString(DataInfo.stepn)
                        tv2.text = item.getFieldString(DataInfo.cust_nm)
                        tv3.text = item.getFieldString(DataInfo.qty)
                        tv4.text = item.getFieldString(DataInfo.c_qty)
                    }
                }

                SFData.GRID_2102-> {
                    var grBinding = gridBinding as GridCol4Binding
                    grBinding.run {
                        tv1.text = item.getFieldString(DataInfo.loc_nm)
                        var mfg_dat =item.getFieldString(DataInfo.mfg_dat)
                        if (mfg_dat.length == 8){
                            var smfg_dat = mfg_dat.substring(0,4) + "-" + mfg_dat.substring(4 ,6) + "-" + mfg_dat.substring(6, 8)
                            mfg_dat = smfg_dat
                        }
                        tv2.text = item.getFieldString(DataInfo.mfg_dat)
                        tv3.text = item.getFieldString(DataInfo.qty)
                  //      tv4.text = item.getFieldString(DataInfo.plt_bar)
                    }
                }
                SFData.GRID_2202-> {
                    var grBinding = gridBinding as GridCol41Binding
                    grBinding.run {
                        var hold = item.getFieldString("hold")
                        tv1.text = item.getFieldString(DataInfo.item_nm)
                        tv2.text = item.getFieldString(DataInfo.loc_nm)
                        tv3.text = item.getFieldString(DataInfo.qty)
                        tv4.text = item.getFieldString(DataInfo.c_qty)

                        if (hold.equals("Y")){
                            tv1.setTextColor(R.color.darkprimary)
                            tv2.setTextColor(R.color.darkprimary)
                            tv3.setTextColor(R.color.darkprimary)
                            tv4.setTextColor(R.color.darkprimary)
                        }
                        else{
                            tv1.setTextColor(R.color.BlueGray900)
                            tv2.setTextColor(R.color.BlueGray900)
                            tv3.setTextColor(R.color.BlueGray900)
                            tv4.setTextColor(R.color.BlueGray900)

                        }
                    }
                }
                SFData.GRID_3200-> {
                    var grBinding = gridBinding as GridCol3200Binding
                    grBinding.run {
                        var gb =  item.getFieldString("action")
                        var pk =  item.getFieldString("pk")
                        if (gb.equals(DataInfo.loc_cd)) {
                            tv1.text = item.getFieldString(DataInfo.item_nm)
                        }
                        else{
                            var loc_type = item.getFieldString("loc_type")
                            if (loc_type.equals("02"))
                                tv1.text = item.getFieldString(DataInfo.loc_nm)
                            else
                                tv1.text = item.getFieldString(DataInfo.loc_cd)
                        }
                        tv2.text = item.getFieldString(DataInfo.mfg_dat)
                        var tmp = item.getFieldString(DataInfo.plt_bar)
                        if ( tmp.length> 18) {
                            var seq = tmp.substring(14, 18)
                            tv3.text = seq
                        }
                        tv4.text = item.getFieldString(DataInfo.qty)
                        //      tv4.text = item.getFieldString(DataInfo.plt_bar)
                    }
                }

                else-> throw IllegalArgumentException("Invalid ViewType")
            }
        }

        fun setColor(colorString: Int, colorRecString: Int) {
            when (itemType) {
                SFData.GRID_COL -> {
                    (gridBinding as GridColBinding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.view_stroke) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_EMPTY_LOC -> {
                    (gridBinding as GridLoccolBinding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.view_stroke) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_COLSEL -> {
                    (gridBinding as GridColSelBinding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_PLTBAR -> {

                    (gridBinding as GridColOubPltBinding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv3.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv4.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_LOCSEL -> {
                    (gridBinding as GridCol3Binding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv3.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_2000 -> {
                    (gridBinding as GridCol2000Binding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv3.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_2100 -> {
                    (gridBinding as GridCol2Binding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_2200 -> {
                    (gridBinding as GridCol2Binding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_2101 -> {
                    (gridBinding as GridCol5Binding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv3.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv4.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_2201 -> {
                    (gridBinding as GridCol5Binding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv3.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv4.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1

                SFData.GRID_2102 -> {
                    (gridBinding as GridCol4Binding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv3.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                     //   tv4.apply {
                     //       setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                     //       setTextColor(ContextCompat.getColor(root.context,colorString))
                     //   }
                    }
                }// end Grid_col1
                SFData.GRID_2202 -> {
                    (gridBinding as GridCol41Binding).apply {
                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                    //        setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                    //        setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv3.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                    //        setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv4.apply {
                              setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                    //          setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                    }
                }// end Grid_col1
                SFData.GRID_3200 -> {
                    (gridBinding as GridCol3200Binding).apply {

                        lin.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, R.color.BlueGray200) )
                        }
                        tv1.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv2.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv3.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        tv4.apply {
                            setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                            setTextColor(ContextCompat.getColor(root.context,colorString))
                        }
                        //   tv4.apply {
                        //       setBackgroundColor(ContextCompat.getColor(root.context, colorRecString) )
                        //       setTextColor(ContextCompat.getColor(root.context,colorString))
                        //   }
                    }
                }// end Grid_col1

            }// when

        }//end setColor Function
    }
    private var itemList : ArrayList<Any> = ArrayList<Any>()
    private var sfDataList : SFDataList = SFDataList(SFData.GRID_COL1)
    private var sumList: ArrayList<Int> = ArrayList<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            SFData.TEXT_MSG ->SFDataViewHolder(  parent
                , viewType
                , TextMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_COL ->SFDataViewHolder(  parent
                , viewType
                , GridColBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_EMPTY_LOC ->SFDataViewHolder(  parent
                , viewType
                , GridLoccolBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_COLSEL ->SFDataViewHolder(  parent
                , viewType
                , GridColSelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_PLTBAR ->SFDataViewHolder(  parent
                , viewType
                , GridColOubPltBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )

            SFData.GRID_LOCSEL ->SFDataViewHolder(  parent
                , viewType
                , GridCol3Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_2000 ->SFDataViewHolder(  parent
                , viewType
                , GridCol2000Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_2100 ->SFDataViewHolder(  parent
                , viewType
                , GridCol2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_2200 ->SFDataViewHolder(  parent
                , viewType
                , GridCol2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_2101 ->SFDataViewHolder(  parent
                , viewType
                , GridCol5Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_2201 ->SFDataViewHolder(  parent
                , viewType
                , GridCol5Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_2102 ->SFDataViewHolder(  parent
                , viewType
                , GridCol4Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_2202 ->SFDataViewHolder(  parent
                , viewType
                , GridCol41Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            SFData.GRID_3200 ->SFDataViewHolder(  parent
                , viewType
                , GridCol3200Binding.inflate(LayoutInflater.from(parent.context), parent, false)
                , onClickInterface
                , onLongClickInterface
            )
            else -> throw IllegalArgumentException("Invalid ViewType")

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //  var sfData = sfDataList.getRecord(sfDataList.type, position)
        //  (holder as SFDataViewHolder).bind(sfData as SFData)
        //  (holder as SFDataViewHolder).bind(itemList[position] as SFData)
        //when(holder){
        //    is SFDataViewHolder->holder.bind(itemList[position] as SFData)
        // }
        (holder as SFDataViewHolder).apply{
            val sfData= (itemList[position] as SFData)
            bind(sfData)
            if (sfData.type == SFData.GRID_COL1T){
                setColor(R.color.grid_title_text, R.color.grid_title_back)
            }
            else if (sfData.type == SFData.GRID_3200){
                if (sfData.IsSelected == true)
                    setColor(R.color.grid_rec_select_text, R.color.grid_rec_select_back)
                else
                    setColor(R.color.grid_rec_text, R.color.grid_rec_back)
                if (sfData.getFieldString("pk").equals("1")){
                    setColor(R.color.grid_title_text, R.color.grid_title_back)
                }
            }
            else {
                if (sfData.IsSelected == true)
                    setColor(R.color.grid_rec_select_text, R.color.grid_rec_select_back)
                else
                    setColor(R.color.grid_rec_text, R.color.grid_rec_back)
            }
            itemView.setOnClickListener(View.OnClickListener {
                IsSelectedClear()
                (itemList[position] as SFData).IsSelected = true
                notifyDataSetChanged()
                selectedPosition = position
                onClickInterface.onClick(sfData, position)
                //실행하고자 하는 코드 입력
            })

            //LongClick Listener
            itemView.setOnLongClickListener(View.OnLongClickListener {
                IsSelectedClear()
                (itemList[position] as SFData).IsSelected = true
                notifyDataSetChanged()
                selectedPosition = position
                onLongClickInterface.onLongClick(sfData, position)
                //실행하고자 하는 코드 입력
                return@OnLongClickListener true
            })

        }
    }

    override fun getItemCount(): Int = itemList.size
    override fun getItemViewType(position : Int) = (itemList[position] as SFData).type

    // override fun getItemCount(): Int = sfDataList.getSize() //itemList.size
    // override fun getItemViewType(position : Int) = sfDataList.type  //(itemList[position] as SFData).type

    fun updateList(updateList: List<Any>){
        itemList.clear()
        itemList.addAll(updateList)
        notifyDataSetChanged()
    }
    fun updateList(updateList: SFDataList){
        sfDataList = updateList;
        notifyDataSetChanged()
    }
    fun remove(position: Int) {
        try {
            itemList.removeAt(position)
            notifyItemRemoved(position)
        } catch (ex: IndexOutOfBoundsException) {
            ex.printStackTrace()
        }
    }

    fun FieldSum(field_name: String):Int{
        var totsum = 0
        for (i in itemList.indices) {
            val jd = itemList.get(i) as SFData
            totsum += jd.getFieldInteger(field_name)
        }
        return totsum
    }
    fun FieldSum(field_name1: String,field_name2: String):ArrayList<Int>{
        var totsum = ArrayList<Int>()
        while(totsum.size < 3 ) totsum.add(0)
        for (i in itemList.indices) {
            val jd = itemList.get(i) as SFData

            totsum[0] += jd.getFieldInteger(field_name1)
            totsum[1] += jd.getFieldInteger(field_name2)
        }
        return totsum // as ArrayList<Int>
    }

    fun FieldSum(field_name1: String,field_name2: String, field_name3: String, field_name4: String, field_name5: String):ArrayList<Int>{
        var totsum = ArrayList<Int>()
        while(totsum.size < 6 ) totsum.add(0)
        for (i in itemList.indices) {
            val jd = itemList.get(i) as SFData

            totsum[0] += jd.getFieldInteger(field_name1)
            totsum[1] += jd.getFieldInteger(field_name2)
            totsum[2] += jd.getFieldInteger(field_name3)
            totsum[3] += jd.getFieldInteger(field_name4)
            totsum[4] += jd.getFieldInteger(field_name5)
        }
        return totsum // as ArrayList<Int>
    }


    fun FindRow(field_name: String, find_data: String): Int {
        IsSelectedClear()
        for (i in itemList.indices) {
            val jd = itemList.get(i) as SFData
            if (jd.getFieldString(field_name).equals(find_data)) {
                jd.IsSelected = true
                selectedPosition = i
                notifyDataSetChanged()
                return i
            }
        }
        notifyDataSetChanged()
        return -1
    }

    fun FindRow(
        field_name1: String,
        find_data1: String,
        field_name2: String,
        find_data2: String
    ): Int {
        IsSelectedClear()
        for (i in itemList.indices) {
            val jd = itemList.get(i) as SFData
            if (jd.getFieldString(field_name1).equals(find_data1) && jd.getFieldString(field_name2)
                    .equals(find_data2)
            ) {
                jd.IsSelected = true
                selectedPosition = i
                notifyDataSetChanged()
                return i
            }
        }
        return -1
    }

    fun FindRow(
        field_name1: String,
        find_data1: String,
        field_name2: String,
        find_data2: String,
        field_name3: String,
        find_data3: String
    ): Int {
        IsSelectedClear()
        for (i in itemList.indices) {
            val jd = itemList.get(i) as SFData
            val str = String.format("FindRow = %d  ", i)
            if (jd.getFieldString(field_name1).equals(find_data1) &&
                jd.getFieldString(field_name2).equals(find_data2) &&
                jd.getFieldString(field_name3).equals(find_data3)
            ) {
                jd.IsSelected = true
                selectedPosition = i
                notifyDataSetChanged()
                return i
            }
        }
        return -1
    }

    fun IsSelectedClear() {
        for (i in itemList.indices) {
            (itemList.get(i) as SFData).IsSelected = false
        }
        // notifyDataSetChanged()
    }
    fun IsSelected(position:Int){
        IsSelectedClear()
        (itemList.get(position) as SFData).IsSelected = true
        notifyDataSetChanged()
    }

    fun Sum(field:String):Int{
        var tot = 0
        for (i in itemList.indices) {
            var num = (itemList.get(i) as SFData).getFieldInteger(field)
            tot += num
        }
        return  tot
    }

    fun getCheckCount(): Int {
        var count = 0
        for (i in itemList.indices) {
            if ((itemList.get(i) as SFData).IsChecked === true) count++
        }
        return count
    }

    fun setField(field_name: String, field_value: String) {
        val count = 0
        for (i in itemList.indices) {
            (itemList.get(i) as SFData).setField(field_name, field_value)
        }
        return
    }

    fun setField(field_name1: String, val1: String, field_name2: String, val2: String) {
        val count = 0
        for (i in itemList.indices) {
            (itemList.get(i) as SFData).setField(field_name1, val1)
            (itemList.get(i) as SFData).setField(field_name2, val2)
        }
        return
    }

    fun setAllCheck(bchk: Boolean) {
        for (i in itemList.indices) {
            (itemList.get(i) as SFData).IsChecked = bchk
            val jdata: SFData = (itemList.get(i) as SFData)
            itemList.set(i, jdata)
        }
        // notifyDataSetChanged();
    }

    fun IsCheckCompleted(): Boolean? {
        var count = 0
        for (i in itemList.indices) {
            if ((itemList.get(i) as SFData).IsChecked === true) count++
        }
        val total: Int = itemList.size
        return if (total == count) true else false
    }


}

fun interface SFRvJsonAdapterListener {
    fun onClickItem(sfData: SFData)
//    fun onLongClickItem(position:Int)
}
interface OnClickInterface {
    fun onClick(sfData: SFData, position:Int)
//    fun onLongClickItem(position:Int)
}
interface OnLongClickInterface {
    fun onLongClick(sfData: SFData, position:Int)
//    fun onLongClickItem(position:Int)
}
