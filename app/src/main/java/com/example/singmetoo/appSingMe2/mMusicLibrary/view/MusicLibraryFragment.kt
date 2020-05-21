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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.appSingMe2.mBase.view.MainActivity
import com.example.singmetoo.appSingMe2.mMusicLibrary.interfaces.MusicLibraryAdapterCallback
import com.example.singmetoo.appSingMe2.mMusicLibrary.adapter.MusicLibraryAdapter
import com.example.singmetoo.appSingMe2.mUtils.helpers.NavigationHelper
import com.example.singmetoo.appSingMe2.mUtils.helpers.SpaceItemDecoration
import com.example.singmetoo.appSingMe2.mUtils.helpers.fetchDimen
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongsViewModel
import com.example.singmetoo.databinding.LayoutMusicLibraryFragmentBinding

class MusicLibraryFragment : BaseFragment(),MusicLibraryAdapterCallback{

    private var mContext:Context? =null
    private lateinit var mLayoutBinding: LayoutMusicLibraryFragmentBinding
    private var mSongsListFromDevice: ArrayList<SongModel>? = null
    private var mSongViewModel: SongsViewModel? = null
    private var mSongsAdapter: MusicLibraryAdapter? = null
    private var mSelectedSongModel: SongModel? = null
    private var mLinearLayoutManager: RecyclerView.LayoutManager? = null

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
        initView()
        initObservers()
        initToolbar()
        initListeners()
    }

    private fun initView() {
        mLayoutBinding.songsRv.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayoutManager
            mContext?.let {
                addItemDecoration(SpaceItemDecoration(it.fetchDimen(R.dimen.song_item_spacing_top),0,1))
            }
            itemAnimator = null
        }
    }

    private fun initObservers() {
        mSongViewModel?.getSongsLiveData()?.observe(this, Observer {
            mSongsListFromDevice?.clear()
            mSongsListFromDevice?.addAll(it)
            updateView()
        })
    }

    private fun updateView() {
        mSongsListFromDevice?.let { list ->
            if(list.size > 0) {
                list[0].songCurrentlyPlaying = true
                mSelectedSongModel = list[0]
                setTitleForSongPlaying(list[0])
                mLayoutBinding.playingSongGroup.visibility = View.VISIBLE
                mLayoutBinding.songsRv.visibility = View.VISIBLE
                setMusicLibraryAdapter()
            } else {
                mLayoutBinding.songsRv.visibility = View.GONE
                mLayoutBinding.playingSongGroup.visibility = View.GONE
            }
        }
    }

    private fun setMusicLibraryAdapter() {
        if(mSongsAdapter!=null) {
            mSongsAdapter!!.updateData(mSongsListFromDevice)
            mSongsAdapter!!.notifyDataSetChanged()
        } else {
            mSongsAdapter = MusicLibraryAdapter(mContext,mSongsListFromDevice,this)
            mLayoutBinding.songsRv.adapter = mSongsAdapter
        }
    }

    private fun initObject() {
        mSongsListFromDevice = ArrayList()
        mLinearLayoutManager = LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false)
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

    private fun setTitleForSongPlaying(songModel:SongModel?) {
        mLayoutBinding.toolbarTitle.isSelected = true
        mLayoutBinding.toolbarSubtitle.isSelected = true
        mLayoutBinding.toolbarTitle.text = songModel?.songTitle
        mLayoutBinding.toolbarSubtitle.text = songModel?.songArtist
    }

    override fun selectedSongForPlaying(newSelectedSongForPlaying: SongModel?) {
        setTitleForSongPlaying(newSelectedSongForPlaying)

        val oldSelectedSong:Int = mSongsListFromDevice?.indexOf(mSelectedSongModel)!!
        val newSelectedSong:Int= mSongsListFromDevice?.indexOf(newSelectedSongForPlaying)!!
        if(oldSelectedSong!=-1 && newSelectedSong!=-1) {
            mSongsListFromDevice?.get(oldSelectedSong)?.songCurrentlyPlaying = false
            mSongsListFromDevice?.get(newSelectedSong)?.songCurrentlyPlaying = true
        }
        mSelectedSongModel = newSelectedSongForPlaying
    }
}