package com.example.singmetoo.appSingMe2.mUtils.helpers

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.singmetoo.CustomApplicationClass
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mHome.view.HomeFragment
import com.example.singmetoo.appSingMe2.mMusicLibrary.view.MusicLibraryFragment

class NavigationHelper {
    companion object {

        fun openHomeFragment(supportFragmentManager: FragmentManager?){
            supportFragmentManager?.activeFragment().let {
                if(it == null || it !is HomeFragment){
                    supportFragmentManager?.clearBackStack()
                    supportFragmentManager?.addFragment(fragment = HomeFragment(),container = R.id.main_activity_container,fragmentTag = AppConstants.HOME_FRAGMENT_TAG)
                }
            }
        }

        fun openYourMusicFragment(supportFragmentManager: FragmentManager?) {
            supportFragmentManager?.activeFragment().let {
                if (it == null || it !is MusicLibraryFragment) {
                    supportFragmentManager?.addFragment(fragment = MusicLibraryFragment(),container = R.id.main_activity_container,fragmentTag = AppConstants.MUSIC_LIBRARY_FRAGMENT_TAG)
                }
            }
        }

        fun openAboutUsFragment(mContext: Context?) {
            AppUtil.openDetailDialog(mContext)
        }
    }
}