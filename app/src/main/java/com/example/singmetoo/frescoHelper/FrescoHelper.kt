package com.example.singmetoo.frescoHelper

import android.content.Context
import com.example.singmetoo.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest


class FrescoHelper {
    companion object {

        fun getDraweeController(imageUri: String?,simpleDraweeView: SimpleDraweeView?) : DraweeController? {
            val request: ImageRequest? = ImageRequest.fromUri(imageUri)
            return request.let {
                Fresco.newDraweeControllerBuilder()
                .setImageRequest(it)
                .setOldController(simpleDraweeView?.controller).build()
            }
        }

        fun getGenericHierarchyBuilderForSongItem(context: Context): GenericDraweeHierarchy? {
            val roundingParams:RoundingParams? = RoundingParams.asCircle().setCornersRadius(2f)
            return GenericDraweeHierarchyBuilder(context.resources)
                .setPlaceholderImage(R.drawable.bg_default_playing_song, ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setFailureImage(R.drawable.bg_default_playing_song)
                .setRoundingParams(roundingParams)
                .build()
        }
    }
}