package com.example.singmetoo.appSingMe2.mHome

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.appSingMe2.mUtils.AppUtil
import com.example.singmetoo.appSingMe2.mUtils.setProfileName
import com.example.singmetoo.databinding.LayoutHomeFragmentBinding

class HomeFragment : BaseFragment() {

    private lateinit var mLayoutBinding: LayoutHomeFragmentBinding
    private var mContext:Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        mLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.layout_home_fragment,container,false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initToolbar()
        initView()
    }

    private fun initView() {
        mLayoutBinding.userInfo = mUserInfo
        mLayoutBinding.button.setOnClickListener {
            mContext?.let { AppUtil.showToast(it,mUserInfo.userName!!) }
        }
    }

    private fun initToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(mLayoutBinding.homeFragToolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = ""
        mLayoutBinding.toolbarTitle.setProfileName(mUserInfo.userName)
        setToolbarProfileImage()
    }

    private fun setToolbarProfileImage() {
        if(!AppUtil.checkIsNotNull(mUserInfo.userProfilePicUrl)){
            mLayoutBinding.toolbarNonProfileImgRl.visibility = View.VISIBLE
            mLayoutBinding.toolbarProfileImg.visibility = View.GONE
            mLayoutBinding.toolbarNonProfileImg.text = "${mUserInfo.userName?.get(0)}"
        } else {
            mLayoutBinding.toolbarNonProfileImgRl.visibility = View.GONE
            mLayoutBinding.toolbarProfileImg.visibility = View.VISIBLE
        }
    }

    fun onBackPressed() {
        mContext?.let { AppUtil.showToast(it,"HomeFrag") }
    }
}