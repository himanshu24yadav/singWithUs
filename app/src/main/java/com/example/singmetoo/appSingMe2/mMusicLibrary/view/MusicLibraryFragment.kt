package com.example.singmetoo.appSingMe2.mMusicLibrary.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.appSingMe2.mBase.view.MainActivity
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppUtil
import com.example.singmetoo.appSingMe2.mUtils.helpers.NavigationHelper
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongsViewModel
import com.example.singmetoo.databinding.LayoutMusicLibraryFragmentBinding

class MusicLibraryFragment : BaseFragment() {

    private var mContext:Context? =null
    private lateinit var mLayoutBinding: LayoutMusicLibraryFragmentBinding
    private var mSongsListFromDevice: ArrayList<SongModel>? = null
    private var mSongViewModel: SongsViewModel? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        mLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_music_library_fragment,container,false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initObject()
        initObservers()
        initToolbar()
        initViews()
        initListeners()
    }

    private fun initViews() {
        mLayoutBinding.toolbarTitle.isSelected = true
        mLayoutBinding.toolbarSubtitle.isSelected = true
    }

    private fun initObservers() {
        mSongViewModel?.getSongsLiveData()?.observe(this, Observer {
            AppUtil.showToast(mContext,"song = ${it[0].songTitle}")
        })
    }

    private fun initObject() {
        mSongViewModel = ViewModelProviders.of((mContext as MainActivity)).get(SongsViewModel::class.java)
    }

    private fun initListeners() {
        mLayoutBinding.toolbarHomeIcon.setOnClickListener {
            NavigationHelper.openHomeFragment((mContext as? MainActivity)?.supportFragmentManager)
        }
    }

    private fun initToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(mLayoutBinding.musicLibFragToolbar)
        setDefaultToolbar(true)
        mLayoutBinding.musicLibFragToolbar.setNavigationOnClickListener(navigationDrawerListener())
    }
}