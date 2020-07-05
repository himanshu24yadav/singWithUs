package com.example.singmetoo.appSingMe2.mHome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mHome.interfaces.HomeItemsInterface
import com.example.singmetoo.appSingMe2.mHome.pojo.HomeContentModel
import com.example.singmetoo.appSingMe2.mUtils.helpers.fetchString
import com.example.singmetoo.databinding.LayoutHomeItemBinding

class HomeAdapter(private var context: Context?, private var contentList:ArrayList<HomeContentModel>?, private val homeItemsInterface: HomeItemsInterface?) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    fun updateData(contentList: ArrayList<HomeContentModel>?){
        this.contentList = contentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val homeItemBinding : LayoutHomeItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.layout_home_item,parent,false)
        return MyViewHolder(homeItemBinding)
    }

    override fun getItemCount(): Int = if(contentList!=null) contentList!!.size else 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        contentList?.let {
            holder.itemBinding.itemData = it[position]
            holder.itemBinding.listener = homeItemsInterface
            val aboutUsTitle = context?.fetchString(R.string.home_item_about_us) ?: ""
            holder.itemBinding.playIcon.visibility = if(aboutUsTitle == it[position].title) View.GONE else View.VISIBLE
        }
    }

    inner class MyViewHolder(val itemBinding: LayoutHomeItemBinding) : RecyclerView.ViewHolder(itemBinding.root)
}