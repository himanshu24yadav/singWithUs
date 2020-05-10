package com.example.singmetoo.appSingMe2.mHome.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.appSingMe2.mHome.adapter.HomeAdapter
import com.example.singmetoo.appSingMe2.mHome.interfaces.HomeItemsInterface
import com.example.singmetoo.appSingMe2.mHome.pojo.HomeContentModel
import com.example.singmetoo.appSingMe2.mUtils.*
import com.example.singmetoo.databinding.LayoutHomeFragmentBinding

class HomeFragment : BaseFragment(),HomeItemsInterface {

    private lateinit var mLayoutBinding: LayoutHomeFragmentBinding
    private var mContext:Context? = null
    private var mLayoutManager: GridLayoutManager? = null
    private var mHomeAdapter: HomeAdapter? = null
    private val itemSpanCount:Int = 2

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
        initObject()
        initToolbar()
        initView()
    }

    private fun initObject() {
        mLayoutManager = GridLayoutManager(mContext,itemSpanCount)
    }

    private fun initView() {
        mLayoutBinding.userInfo = mUserInfo
        mLayoutBinding.profilePhotoUrl = "${mUserInfo.userName?.get(0)}"
        mLayoutBinding.hasUserProfilePhoto = AppUtil.checkIsNotNull(mUserInfo.userProfilePicUrl)
        mLayoutBinding.homeRV.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
            mContext?.let {
                addItemDecoration(SpaceItemDecoration(it.fetchDimen(R.dimen.item_spacing_top),it.fetchDimen(R.dimen.item_spacing_left),itemSpanCount))
            }
        }
        setHomeAdapter()
    }

    private fun initToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(mLayoutBinding.homeFragToolbar)
        setDefaultToolbar(true)
        mLayoutBinding.homeFragToolbar.setNavigationOnClickListener(navigationDrawerListener())
    }

    private fun setHomeAdapter() {
        if(mHomeAdapter==null){
            mLayoutBinding.homeRV.adapter = HomeAdapter(mContext,AppUtil.getHomeItems(mContext),this)
        } else {
            mHomeAdapter!!.updateData(AppUtil.getHomeItems(mContext))
            mHomeAdapter!!.notifyDataSetChanged()
        }
    }

    fun onBackPressed() {
        AppUtil.showToast(mContext,"HomeFrag")
    }

    override fun openSelectedScreen(view: View?,itemModel: HomeContentModel?) {
        itemModel?.let {
            when(itemModel.title) {
                mContext?.fetchString(R.string.home_item_music_title) -> { NavigationHelper.openYourMusicFragment(getActivityFragmentManager()) }
                mContext?.fetchString(R.string.home_item_playlists_title) -> { NavigationHelper.openPlaylistFragment() }
                mContext?.fetchString(R.string.home_item_radio_title) -> { NavigationHelper.openRadioFragment() }
                mContext?.fetchString(R.string.home_item_recents_title) -> { NavigationHelper.openRecentFragment() }
                else -> { AppUtil.showToast(mContext,"Else") }
            }
        }
    }
}