package com.example.singmetoo.appSingMe2.mMusicLibrary.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel

class MusicLibraryAdapter(var mContext:Context?,var mLocalSongsList:ArrayList<SongModel>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int = if(mLocalSongsList!=null) mLocalSongsList!!.size else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}