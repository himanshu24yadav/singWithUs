package com.example.singmetoo.appSingMe2.mUtils

import androidx.databinding.BindingAdapter
import de.hdodenhof.circleimageview.CircleImageView

object BindingAdapterUtil {

    @JvmStatic
    @BindingAdapter("profileImageUrl")
    fun setProfileImageUrl (circularImageView:CircleImageView,stringUrl:String){
        if(AppUtil.checkIsNotNull(stringUrl)){

        } else {

        }
    }
}