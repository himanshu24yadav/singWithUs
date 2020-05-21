package com.example.singmetoo.appSingMe2.mMusicLibrary.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mMusicLibrary.interfaces.MusicLibraryAdapterCallback
import com.example.singmetoo.appSingMe2.mUtils.helpers.fetchColor
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.databinding.LayoutSongItemBinding

class MusicLibraryAdapter(var mContext:Context?, private var mLocalSongsList:ArrayList<SongModel>?,
                          private val callback: MusicLibraryAdapterCallback?,
                          private var selectedSongIndex:Int = 0) : RecyclerView.Adapter<MusicLibraryAdapter.MyViewHolder>() {

    fun updateData(mLocalSongsList: ArrayList<SongModel>?){
        this.mLocalSongsList = mLocalSongsList
    }

    override fun getItemCount(): Int = if(mLocalSongsList!=null) mLocalSongsList!!.size else 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        mLocalSongsList?.let { list ->
            val songModel = list[position]
            holder.itemBinding.songItem = songModel

            holder.itemBinding.songTitleTv.isSelected = false
            holder.itemBinding.songArtistTv.isSelected = false

            //set view if currently playing song
            if(selectedSongIndex == position) {
                setPlayingSongView(holder.itemBinding)
            } else {
                setNotPlayingSongView(holder.itemBinding)
            }

            holder.itemBinding.mainViewCl.setOnClickListener {
                holder.itemBinding.songTitleTv.isSelected = true
                holder.itemBinding.songArtistTv.isSelected = true
                setPlayingSongView(holder.itemBinding)
                if(selectedSongIndex != position) {
                    notifyItemChanged(selectedSongIndex)
                    selectedSongIndex = position
                }
                callback?.selectedSongForPlaying(list[position])
            }
        }
    }

    private fun setNotPlayingSongView(itemBinding: LayoutSongItemBinding) {
        itemBinding.songTitleTv.setTypeface(null,Typeface.NORMAL)
        itemBinding.songArtistTv.setTypeface(null,Typeface.ITALIC)
        itemBinding.songArtistTv.setTextColor(mContext?.fetchColor(R.color.song_item_artist_text_color)!!)
        itemBinding.songPlayIv.setImageResource(R.drawable.ic_play_circle_48dp)
    }

    private fun setPlayingSongView(itemBinding: LayoutSongItemBinding) {
        itemBinding.songTitleTv.setTypeface(null,Typeface.BOLD)
        itemBinding.songArtistTv.setTypeface(null,Typeface.BOLD_ITALIC)
        itemBinding.songArtistTv.setTextColor(mContext?.fetchColor(R.color.white)!!)
        itemBinding.songPlayIv.setImageResource(R.drawable.ic_pause_circle_48dp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutSongItemBinding : LayoutSongItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_song_item,parent,false)
        return MyViewHolder(layoutSongItemBinding)
    }

    inner class MyViewHolder(val itemBinding: LayoutSongItemBinding) : RecyclerView.ViewHolder(itemBinding.root)

}