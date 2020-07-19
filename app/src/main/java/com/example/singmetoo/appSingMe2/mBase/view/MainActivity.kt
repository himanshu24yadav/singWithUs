package com.example.singmetoo.appSingMe2.mBase.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.interfaces.CommonBaseInterface
import com.example.singmetoo.appSingMe2.mBase.interfaces.NavigationDrawerInterface
import com.example.singmetoo.appSingMe2.mBase.util.BaseActivity
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.appSingMe2.mBase.util.DrawerManager
import com.example.singmetoo.appSingMe2.mHome.view.HomeFragment
import com.example.singmetoo.appSingMe2.mLogin.AppUserInfo
import com.example.singmetoo.appSingMe2.mUtils.helpers.*
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongsViewModel
import com.example.singmetoo.audioPlayerHelper.AudioPlayService
import com.example.singmetoo.audioPlayerHelper.PlayerStatus
import com.example.singmetoo.databinding.ActivityMainBinding
import com.example.singmetoo.databinding.BottomAudioPlayerBinding
import com.example.singmetoo.databinding.NavHeaderMainBinding
import com.example.singmetoo.permissionHelper.PermissionModel
import com.example.singmetoo.permissionHelper.PermissionsManager
import com.example.singmetoo.permissionHelper.PermissionsResultInterface
import com.google.android.exoplayer2.Player
import com.google.android.material.bottomsheet.BottomSheetBehavior

const val TAG = "MAIN_ACTIVITY_TAG"

class MainActivity : BaseActivity(), CommonBaseInterface,NavigationDrawerInterface,PermissionsResultInterface {

