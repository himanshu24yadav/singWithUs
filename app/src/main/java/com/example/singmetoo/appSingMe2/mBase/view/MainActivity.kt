package com.example.singmetoo.appSingMe2.mBase.view

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.interfaces.CommonBaseInterface
import com.example.singmetoo.appSingMe2.mBase.util.DrawerManager
import com.example.singmetoo.appSingMe2.mBase.util.BaseActivity
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.appSingMe2.mHome.HomeFragment
import com.example.singmetoo.appSingMe2.mUtils.AppUtil
import com.example.singmetoo.appSingMe2.mUtils.addFragment
import com.example.singmetoo.appSingMe2.mUtils.setProfileName
import com.example.singmetoo.databinding.ActivityMainBinding
import com.example.singmetoo.databinding.NavHeaderMainBinding

class MainActivity : BaseActivity(), CommonBaseInterface {

    private lateinit var mLayoutBinding: ActivityMainBinding
    private var actionBarDrawerToggle:ActionBarDrawerToggle? = null
    private var drawerManager: DrawerManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        init()
    }

    private fun init() {
        initObj()
        initNavBar()
        initOpenHomeFragment()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        actionBarDrawerToggle?.syncState()
    }

    private fun initObj() {
        drawerManager = DrawerManager(this, mLayoutBinding.drawerLayout)
    }

    private fun initNavBar() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this, mLayoutBinding.drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_open)
        actionBarDrawerToggle?.let { mLayoutBinding.drawerLayout.addDrawerListener(it) }
        mLayoutBinding.navView.setNavigationItemSelectedListener(drawerManager)
        setNavHeaderView()
    }

    private fun initOpenHomeFragment() {
        supportFragmentManager.addFragment(HomeFragment(),R.id.main_activity_container)
    }

    private fun setNavHeaderView() {
        val headerLayoutBinding: NavHeaderMainBinding? = NavHeaderMainBinding.bind(mLayoutBinding.navView.getHeaderView(0))

        //profile name
        headerLayoutBinding?.navProfileName?.setProfileName(mUserInfo.userName)

        //profile pic
        headerLayoutBinding?.profilePhotoUrl = "${mUserInfo.userName?.get(0)}"
        headerLayoutBinding?.hasUserProfilePhoto = AppUtil.checkIsNotNull(mUserInfo.userProfilePicUrl)
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
}
