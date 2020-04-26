package com.example.singmetoo.appSingMe2.mUtils

import android.content.Context
import android.widget.Toast
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mHome.pojo.HomeContentModel

class AppUtil {

    companion object {
        fun showToast(ctx: Context?,msg:String) {
            ctx?.let { Toast.makeText(it,msg,Toast.LENGTH_SHORT).show() }
        }

        fun getFirstName(name:String?) : String?{
           var firstName: String? = ""
           firstName = name?.let { name.substring(0,name.lastIndexOf(' ')) }
           return firstName
        }

        fun checkIsNotNull(str:String?):Boolean {
            return str!=null && !"".equals(str,ignoreCase = false) && !"null".equals(str,ignoreCase = false)
        }

        fun getHomeItems(context:Context?): ArrayList<HomeContentModel>? {
            val homeItems: ArrayList<HomeContentModel>? = ArrayList()

            //musicLibrary item
            val recents = HomeContentModel()
            recents.title = context?.fetchString(R.string.home_item_recents_title)
            recents.subtitle = "Pick the best"
            recents.resId = R.drawable.ic_home_recent_item
            homeItems?.add(recents)

            //musicLibrary item
            val musicLibrary = HomeContentModel()
            musicLibrary.title = context?.fetchString(R.string.home_item_music_title)
            musicLibrary.subtitle = "Your library"
            musicLibrary.resId = R.drawable.ic_home_music_item
            homeItems?.add(musicLibrary)

            //playlist item
            val playlist = HomeContentModel()
            playlist.title = context?.fetchString(R.string.home_item_playlists_title)
            playlist.subtitle = "Choose your best"
            playlist.resId = R.drawable.ic_home_playlist_item
            homeItems?.add(playlist)

            //radioModel item
            val radioModel = HomeContentModel()
            radioModel.title = context?.fetchString(R.string.home_item_radio_title)
            radioModel.subtitle = "Find your tune"
            radioModel.resId = R.drawable.ic_home_radio_item
            homeItems?.add(radioModel)

            return homeItems
        }
    }

}