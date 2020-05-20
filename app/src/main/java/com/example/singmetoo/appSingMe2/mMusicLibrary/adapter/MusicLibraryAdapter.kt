package com.example.singmetoo.appSingMe2.mMusicLibrary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mHome.pojo.HomeContentModel
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.databinding.LayoutHomeItemBinding
import com.example.singmetoo.databinding.LayoutSongItemBinding

class MusicLibraryAdapter(var mContext:Context?, private var mLocalSongsList:ArrayList<SongModel>?) : RecyclerView.Adapter<MusicLibraryAdapter.MyViewHolder>() {

    fun updateData(mLocalSongsList: ArrayList<SongModel>?){
        this.mLocalSongsList = mLocalSongsList
    }

    override fun getItemCount(): Int = if(mLocalSongsList!=null) mLocalSongsList!!.size else 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        mLocalSongsList?.let {
            holder.itemBinding.songItem = mLocalSongsList?.get(position)
            holder.itemBinding.songArtistTv.isSelected = true
            holder.itemBinding.songTitleTv.isSelected = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutSongItemBinding : LayoutSongItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_song_item,parent,false)
        return MyViewHolder(layoutSongItemBinding)
    }

    inner class MyViewHolder(val itemBinding: LayoutSongItemBinding) : RecyclerView.ViewHolder(itemBinding.root)

}