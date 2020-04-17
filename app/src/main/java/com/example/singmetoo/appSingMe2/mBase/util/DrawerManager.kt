package com.example.singmetoo.appSingMe2.mBase.util

import android.content.Context
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mUtils.AppUtil
import com.google.android.material.navigation.NavigationView

class DrawerManager(var mContext: Context?, var drawerLayout: DrawerLayout) : NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_item_home -> { AppUtil.showToast(mContext!!,"Home") }

            R.id.nav_item_your_music -> { AppUtil.showToast(mContext!!,"Your Music") }

            else -> { AppUtil.showToast(mContext!!,"Else") }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}