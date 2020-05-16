package com.example.singmetoo.appSingMe2.mUtils.helpers

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val topSpace:Int,private val leftSpace:Int,private val spanCount:Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column: Int = position % spanCount

        outRect.right = if(column==1)  0 else leftSpace
        outRect.bottom = 0
        outRect.left = if(column==0)  0 else leftSpace
        outRect.top = if(position < spanCount) 0 else topSpace
    }

}