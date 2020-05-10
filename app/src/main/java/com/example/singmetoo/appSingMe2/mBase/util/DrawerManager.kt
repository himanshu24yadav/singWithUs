package com.example.singmetoo.appSingMe2.mBase.util

import android.content.Context
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.view.MainActivity
import com.example.singmetoo.appSingMe2.mUtils.AppUtil
import com.example.singmetoo.appSingMe2.mUtils.NavigationHelper
import com.google.android.material.navigation.NavigationView

class DrawerManager(var mContext: Context?, var drawerLayout: DrawerLayout) : NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_home -> { NavigationHelper.openHomeFragment() }

            R.id.nav_item_your_music -> { NavigationHelper.openYourMusicFragment((mContext as? MainActivity)?.supportFragmentManager) }

            R.id.nav_item_playlist -> { NavigationHelper.openPlaylistFragment() }

            R.id.nav_item_about -> { NavigationHelper.openAboutUsFragment() }

            R.id.nav_item_logout -> { AppUtil.showToast(mContext,"Logout") }

            else -> { AppUtil.showToast(mContext,"Else") }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}