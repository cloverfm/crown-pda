package com.sf.cwms.util

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding

interface ActivityBindingLayout {
    @LayoutRes
    fun getLayout(): Int

    fun setBinding(binding: ViewDataBinding?)
}