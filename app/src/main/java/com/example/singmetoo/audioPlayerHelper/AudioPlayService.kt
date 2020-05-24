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
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ExoPlayerFactory.newSimpleInstance
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

private const val PLAYBACK_CHANNEL_ID = "playback_channel"
private const val PLAYBACK_NOTIFICATION_ID = 1
private const val MEDIA_SESSION_TAG = "sed_audio"

class AudioPlayService : Service() {

    companion object {
        @MainThread
        fun newIntent(context: Context, songDetails: SongModel? = null) = Intent(context, AudioPlayService::class.java).apply {
            songDetails?.let {
                putExtra(AppConstants.ARG_SONG_ID, it.songId.toString())
                it.songTitle?.let { title -> putExtra(AppConstants.ARG_SONG_TITLE, title) }
                it.songPath?.let{ songPath -> putExtra(AppConstants.ARG_SONG_PATH, Uri.parse(songPath)) }
                putExtra(AppConstants.ARG_SONG_START_POS, 0L)
            }
        }
    }


    private lateinit var mExoPlayer: SimpleExoPlayer
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionConnector: MediaSessionConnector? = null
    var mSongId: String? = null
    var mSongTitle: String? = null
    private val mPlayerStatusLiveData = MutableLiveData<PlayerStatus>()
    val playerStatusLiveData: LiveData<PlayerStatus>
        get() = mPlayerStatusLiveData

    override fun onBind(intent: Intent?): IBinder? {
        handleIntent(intent)
        return PlayMusicServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()

        initialiseExoPlayer()

        setMusicNotification()

        setMediaSession()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleIntent(intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        releaseExoPlayer()
        super.onDestroy()
    }

    private fun releaseExoPlayer() {
        mediaSession?.release()
        mediaSessionConnector?.setPlayer(null)
        playerNotificationManager?.setPlayer(null)
        mExoPlayer.release()
    }

    @MainThread
    private fun handleIntent(intent: Intent?) {
        // Play
        intent?.let {
            intent.getParcelableExtra<Uri>(AppConstants.ARG_SONG_PATH)?.also { uri ->
                mSongId = intent.getStringExtra(AppConstants.ARG_SONG_ID)
                mSongTitle = intent.getStringExtra(AppConstants.ARG_SONG_TITLE)
                val startPosition = intent.getLongExtra(AppConstants.ARG_SONG_START_POS, C.POSITION_UNSET.toLong())
                play(uri, startPosition)
            } ?: R.string.playback_uri_was_not_set
        }
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

    private fun setMusicNotification() {

        // Setup notification and media session.
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            applicationContext,
            PLAYBACK_CHANNEL_ID,
            R.string.playback_channel_name,
            PLAYBACK_NOTIFICATION_ID,
            object : PlayerNotificationManager.MediaDescriptionAdapter {

                override fun getCurrentContentTitle(player: Player): String {
                    return mSongTitle ?: getString(R.string.loading_dots)
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
                    return getBitmapFromVectorDrawable(applicationContext)
                }
            },
            object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
                    startForeground(notificationId, notification)
                }

                override fun onNotificationCancelled(notificationId: Int) {
                    mPlayerStatusLiveData.value = PlayerStatus.Cancelled(mSongId)
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
            // Omit skip previous and next actions.
            setUseNavigationActions(false)

            // Add stop action.
            setUseStopAction(true)

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
                    val bitmap = getBitmapFromVectorDrawable(applicationContext)
                    val extras = Bundle().apply {
                        putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                        putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
                    }

                    val title = mSongTitle ?: getString(R.string.loading_dots)

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
    fun play(uri: Uri, startPosition: Long) {
        val userAgent = Util.getUserAgent(applicationContext, BuildConfig.APPLICATION_ID)
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(applicationContext, userAgent)
        val mediaSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        val haveStartPosition = startPosition != C.POSITION_UNSET.toLong()
        if (haveStartPosition) {
            mExoPlayer.seekTo(startPosition)
        }

        mExoPlayer.prepare(mediaSource, !haveStartPosition, false)
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
    private fun getBitmapFromVectorDrawable(context: Context, @DrawableRes drawableId: Int = R.drawable.bg_default_playing_song): Bitmap? {
        return ContextCompat.getDrawable(context, drawableId)?.let {
            val drawable = DrawableCompat.wrap(it).mutate()
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when(playbackState) {
                Player.STATE_READY -> {
                    if (mExoPlayer.playWhenReady) {
                        mSongId?.let { mPlayerStatusLiveData.value =
                            PlayerStatus.Playing(it)
                        }
                    } else {
                        mSongId?.let { mPlayerStatusLiveData.value =
                            PlayerStatus.Paused(it)
                        }
                    }
                }

                Player.STATE_ENDED -> {
                    mSongId?.let { mPlayerStatusLiveData.value =
                        PlayerStatus.Ended(it)
                    }
                }

                Player.STATE_BUFFERING -> {
                    mSongId?.let { mPlayerStatusLiveData.value =
                        PlayerStatus.Buffering(it)
                    }
                }

                Player.STATE_IDLE -> {
                    mSongId?.let { mPlayerStatusLiveData.value =
                        PlayerStatus.Idle(it)
                    }
                }
            }
        }

        override fun onPlayerError(e: ExoPlaybackException?) {
            mSongId?.let { mPlayerStatusLiveData.value =
                PlayerStatus.Error(it, e)
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