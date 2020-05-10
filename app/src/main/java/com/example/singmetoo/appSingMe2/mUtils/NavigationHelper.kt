package com.example.singmetoo.appSingMe2.mUtils

import androidx.fragment.app.FragmentManager
import com.example.singmetoo.CustomApplicationClass
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mMusicLibrary.MusicLibraryFragment
import kotlinx.android.synthetic.main.layout_home_fragment.view.*

class NavigationHelper {
    companion object {

        fun openHomeFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openHomeFragment")
        }

        fun openRecentFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openRecentFragment")
        }

        fun openYourMusicFragment(supportFragmentManager: FragmentManager?) {
            supportFragmentManager?.addFragment(MusicLibraryFragment(),R.id.main_activity_container,"Music")
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