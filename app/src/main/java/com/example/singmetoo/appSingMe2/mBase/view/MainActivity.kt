package com.example.singmetoo.appSingMe2.mBase.view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.interfaces.CommonBaseInterface
import com.example.singmetoo.appSingMe2.mBase.interfaces.NavigationDrawerInterface
import com.example.singmetoo.appSingMe2.mBase.util.BaseActivity
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.appSingMe2.mBase.util.DrawerManager
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongsViewModel
import com.example.singmetoo.appSingMe2.mHome.view.HomeFragment
import com.example.singmetoo.appSingMe2.mUtils.helpers.*
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.audioPlayerHelper.PlayerStatus
import com.example.singmetoo.databinding.ActivityMainBinding
import com.example.singmetoo.databinding.BottomAudioPlayerBinding
import com.example.singmetoo.databinding.NavHeaderMainBinding
import com.example.singmetoo.permissionHelper.PermissionModel
import com.example.singmetoo.permissionHelper.PermissionsManager
import com.example.singmetoo.permissionHelper.PermissionsResultInterface
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_audio_player.view.*

class MainActivity : BaseActivity(), CommonBaseInterface,NavigationDrawerInterface,PermissionsResultInterface {

    private lateinit var mLayoutBinding: ActivityMainBinding
    private lateinit var mBottomAudioPlayerBinding: BottomAudioPlayerBinding
    private var actionBarDrawerToggle:ActionBarDrawerToggle? = null
    private var drawerManager: DrawerManager? = null
    private var mBottomSheetAudioPlayerBehaviour: BottomSheetBehavior<View>? = null
    private val mPlayerStatusLiveData: MutableLiveData<PlayerStatus> = MutableLiveData()
    private var mSongViewModel : SongsViewModel? = null
    private var mCurrentPlayingSong: SongModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBottomAudioPlayerBinding = mLayoutBinding.appBarMain.includeAudioPlayerLayout

        init()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle?.syncState()
    }

    private fun init() {
        initObj()
        initNavBar()
        initSetUpAudioPlayerBottomSheet()
        initFetchSongsFromDevice()
        initObserver()
        initOpenHomeFragment()
    }

    private fun initObserver() {
        mSongViewModel?.getSongsLiveData()?.observe(this@MainActivity, Observer { list ->
            mCurrentPlayingSong = AppUtil.getPlayingSongFromList(list)
            mCurrentPlayingSong?.let {
                showBottomAudioPlayer(it)
            }
        })
    }

    private fun initSetUpAudioPlayerBottomSheet() {
        mBottomSheetAudioPlayerBehaviour = BottomSheetBehavior.from(mBottomAudioPlayerBinding.audioPlayerMainCl)
        mBottomSheetAudioPlayerBehaviour?.peekHeight = this.fetchDimen(R.dimen.audio_player_preview_height)
        mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
        mBottomSheetAudioPlayerBehaviour?.isHideable = false
        addBottomSheetCallbacks()
    }

    private fun addBottomSheetCallbacks() {
        mBottomSheetAudioPlayerBehaviour?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when(newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN-> {

                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    else -> {

                    }
                }
            }

        })
    }

    private fun initObj() {
        drawerManager = DrawerManager(this, mLayoutBinding.drawerLayout)
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
            mSongViewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
            mSongViewModel?.fetchAllSongsFromDevice()
        }
    }

    private fun initOpenHomeFragment() {
        supportFragmentManager.addFragment(fragment = HomeFragment(),container = R.id.main_activity_container,fragmentTag = AppConstants.HOME_FRAGMENT_TAG)
    }

    private fun setNavHeaderView() {
        val headerLayoutBinding: NavHeaderMainBinding? = NavHeaderMainBinding.bind(mLayoutBinding.navView.getHeaderView(0))

        //set profile name using extension method (setProfileName)
        headerLayoutBinding?.navProfileName?.setProfileName(mUserInfo.userName)

        //setting binding variables
        headerLayoutBinding?.profilePhotoUrl = "${mUserInfo.userName?.get(0)}"
        headerLayoutBinding?.hasUserProfilePhoto = AppUtil.checkIsNotNull(mUserInfo.userProfilePicUrl)
        headerLayoutBinding?.navBarCallback = this
        headerLayoutBinding?.userInfo = mUserInfo
    }

    override fun onBackPressed() {
        //if left nav_drawer is open
        if(mLayoutBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            mLayoutBinding.drawerLayout.closeDrawers()
            return
        }

        when (val baseFragment: BaseFragment? = supportFragmentManager.findFragmentById(R.id.main_activity_container) as? BaseFragment) {
            is HomeFragment -> {
                (baseFragment as? HomeFragment)?.onBackPressed()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override val playerStatusLiveData: MutableLiveData<PlayerStatus>?
        get() = mPlayerStatusLiveData

    override fun playAudio(songModel: SongModel?) {

    }

    override fun pauseAudio() {

    }

    override fun stopAudio() {

    }

    override fun showBottomAudioPlayer(songToPlay:SongModel?) {
        songToPlay?.let {
            mBottomAudioPlayerBinding.audioPlayerPreviewTitleTv.text = it.songTitle ?: AppConstants.DEFAULT_TITLE
            mBottomAudioPlayerBinding.audioPlayerPreviewArtistTv.text = it.songArtist ?: AppConstants.DEFAULT_ARTIST
            mBottomAudioPlayerBinding.audioPlayerPreviewTitleTv.isSelected = true
            mBottomAudioPlayerBinding.audioPlayerPreviewArtistTv.isSelected = true
            mBottomAudioPlayerBinding.audioPlayerMainCl.visibility = View.VISIBLE
            mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
            mBottomAudioPlayerBinding.exoPlayerView.showController()
        }
    }

    override fun hideBottomAudioPlayer() {
        mBottomAudioPlayerBinding.audioPlayerMainCl.visibility = View.GONE
        mBottomSheetAudioPlayerBehaviour?.state = BottomSheetBehavior.STATE_COLLAPSED
        mBottomAudioPlayerBinding.exoPlayerView.hideController()
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

    override fun onProfileClick(view:View) {
        mLayoutBinding.drawerLayout.closeDrawer(GravityCompat.START)
        AppUtil.showToast(this,"onProfileClick")
    }

    override fun onPermissionResult(isAllGranted: Boolean, permissionResults: ArrayList<PermissionModel>?, requestCode: Int) {
        if(isAllGranted){
            val viewModelSongs: SongsViewModel? = ViewModelProviders.of(this).get(SongsViewModel::class.java)
            viewModelSongs?.fetchAllSongsFromDevice()
        }
    }
}
