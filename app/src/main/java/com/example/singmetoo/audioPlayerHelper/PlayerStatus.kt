package com.example.singmetoo.audioPlayerHelper

sealed class PlayerStatus(open val songId: String?) {
    data class Other(override val songId: String? = null) : PlayerStatus(songId)
    data class Playing(override val songId: String) : PlayerStatus(songId)
    data class Paused(override val songId: String) : PlayerStatus(songId)
    data class Cancelled(override val songId: String? = null) : PlayerStatus(songId)
    data class Ended(override val songId: String) : PlayerStatus(songId)
    data class Buffering(override val songId: String) : PlayerStatus(songId)
    data class Idle(override val songId: String) : PlayerStatus(songId)
    data class Error(override val songId: String, val exception: Exception?) : PlayerStatus(songId)
}