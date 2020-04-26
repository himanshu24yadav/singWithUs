package com.example.singmetoo.appSingMe2.mHome.interfaces

import android.view.View
import com.example.singmetoo.appSingMe2.mHome.pojo.HomeContentModel

interface HomeItemsInterface {
    fun openSelectedScreen(view:View?,itemModel:HomeContentModel?)
}