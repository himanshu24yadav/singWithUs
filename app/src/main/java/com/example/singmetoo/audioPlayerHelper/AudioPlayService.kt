package com.example.singmetoo.audioPlayerHelper

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.DrawableRes
import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.view.MainActivity
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppConstants
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppUtil
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongsRepository
import com.example.singmetoo.frescoHelper.FrescoHelper
import com.example.singmetoo.frescoHelper.FrescoLoadBitmapCallback
import com.facebook.common.references.CloseableReference
import com.facebook.imagepipeline.image.CloseableImage
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlayerFactory.newSimpleInstance
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

private const val PLAYBACK_CHANNEL_ID = "playback_channel"
private const val PLAYBACK_NOTIFICATION_ID = 1
private const val MEDIA_SESSION_TAG = "media_session_audio"
private const val FROM_ON_BIND = "FROM_ON_BIND"
private const val FROM_ON_START_COMMAND = "FROM_ON_START_COMMAND"

class AudioPlayService : Service() {

    companion object {
        @MainThread
        fun newIntent(context: Context, songDetails: SongModel? = null) = Intent(context, AudioPlayService::class.java).apply {
            songDetails?.let {
                putExtra(AppConstants.ARG_SONG_ID, it.songId)
            }
        }

        var isAudioPlayServiceRunning: Boolean = false
    }


    private lateinit var mExoPlayer: SimpleExoPlayer
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionConnector: MediaSessionConnector? = null
    private var mSongList: ArrayList<SongModel>? = ArrayList()
    var mCurrentlyPlayingSongId: Long? = null
    private var mDefaultBitmap: Bitmap? = null
    private var mPlayerStatusLiveData = MutableLiveData<PlayerStatus>()
    val playerStatusLiveData: LiveData<PlayerStatus>
        get() = mPlayerStatusLiveData

