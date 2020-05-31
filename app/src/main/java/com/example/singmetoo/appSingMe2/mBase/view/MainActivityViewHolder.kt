package com.example.singmetoo.appSingMe2.mBase.view

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Barrier
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.singmetoo.R
import com.facebook.drawee.view.SimpleDraweeView
import de.hdodenhof.circleimageview.CircleImageView

class MainActivityViewHolder (bottomSheetView:View?) {

    var audioPlayerPreviewCl : ConstraintLayout? = null
        private set
    var nowPlayingHeaderCl : ConstraintLayout? = null
        private set
    var nowPlayingBodyCl : ConstraintLayout? = null
        private set
    var audioPlayerPreviewIv : ImageView? = null
        private set
    var audioPlayerPreviewPlayIv : ImageView? = null
        private set
    var audioPlayerPreviewTitleTv : TextView? = null
        private set
    var audioPlayerPreviewArtistTv : TextView? = null
        private set
    var headerBarrier : Barrier? = null
        private set
    var nowPlayingSongTitle : TextView? = null
        private set
    var nowPlayingSongSubtitle : TextView? = null
        private set
    var nowPlayingHeaderPlaylistIv : ImageView? = null
        private set
    var nowPlayingHeaderMenuIv : ImageView? = null
        private set
    var nowPlayingSongIv : CircleImageView? = null
        private set
    var nowPlayingSongBgIv : ImageView? = null
        private set
    var playerControlsLayout : RelativeLayout? = null
        private set


    init {

        //playing song header
        audioPlayerPreviewCl = bottomSheetView?.findViewById(R.id.audio_player_preview_cl)
        audioPlayerPreviewIv = bottomSheetView?.findViewById(R.id.audio_player_preview_iv)
        audioPlayerPreviewPlayIv = bottomSheetView?.findViewById(R.id.audio_player_preview_play_iv)
        audioPlayerPreviewTitleTv = bottomSheetView?.findViewById(R.id.audio_player_preview_title_tv)
        audioPlayerPreviewArtistTv = bottomSheetView?.findViewById(R.id.audio_player_preview_artist_tv)

        //now playing header
        nowPlayingHeaderCl = bottomSheetView?.findViewById(R.id.now_playing_header_cl)
        nowPlayingBodyCl = bottomSheetView?.findViewById(R.id.now_playing_body_cl)
        nowPlayingSongTitle = bottomSheetView?.findViewById(R.id.now_playing_song_title)
        nowPlayingSongSubtitle = bottomSheetView?.findViewById(R.id.now_playing_song_subtitle)
        nowPlayingHeaderPlaylistIv = bottomSheetView?.findViewById(R.id.now_playing_header_playlist_iv)
        nowPlayingHeaderMenuIv = bottomSheetView?.findViewById(R.id.now_playing_header_menu_iv)

        //now playing song view
        nowPlayingSongIv = bottomSheetView?.findViewById(R.id.now_playing_song_iv)
        nowPlayingSongBgIv = bottomSheetView?.findViewById(R.id.now_playing_song_bg_iv)

        //barrier
        headerBarrier = bottomSheetView?.findViewById(R.id.header_barrier)

        //player control view
        playerControlsLayout = bottomSheetView?.findViewById(R.id.player_controls_layout)

    }
}