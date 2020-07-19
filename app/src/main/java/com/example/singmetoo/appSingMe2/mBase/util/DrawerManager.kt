package com.example.singmetoo.appSingMe2.mBase.util

import android.content.Context
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.interfaces.CommonBaseInterface
import com.example.singmetoo.appSingMe2.mBase.view.MainActivity
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppUtil
import com.example.singmetoo.appSingMe2.mUtils.helpers.NavigationHelper
import com.google.android.material.navigation.NavigationView

class DrawerManager(var mContext: Context?, var drawerLayout: DrawerLayout,val commonBaseInterface: CommonBaseInterface?) : NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_home -> { NavigationHelper.openHomeFragment((mContext as? MainActivity)?.supportFragmentManager) }

            R.id.nav_item_your_music -> { NavigationHelper.openYourMusicFragment((mContext as? MainActivity)?.supportFragmentManager) }

            R.id.nav_item_about -> { NavigationHelper.openAboutUsFragment(mContext) }

            R.id.nav_item_logout -> { commonBaseInterface?.changeUserSigningInfo() }

            else -> { AppUtil.showToast(mContext,"Else") }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}