    private lateinit var mLayoutBinding: ActivityMainBinding
    private lateinit var mBottomAudioPlayerBinding: BottomAudioPlayerBinding
    private var mNowPlayingView : MainActivityViewHolder? = null
    private var headerLayoutBinding: NavHeaderMainBinding? = null
    private var actionBarDrawerToggle:ActionBarDrawerToggle? = null
    private var drawerManager: DrawerManager? = null
    private var mBottomSheetAudioPlayerBehaviour: BottomSheetBehavior<View>? = null
    private var mPlayerStatusLiveData: LiveData<PlayerStatus>? = null
    private var mPlayingSongLiveData: MutableLiveData<SongModel>? = MutableLiveData()
    private var mSongListFromDeviceLiveData: MutableLiveData<ArrayList<SongModel>>? = MutableLiveData()
    private var mUserInfoLiveData:MutableLiveData<AppUserInfo>? = MutableLiveData()
    private var mSongViewModel : SongsViewModel? = null
    private var mAudioPlayService: AudioPlayService? = null
    private var mSongsListFromDevice: ArrayList<SongModel>? = ArrayList()
    private var mToExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG,"onCreate")
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBottomAudioPlayerBinding = mLayoutBinding.appBarMain.includeAudioPlayerLayout
        mNowPlayingView = MainActivityViewHolder(mBottomAudioPlayerBinding.exoPlayerView.children.elementAt(0))

        init()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.extras?.getBoolean(AppConstants.FROM_MUSIC_NOTIFICATION,false)!!){
            mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun init() {
        initObj()
        initNavBar()
        initListeners()
        initSetUpAudioPlayerBottomSheet()
        initFetchSongsFromDevice()
        initObserver()
        initOpenHomeFragment()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Log.e(TAG,"onPostCreate")
        actionBarDrawerToggle?.syncState()
    }

    override fun onStart() {
        Log.e(TAG,"onStart")
        super.onStart()
        bindToAudioService()
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG,"onStop")
        unbindAudioService()
        mSongViewModel?.updateCurrentlyPlayingSongFromDevice(mPlayingSongLiveData?.value?.songId)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudioService()
    }

    private fun initListeners() {
        mNowPlayingView?.audioPlayerPreviewPlayIv?.setOnClickListener {
            startPlayingSong()
        }

        mNowPlayingView?.nowPlayingHeaderPlaylistIv?.setOnClickListener {
            NavigationHelper.openYourMusicFragment(supportFragmentManager)
        }
    }

    private fun initObserver() {
        mSongViewModel?.getSongsLiveData()?.observe(this@MainActivity, Observer { list ->
            Log.e(TAG,"Observer")
            mSongsListFromDevice?.clear()
            mSongsListFromDevice?.addAll(list)
            mSongListFromDeviceLiveData?.value = mSongsListFromDevice
            mPlayingSongLiveData?.value = AppUtil.getPlayingSongFromList(list)
            mPlayingSongLiveData?.value?.let {
                showBottomAudioPlayer(it)
            }
        })

        userLoggedInLiveData?.observe(this, Observer { success ->
            if(success) {
                updateUserInfoUI()
            }
        })
    }

    private fun initSetUpAudioPlayerBottomSheet() {
        mBottomSheetAudioPlayerBehaviour = BottomSheetBehavior.from(mBottomAudioPlayerBinding.audioPlayerMainCl)
        mBottomSheetAudioPlayerBehaviour?.peekHeight = this.fetchDimen(R.dimen.audio_player_preview_height)
        mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetAudioPlayerBehaviour?.isHideable = false
        addBottomSheetCallbacks()
        hideBottomAudioPlayer()
    }

    private fun addBottomSheetCallbacks() {
        mBottomSheetAudioPlayerBehaviour?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                animateBottomSheet(slideOffset)
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_DRAGGING-> {
                        if(!AudioPlayService.isAudioPlayServiceRunning){
                            mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                    else -> {
                    }
                }
            }

        })
    }

    private fun animateBottomSheet(slideOffset:Float) {
        if(mBottomSheetAudioPlayerBehaviour == null) return

        when(slideOffset){
            0f -> {
                mNowPlayingView?.audioPlayerPreviewCl?.isClickable = true
                mNowPlayingView?.nowPlayingHeaderCl?.isClickable = false
            }
            1f -> {
                mNowPlayingView?.audioPlayerPreviewCl?.isClickable = false
                mNowPlayingView?.nowPlayingHeaderCl?.isClickable = true
            }
        }

        mNowPlayingView?.nowPlayingHeaderCl?.alpha = slideOffset
        mNowPlayingView?.nowPlayingHeaderCl?.alpha = slideOffset
        mNowPlayingView?.nowPlayingBodyCl?.alpha = slideOffset
        mNowPlayingView?.audioPlayerPreviewCl?.alpha = 1 - slideOffset
    }

    private fun initObj() {
        drawerManager = DrawerManager(this, mLayoutBinding.drawerLayout,this)
        mSongViewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        headerLayoutBinding = NavHeaderMainBinding.bind(mLayoutBinding.navView.getHeaderView(0))
    }

    private fun initNavBar() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this, mLayoutBinding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_open)
        actionBarDrawerToggle?.let { mLayoutBinding.drawerLayout.addDrawerListener(it) }
        mLayoutBinding.navView.setNavigationItemSelectedListener(drawerManager)
        setNavHeaderView()

        //to show hamburger menu on each fragment
        supportFragmentManager.addOnBackStackChangedListener {
            if(supportFragmentManager.backStackEntryCount > 0) {
                actionBarDrawerToggle?.syncState()
            }
        }
    }

    private fun initFetchSongsFromDevice() {
        if(PermissionsManager.checkPermissions(this, arrayOf(AppConstants.PERMISSION_WRITE_STORAGE),permissionCallback = this)) {
            mSongViewModel?.fetchAllSongsFromDevice()
        }
    }

    private fun initOpenHomeFragment() {
        supportFragmentManager.addFragment(fragment = HomeFragment(),container = R.id.main_activity_container,fragmentTag = AppConstants.HOME_FRAGMENT_TAG)
    }

    private fun setNavHeaderView() {
        //set profile name using extension method (setProfileName)
        headerLayoutBinding?.navProfileName?.setProfileName(mUserInfo.userName)

        //setting binding variables
        headerLayoutBinding?.profilePhotoUrl = "${mUserInfo.userName?.get(0)}"
        headerLayoutBinding?.hasUserProfilePhoto = AppUtil.checkIsNotNull(mUserInfo.userProfilePicUrl)
        headerLayoutBinding?.navBarCallback = this
        headerLayoutBinding?.userInfo = mUserInfo
        mUserInfoLiveData?.value = mUserInfo
        if(mUserInfo.isUserLoggedIn) {
            mLayoutBinding.navView.menu.findItem(R.id.nav_item_logout).title = this.fetchString(R.string.menu_item_logout)
            mLayoutBinding.navView.menu.findItem(R.id.nav_item_logout).icon = ContextCompat.getDrawable(this, R.drawable.ic_menu_logout)
        } else {
            mLayoutBinding.navView.menu.findItem(R.id.nav_item_logout).title = this.fetchString(R.string.menu_item_login)
            mLayoutBinding.navView.menu.findItem(R.id.nav_item_logout).icon = ContextCompat.getDrawable(this, R.drawable.ic_menu_login)
        }
    }

    private fun updateAudioPlayerDetails(songToPlay: SongModel?, songPaused: Boolean) {
        songToPlay?.let {
            mNowPlayingView?.audioPlayerPreviewTitleTv?.text = it.songTitle ?: AppConstants.DEFAULT_TITLE
            mNowPlayingView?.audioPlayerPreviewArtistTv?.text = it.songArtist ?: AppConstants.DEFAULT_ARTIST
            mNowPlayingView?.nowPlayingSongTitle?.text = it.songTitle ?: AppConstants.DEFAULT_TITLE
            mNowPlayingView?.nowPlayingSongSubtitle?.text = it.songArtist ?: AppConstants.DEFAULT_ARTIST
            mNowPlayingView?.audioPlayerPreviewTitleTv?.isSelected = true
            mNowPlayingView?.audioPlayerPreviewArtistTv?.isSelected = true
            mNowPlayingView?.nowPlayingSongTitle?.isSelected = true
            mNowPlayingView?.nowPlayingSongSubtitle?.isSelected = true
            mNowPlayingView?.audioPlayerPreviewPlayIv?.togglePlayIcon(songPaused)
            mNowPlayingView?.audioPlayerPreviewIv?.setAlbumImage(AppUtil.getImageUriFromAlbum(it.songAlbumId))
            mNowPlayingView?.nowPlayingSongIv?.setAlbumImage(AppUtil.getImageUriFromAlbum(it.songAlbumId))
            mNowPlayingView?.nowPlayingSongBgIv?.setAlbumImage(AppUtil.getImageUriFromAlbum(it.songAlbumId))
        }
    }

    //audio service connection
    private val audioServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG,"onServiceConnected")
            val binder = service as AudioPlayService.PlayMusicServiceBinder
            mAudioPlayService = binder.playMusicService

            // Attach the ExoPlayer to the PlayerView.
            mBottomAudioPlayerBinding.exoPlayerView.player = binder.exoPlayer
            mPlayerStatusLiveData = mAudioPlayService?.playerStatusLiveData

            mPlayerStatusLiveData?.observe(this@MainActivity, Observer {
                handlePlayerStatusChangeFromService(it)
            })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mAudioPlayService = null
        }
    }

    private fun handlePlayerStatusChangeFromService(playerStatus: PlayerStatus) {
        val songModel = AppUtil.getPlayingSongFromList(mSongsListFromDevice,songId = playerStatus.songId?.toLong())
        mPlayingSongLiveData?.value = songModel
        when(playerStatus) {
            is PlayerStatus.Playing -> {
                songModel?.let { updateAudioPlayerDetails(it,false) }
            }
            is PlayerStatus.Ended -> {
                songModel?.let { updateAudioPlayerDetails(it,true) }
            }
            is PlayerStatus.Cancelled -> {
                songModel?.let { updateAudioPlayerDetails(it,true) }
            }
            is PlayerStatus.Paused -> {
                songModel?.let { updateAudioPlayerDetails(it,true) }
            }
            is PlayerStatus.Error -> {
                songModel?.let { updateAudioPlayerDetails(it,true) }
            }
        }
    }

    private fun bindToAudioService() {
        if (mAudioPlayService == null) {
            AudioPlayService.newIntent(this).also { intent ->
                bindService(intent, audioServiceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun unbindAudioService() {
        if (mAudioPlayService != null) {
            unbindService(audioServiceConnection)
            mAudioPlayService = null
        }
    }

    private fun startPlayingSong() {
        if(mNowPlayingView?.audioPlayerPreviewPlayIv?.tag == AppConstants.SONG_TAG_PLAY) {
            when {
                AppUtil.toResumePlayingSong(mPlayingSongLiveData?.value?.songId,mAudioPlayService?.mCurrentlyPlayingSongId,mBottomAudioPlayerBinding.exoPlayerView.player) -> {
                    mAudioPlayService?.resume()
                }
                AudioPlayService.isAudioPlayServiceRunning -> {
                    mAudioPlayService?.play(mPlayingSongLiveData?.value?.songId)
                }
                else -> {
                    AudioPlayService.newIntent(this, mPlayingSongLiveData?.value).also { intent ->
                        startService(intent)
                    }
                }
            }
        } else {
            mAudioPlayService?.pause()
        }
    }

    private fun stopAudioService() {
        mAudioPlayService?.pause()
        unbindAudioService()
        stopService(Intent(this, AudioPlayService::class.java))
        mAudioPlayService = null
    }

    private fun updateUserInfoUI() {
        mUserInfo.apply {
            userName = currentUser?.displayName
            userEmail = currentUser?.email
            userId = currentUser?.uid
            isUserLoggedIn = true
        }
        setNavHeaderView()
        SharedPrefHelper.storeSharedPref(SharedPrefHelper.SF_KEY_SKIP_LOGIN,false)
        closeDrawer()
        AppUtil.showToast(this,"Hi ${mUserInfo.userName}")
    }

    private fun signOutUser() {
        mUserInfo.apply {
            userName = "User"
            userEmail = ""
            userId = ""
            isUserLoggedIn = false
        }
        setNavHeaderView()
        firebaseAuth?.signOut()
        googleSignInClient?.signOut()
        currentUser = null
        SharedPrefHelper.storeSharedPref(SharedPrefHelper.SF_KEY_SKIP_LOGIN,true)
        closeDrawer()
        AppUtil.showToast(this,"signed out successfully")
    }




    //--------------------------------------------------------------------------------------------//


    //overridden methods
    override fun onBackPressed() {
        //if left nav_drawer is open
        if(mLayoutBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mLayoutBinding.drawerLayout.closeDrawers()
            return
        }

        when (supportFragmentManager.findFragmentById(R.id.main_activity_container) as? BaseFragment) {
            is HomeFragment -> {
                when {
                    mBottomSheetAudioPlayerBehaviour?.state == BottomSheetBehavior.STATE_EXPANDED -> {
                        mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    mToExit -> {
                        finish()
                    }
                    else -> {
                        AppUtil.showToast(this,"Press again to exit")
                        mToExit = true
                        Handler().postDelayed({
                            mToExit = false
                        },3000)
                    }
                }
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override val playerStatusLiveData: LiveData<PlayerStatus>?
        get() = mPlayerStatusLiveData

    override val playingSongLiveData: LiveData<SongModel>?
        get() = mPlayingSongLiveData

    override val songListFromDeviceLiveData: LiveData<ArrayList<SongModel>>?
        get() = mSongListFromDeviceLiveData

    override val exoAudioPlayerView: Player?
        get() = mBottomAudioPlayerBinding.exoPlayerView.player

    override val userInfoLiveData: LiveData<AppUserInfo>?
        get() = mUserInfoLiveData

    override fun playAudio(songModel: SongModel?,toShowBottomAudioPlayer: Boolean) {
        songModel?.let {
            updateAudioPlayerDetails(it, true)
            mPlayingSongLiveData?.value = it
            startPlayingSong()
        }
    }

    override fun pauseAudio() {
        mPlayingSongLiveData?.value?.let {
            mAudioPlayService?.pause()
            updateAudioPlayerDetails(mPlayingSongLiveData?.value,true)
        }
    }

    override fun stopAudio() {
    }

    override fun showBottomAudioPlayer(songToPlay:SongModel?) {
        songToPlay?.let {
            mBottomAudioPlayerBinding.exoPlayerView.player?.let { player ->
                updateAudioPlayerDetails(it,!player.isSongPlaying())
                mBottomAudioPlayerBinding.audioPlayerMainCl.visibility = View.VISIBLE
                mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    override fun hideBottomAudioPlayer() {
        mBottomAudioPlayerBinding.audioPlayerMainCl.visibility = View.GONE
        mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun closeDrawer() {
        mLayoutBinding.drawerLayout.closeDrawers()
    }

    override fun openDrawer() {
        mLayoutBinding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun lockDrawer() {
        mLayoutBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun unLockDrawer() {
        mLayoutBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun isDrawerOpen() : Boolean{
        return mLayoutBinding.drawerLayout.isDrawerOpen(GravityCompat.START)
    }

    override fun updatePlayingSong(playingSong: SongModel?) {
        playingSong?.let {
            updateAudioPlayerDetails(it,!mBottomAudioPlayerBinding.exoPlayerView.player?.isSongPlaying()!!)
            mPlayingSongLiveData?.value = it
        }
    }

    override fun changeUserSigningInfo() {
        if(mUserInfo.isUserLoggedIn) {
            signOutUser()
        } else {
            signInUser()
        }

    }

    override fun onProfileClick(view:View) {}

    override fun onPermissionResult(isAllGranted: Boolean, permissionResults: ArrayList<PermissionModel>?, requestCode: Int) {
        if(isAllGranted){
            mSongViewModel?.fetchAllSongsFromDevice()
        }
    }
}
