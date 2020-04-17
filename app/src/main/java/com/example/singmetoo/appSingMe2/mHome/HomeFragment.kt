package com.example.singmetoo.appSingMe2.mHome

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.appSingMe2.mUtils.AppUtil
import com.example.singmetoo.databinding.LayoutHomeFragmentBinding

class HomeFragment : BaseFragment() {

    private lateinit var mLayoutBinding: LayoutHomeFragmentBinding
    private var mContext:Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.layout_home_fragment,container,false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

    }

    fun onBackPressed() {
        mContext?.let { AppUtil.showToast(it,"HomeFrag") }
    }
}