    override fun onBind(intent: Intent?): IBinder? {
        handleIntent(intent, FROM_ON_BIND)
        return PlayMusicServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()

        initialiseExoPlayer()

        setDefaultBitmap()

        setMusicNotification()

        setMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent, FROM_ON_START_COMMAND)
        isAudioPlayServiceRunning = true
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        releaseExoPlayer()
        isAudioPlayServiceRunning = false
        super.onDestroy()
    }

    private fun releaseExoPlayer() {
        mediaSession?.release()
        mediaSessionConnector?.setPlayer(null)
        playerNotificationManager?.setPlayer(null)
        mExoPlayer.release()
    }

    @MainThread
    private fun handleIntent(intent: Intent?,from: String) {

        // prepare ExoPlayer
        intent?.let {
            mSongList = SongsRepository.mSongLiveData?.value ?: ArrayList()
            mSongList?.let {
                mCurrentlyPlayingSongId = intent.getLongExtra(AppConstants.ARG_SONG_ID,0)
                if(from == FROM_ON_START_COMMAND) prepareExoPlayer()
            }
        }
    }

    private fun prepareExoPlayer() {
        val userAgent = Util.getUserAgent(applicationContext, BuildConfig.APPLICATION_ID)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(applicationContext, userAgent)
        val concatenatingMediaSource: ConcatenatingMediaSource? = ConcatenatingMediaSource()
        var windowIndex = 0

        mSongList?.let {
            for (index in 0 until it.size) {
                val songItem:SongModel? = it[index]
                if(songItem?.songId == mCurrentlyPlayingSongId) { windowIndex =  index }
                val mediaSource: MediaSource? = ProgressiveMediaSource.Factory(dataSourceFactory).setTag(songItem?.songId).createMediaSource(Uri.parse(songItem?.songPath))
                mediaSource?.let { ms -> concatenatingMediaSource?.addMediaSource(ms) }
            }
        }

        mExoPlayer.seekTo(windowIndex,0)
        mExoPlayer.prepare(concatenatingMediaSource, false, false)
        mExoPlayer.playWhenReady = true
    }

    private fun initialiseExoPlayer() {
        mExoPlayer = newSimpleInstance(this, DefaultTrackSelector())
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_SPEECH)
            .build()
        mExoPlayer.setAudioAttributes(audioAttributes, true)

        // Monitor ExoPlayer events.
        mExoPlayer.addListener(PlayerEventListener())
    }

    private fun setDefaultBitmap() {
        FrescoHelper.getBitmapFromImagePath(frescoLoadBitmapCallback = object : FrescoLoadBitmapCallback {
            override fun onBitmapLoadSuccess(bitmap: Bitmap?) {
                mDefaultBitmap = bitmap
            }

            override fun onBitmapLoadFailure(dataSource: com.facebook.datasource.DataSource<CloseableReference<CloseableImage?>>?) {
                mDefaultBitmap = null
            }
        })
    }

    private fun setMusicNotification() {

        // Setup notification and media session.
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            applicationContext,
            PLAYBACK_CHANNEL_ID,
            R.string.playback_channel_name,
            PLAYBACK_NOTIFICATION_ID,
            object : PlayerNotificationManager.MediaDescriptionAdapter {

                override fun getCurrentContentTitle(player: Player): String {
                    return if(mSongList!!.size > 0) {
                        mSongList!![player.currentWindowIndex].songTitle ?: getString(R.string.loading_dots)
                    } else {
                        getString(R.string.loading_dots)
                    }
                }

                @Nullable
                override fun createCurrentContentIntent(player: Player): PendingIntent? = PendingIntent.getActivity(applicationContext, 0,
                    Intent(applicationContext, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT)

                @Nullable
                override fun getCurrentContentText(player: Player): String? {
                    return null
                }

                @Nullable
                override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
                    return getBitmapForSong(player)
                }
            },
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
                    startForeground(notificationId, notification)
                }

                override fun onNotificationCancelled(notificationId: Int) {
                    mPlayerStatusLiveData.value = PlayerStatus.Cancelled(mCurrentlyPlayingSongId.toString())
                    stopSelf()
                }

                override fun onNotificationPosted(notificationId: Int, notification: Notification?, ongoing: Boolean) {
                    if (ongoing) {
                        // Make sure the service will not get destroyed while playing media.
                        startForeground(notificationId, notification)
                    } else {
                        // Make notification cancellable.
                        stopForeground(false)
                    }
                }
            }

        ).apply {
            setUseNavigationActions(true)
            setRewindIncrementMs(0)
            setFastForwardIncrementMs(0)
            setUseStopAction(false)
            setPlayer(mExoPlayer)
        }
    }

    private fun setMediaSession() {
        // Show lock screen controls and let apps like Google assistant manager playback.
        mediaSession = MediaSessionCompat(applicationContext, MEDIA_SESSION_TAG).apply {
            isActive = true
        }
        playerNotificationManager?.setMediaSessionToken(mediaSession?.sessionToken)

        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
                    val bitmap: Bitmap? = getBitmapForSong(player)
                    val extras = Bundle().apply {
                        putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                        putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
                    }

                    val title = if(mSongList!!.size > 0) {
                        mSongList!![player.currentWindowIndex].songTitle ?: getString(R.string.loading_dots)
                    } else {
                        getString(R.string.loading_dots)
                    }

                    return MediaDescriptionCompat.Builder()
                        .setIconBitmap(bitmap)
                        .setTitle(title)
                        .setExtras(extras)
                        .build()
                }
            })

            setPlayer(mExoPlayer)
        }
    }

    @MainThread
    fun play(songId:Long?) {
        val windowIndex = mSongList?.indexOf(AppUtil.getPlayingSongFromList(mSongList,songId)) ?: 0
        mExoPlayer.seekTo(windowIndex,0)
        mExoPlayer.playWhenReady = true
    }

    @MainThread
    fun resume() {
        mExoPlayer.playWhenReady = true
    }

    @MainThread
    fun pause() {
        mExoPlayer.playWhenReady = false
    }

    @MainThread
    private fun getBitmapForSong(player: Player): Bitmap? {
        var bitmapToLoad: Bitmap? = mDefaultBitmap
        val albumId : Long? = if(mSongList!!.size > 0) {
            mSongList!![player.currentWindowIndex].songAlbumId
        } else {
            null
        }

        FrescoHelper.getBitmapFromImagePath(AppUtil.getImageUriFromAlbum(albumId).toString(), frescoLoadBitmapCallback = object : FrescoLoadBitmapCallback{
            override fun onBitmapLoadSuccess(bitmap: Bitmap?) {
                bitmapToLoad = bitmap
            }
            override fun onBitmapLoadFailure(dataSource: com.facebook.datasource.DataSource<CloseableReference<CloseableImage?>>?) {
                bitmapToLoad = mDefaultBitmap
            }
        })

        return bitmapToLoad
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when(playbackState) {
                Player.STATE_READY -> {
                    mExoPlayer.currentTag?.let {
                        mCurrentlyPlayingSongId = it.toString().toLong()
                    }

                    if (mExoPlayer.playWhenReady) {
                        mCurrentlyPlayingSongId?.let { mPlayerStatusLiveData.value =
                            PlayerStatus.Playing(it.toString())
                        }
                    } else {
                        mCurrentlyPlayingSongId?.let { mPlayerStatusLiveData.value =
                            PlayerStatus.Paused(it.toString())
                        }
                    }
                }

                Player.STATE_ENDED -> {
                    mCurrentlyPlayingSongId?.let { mPlayerStatusLiveData.value =
                        PlayerStatus.Ended(it.toString())
                    }
                }

                Player.STATE_BUFFERING -> {
                    mCurrentlyPlayingSongId?.let { mPlayerStatusLiveData.value =
                        PlayerStatus.Buffering(it.toString())
                    }
                }

                Player.STATE_IDLE -> {
                    mCurrentlyPlayingSongId?.let { mPlayerStatusLiveData.value =
                        PlayerStatus.Idle(it.toString())
                    }
                }
            }
        }

        override fun onPlayerError(e: ExoPlaybackException?) {
            mCurrentlyPlayingSongId?.let { mPlayerStatusLiveData.value =
                PlayerStatus.Error(it.toString(), e)
            }
        }

    }

    inner class PlayMusicServiceBinder : Binder() {
        val playMusicService
            get() = this@AudioPlayService

        val exoPlayer
            get() = this@AudioPlayService.mExoPlayer
    }
}