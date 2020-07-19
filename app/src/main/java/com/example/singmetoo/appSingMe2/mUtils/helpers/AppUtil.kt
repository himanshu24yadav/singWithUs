package com.example.singmetoo.appSingMe2.mUtils.helpers

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.util.BaseActivity
import com.example.singmetoo.appSingMe2.mHome.pojo.HomeContentModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.google.android.exoplayer2.Player

class AppUtil {

    companion object {
        fun showToast(ctx: Context?,msg:String) {
            ctx?.let { Toast.makeText(it,msg,Toast.LENGTH_SHORT).show() }
        }

        fun getFirstName(name:String?) : String?{
           val index = name?.lastIndexOf(' ') ?: -1
            return if(index != -1)
                name?.let { name.substring(0,index) }
            else
                name
        }

        fun checkIsNotNull(str:String?):Boolean {
            return str!=null && !"".equals(str,ignoreCase = false) && !"null".equals(str,ignoreCase = false)
        }

        fun getHomeItems(context:Context?): ArrayList<HomeContentModel>? {
            val homeItems: ArrayList<HomeContentModel>? = ArrayList()

            //musicLibrary item
            val musicLibrary = HomeContentModel()
            musicLibrary.title = context?.fetchString(R.string.home_item_music_title)
            musicLibrary.subtitle = "Your library"
            musicLibrary.resId = R.drawable.ic_home_music_item
            homeItems?.add(musicLibrary)

            //playlist item
            val playlist = HomeContentModel()
            playlist.title = context?.fetchString(R.string.home_item_about_us)
            playlist.subtitle = "Who are we"
            playlist.resId = R.drawable.ic_home_music_item
            homeItems?.add(playlist)

            return homeItems
        }

        fun getPlayingSongFromList(songListFromDevice:ArrayList<SongModel>?,songId:Long? = null) : SongModel?{
            var playingSongModel:SongModel? = null

            if(songId == null) {
                songListFromDevice?.let {
                    for (item in it) {
                        if (item.songCurrentlyPlaying) {
                            playingSongModel = item
                            break
                        }
                    }
                }
            } else {
                songListFromDevice?.let {
                    for (item in it) {
                        if(item.songId == songId){
                            playingSongModel = item
                            break
                        }
                    }
                }
            }

            return playingSongModel ?: if(songListFromDevice?.size!! > 0) songListFromDevice[0] else null
        }

        fun getImageUriFromAlbum(albumId:Long?) : Uri? {
            var uri: Uri? = null
            albumId?.let {
                val sArtworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
                uri =  ContentUris.withAppendedId(sArtworkUri, it)
            }
            return uri
        }

        fun toResumePlayingSong(newSongId:Long?,audioServiceSongId:Long?,exoPlayer:Player?) : Boolean {
            return (newSongId == audioServiceSongId) && (exoPlayer?.isSongPaused()!!)
        }

        fun openDetailDialog(mContext:Context?) {
            val dialogFragment = DialogAboutUsFragment()
            dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_AppCompat)

            if (!dialogFragment.isAdded && !dialogFragment.isVisible && mContext!=null) dialogFragment.show((mContext as BaseActivity).supportFragmentManager, "")
        }
    }

}