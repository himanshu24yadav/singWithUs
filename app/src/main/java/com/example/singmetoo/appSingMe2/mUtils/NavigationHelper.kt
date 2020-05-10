package com.example.singmetoo.appSingMe2.mUtils

import androidx.fragment.app.FragmentManager
import com.example.singmetoo.CustomApplicationClass
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mHome.view.HomeFragment
import com.example.singmetoo.appSingMe2.mMusicLibrary.MusicLibraryFragment
import kotlinx.android.synthetic.main.layout_home_fragment.view.*

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

        fun openRecentFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openRecentFragment")
        }

        fun openYourMusicFragment(supportFragmentManager: FragmentManager?) {
            supportFragmentManager?.activeFragment().let {
                if (it == null || it !is MusicLibraryFragment) {
                    supportFragmentManager?.addFragment(fragment = MusicLibraryFragment(),container = R.id.main_activity_container,fragmentTag = AppConstants.MUSIC_LIBRARY_FRAGMENT_TAG)
                }
            }
        }

        fun openPlaylistFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openPlaylistFragment")
        }

        fun openRadioFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openRadioFragment")
        }

        fun openAboutUsFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openAboutUsFragment")
        }
    }
}