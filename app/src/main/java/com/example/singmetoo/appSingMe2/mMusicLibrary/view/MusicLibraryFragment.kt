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
import com.example.singmetoo.appSingMe2.mMusicLibrary.adapter.MusicLibraryAdapter
import com.example.singmetoo.appSingMe2.mMusicLibrary.interfaces.MusicLibraryAdapterCallback
import com.example.singmetoo.appSingMe2.mUtils.helpers.*
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongsViewModel
import com.example.singmetoo.audioPlayerHelper.PlayerStatus
import com.example.singmetoo.databinding.LayoutMusicLibraryFragmentBinding


class MusicLibraryFragment : BaseFragment(),MusicLibraryAdapterCallback{

    private var mContext:Context? =null
    private lateinit var mLayoutBinding: LayoutMusicLibraryFragmentBinding
    private var mSongsListFromDevice: ArrayList<SongModel>? = null
    private var mSongViewModel: SongsViewModel? = null
    private var mSongsAdapter: MusicLibraryAdapter? = null
    private var mPlayingSongModel: SongModel? = null
    private var mLinearLayoutManager: RecyclerView.LayoutManager? = null
    private var mOldPlayingSongId : Long? = -1
    private var mNewPlayingSongId : Long? = -1

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

    override fun onResume() {
        super.onResume()
        commonBaseInterface?.hideBottomAudioPlayer()
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

        commonBaseInterface?.playerStatusLiveData?.observe(this, Observer { playerStatus ->
            handlePlayerStatusFromService(playerStatus)
        })
    }

    private fun handlePlayerStatusFromService(playerStatus: PlayerStatus?) {
        val playingSongModel = AppUtil.getPlayingSongFromList(mSongsListFromDevice,playerStatus?.songId?.toLong())
        playingSongModel?.let {
            if(it.songId != mPlayingSongModel?.songId) {
                updateSelectedSongForPlaying(it)
            }

            when(playerStatus) {
                is PlayerStatus.Playing -> {
                    updateAudioPlayerHeader(false)
                }
                is PlayerStatus.Paused -> {
                    updateAudioPlayerHeader(true)
                }
                is PlayerStatus.Cancelled -> {
                    updateAudioPlayerHeader(true)
                }
                is PlayerStatus.Error -> {
                    updateAudioPlayerHeader(true)
                }
                is PlayerStatus.Idle -> {
                    updateAudioPlayerHeader(true)
                }
            }
        }
    }

    private fun updateView() {
        mSongsListFromDevice?.let { list ->
            if(list.size > 0) {
                mPlayingSongModel = AppUtil.getPlayingSongFromList(list)
                mOldPlayingSongId = mPlayingSongModel?.songId
                mNewPlayingSongId = mOldPlayingSongId
                updateAudioPlayerHeader(true)
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
            mSongsAdapter = MusicLibraryAdapter(mContext,mSongsListFromDevice,this,selectedSongIndex = mSongsListFromDevice?.indexOf(mPlayingSongModel)!!)
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

    private fun updateAudioPlayerHeader(songPaused: Boolean) {
        mLayoutBinding.toolbarTitle.text = mPlayingSongModel?.songTitle
        mLayoutBinding.toolbarSubtitle.text = mPlayingSongModel?.songArtist
        mLayoutBinding.playingSongControlsLayout.playIv.togglePlayIcon(songPaused)
        mLayoutBinding.defaultPlayingSongIv.setAlbumImage(AppUtil.getImageUriFromAlbum(mPlayingSongModel?.songAlbumId))
        mLayoutBinding.toolbarTitle.isSelected = true
        mLayoutBinding.toolbarSubtitle.isSelected = true
    }

    override fun onStop() {
        super.onStop()
        mSongViewModel?.updateCurrentlyPlayingSongFromDevice(mOldPlayingSongId,mNewPlayingSongId)
    }

    override fun updateSelectedSongForPlaying(newSelectedSongForPlaying: SongModel?) {
        val oldSelectedSongIndex:Int = mSongsListFromDevice?.indexOf(mPlayingSongModel)!!
        val newSelectedSongIndex:Int= mSongsListFromDevice?.indexOf(newSelectedSongForPlaying)!!
        if(oldSelectedSongIndex!=-1 && newSelectedSongIndex!=-1) {
            mSongsListFromDevice?.get(oldSelectedSongIndex)?.songCurrentlyPlaying = false
            mSongsListFromDevice?.get(newSelectedSongIndex)?.songCurrentlyPlaying = true
        }
        mNewPlayingSongId = newSelectedSongForPlaying?.songId
        mPlayingSongModel = newSelectedSongForPlaying
    }

    override fun toggleAudioPlayer(newSelectedSongForPlaying: SongModel?, toPauseSong: Boolean) {
        mLayoutBinding.toolbarTitle.isSelected = false
        mLayoutBinding.toolbarSubtitle.isSelected = false
        if(toPauseSong) {
            commonBaseInterface?.pauseAudio()
        } else {
            commonBaseInterface?.playAudio(newSelectedSongForPlaying, false)
        }
    }
}