package com.example.singmetoo.appSingMe2.mUtils.helpers

import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.singmetoo.R
import de.hdodenhof.circleimageview.CircleImageView

object BindingAdapterUtil {

    @JvmStatic
    @BindingAdapter(value = ["profileImageUrl","errorImage"], requireAll = false)
    fun setProfileImageUrl (circularImageView: CircleImageView, stringUrl:String?, errorDrawable: Drawable?){
        if(AppUtil.checkIsNotNull(stringUrl)){

        } else {
            errorDrawable?.let { circularImageView.setImageDrawable(it) }
        }
    }

    @JvmStatic
    @BindingAdapter("imageRes")
    fun setImageRes (imageView:ImageView?,resId:Int){
        imageView?.setImageResource(resId)
    }
}