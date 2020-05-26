package com.example.singmetoo.appSingMe2.mUtils.helpers

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.util.BaseFragment
import com.example.singmetoo.frescoHelper.FrescoHelper
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.exoplayer2.Player

fun Context.fetchString(stringId:Int) : String {
    return resources.getString(stringId)
}

fun Context.fetchDimen(dimenId:Int) : Int {
    return resources.getDimensionPixelSize(dimenId)
}

fun Context.fetchColor(colorId:Int) : Int {
    return ContextCompat.getColor(this, colorId)
}

fun FragmentManager.addFragment(fragment:Fragment, container:Int, fragmentTag:String = AppConstants.DEFAULT_FRAGMENT_TAG, addToBackStack:Boolean = true) {
    if(!addToBackStack){
        beginTransaction().add(container,fragment,fragmentTag).commit()
    }
    else {
        beginTransaction().add(container,fragment,fragmentTag).addToBackStack(fragmentTag).commit()
    }
}

fun FragmentManager.activeFragment() : BaseFragment? {
    if (backStackEntryCount == 0) {
        return null
    }
    val tag: String? = getBackStackEntryAt(backStackEntryCount- 1).name
    return (findFragmentByTag(tag) as BaseFragment)
}

fun FragmentManager.clearBackStack(){
    for (count in 0 until backStackEntryCount){
        popBackStack()
    }
}

fun TextView.setProfileName(username:String?) {
    text = "Hi ${AppUtil.getFirstName(username)}"
}

@Suppress("DEPRECATION")
fun Context.isServiceRunning(serviceClassName: String): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

    return activityManager?.getRunningServices(Integer.MAX_VALUE)?.any { it.service.className == serviceClassName } ?: false
}

fun Player.isPlayingSong() : Boolean{
    return playbackState == Player.STATE_READY && playWhenReady
}

fun Player.isSongPaused() : Boolean {
    return playbackState == Player.STATE_READY && !playWhenReady
}

fun ImageView.setAlbumImage(uri: Uri?) {
    setImageURI(uri)
    if(drawable == null) {
        setImageResource(R.drawable.bg_default_playing_song)
    }
}

fun ImageView.togglePlayIcon(songPaused:Boolean) {
    tag = if(songPaused) {
        setImageResource(R.drawable.exo_icon_play)
        AppConstants.SONG_TAG_PLAY
    } else {
        setImageResource(R.drawable.exo_icon_pause)
        AppConstants.SONG_TAG_PAUSE
    }
}

fun SimpleDraweeView.setAlbumImageFromFresco(imagePath: String? = null, context:Context?){
    imagePath?.let {
        controller = FrescoHelper.getDraweeController(imagePath,this)
        hierarchy = context?.let { ctx -> FrescoHelper.getGenericHierarchyBuilderForSongItem(ctx) }
    }
}