package com.example.singmetoo.appSingMe2.mUtils

import com.example.singmetoo.CustomApplicationClass

class NavigationHelper {
    companion object {

        fun openHomeFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openHomeFragment")
        }

        fun openRecentFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openRecentFragment")
        }

        fun openYourMusicFragment(){
            AppUtil.showToast(CustomApplicationClass.applicationContext(),"openYourMusicFragment")
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