package com.sf.cwms.base

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import java.util.*
import com.sf.cwms.util.UserLog

//abstract class BaseRecyclerAdapter <ITEM : Any, VB : ViewBinding>(
//    @LayoutRes private val layoutResId: Int
//) : RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<ITEM, VB>>(),
//    ItemMoveCallbackListener.Listener {
abstract class BaseRecyclerAdapter <ITEM : Any, VB : ViewBinding>(
    @LayoutRes private val layoutResId: Int
) : RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<ITEM, VB>>(){

    private val items = mutableListOf<ITEM>()

    //abstract fun onCreateViewBinding(itemView: View): VB
    // abstract fun onBindView(item: ITEM, binding: VB, adapterPosition: Int)

    override fun onBindViewHolder(holder: BaseRecyclerAdapter.BaseViewHolder<ITEM, VB>, position: Int) {
        holder.onBindView(items[position])
    }
    /*
    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

     */
    //override fun onRowSelected(itemViewHolder: BaseViewBindingViewHolder) {
    // }

    //override fun onRowClear(itemViewHolder: BaseViewBindingViewHolder) {
    //}
    /*   override fun onCreateViewHolder(
           parent: ViewGroup,
           viewType: Int
       ): BaseViewBindingViewHolder<ITEM, VB> {
           return object : BaseViewBindingViewHolder<ITEM, VB>(parent, viewType) {

               override val viewBinding: VB = onCreateViewBinding(itemView)

               override fun onBindView(item: ITEM) =
                   onBindView(item, viewBinding, adapterPosition)
           }
       }
   */

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return layoutResId
    }

    open fun replaceAll(items: List<ITEM>?) {
        this.items.run {
            clear()
            items?.let {
                addAll(it)
            }
        }
        notifyDataSetChanged()
    }
    // position 위치의 데이터를 삭제 후 어댑터 갱신
    open fun removeData(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun removeItem(position: Int): Any? {
        var item: ITEM? = null
        try {
            item = items.get(position)
            items.removeAt(position)
            notifyItemRemoved(position)
        } catch (e: Exception) {
            UserLog.userLog(e.message!!)
        }
        return item
    }

    open fun addItem(position: Int, item: ITEM?) {
        try {
            items.add(position, item!!)
            notifyItemInserted(position)
        } catch (e: java.lang.Exception) {
            Log.e("MainActivity", e.message!!)
        }
    }

    // 현재 선택된 데이터와 드래그한 위치에 있는 데이터를 교환
    open fun swapData(fromPos: Int, toPos: Int) {
        Collections.swap(items, fromPos, toPos)
        notifyItemMoved(fromPos, toPos)
    }


    abstract class BaseViewHolder<ITEM : Any, VB : ViewBinding>(
        parent: ViewGroup,
        @LayoutRes
        layoutRes: Int
    ) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        ) {

        abstract val viewBinding: VB
        abstract fun onBindView(item: ITEM)
    }


}