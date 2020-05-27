package com.example.singmetoo.frescoHelper

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.singmetoo.R
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.interfaces.DraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder


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

        fun getBitmapFromImagePath (imagePath:String?) : Bitmap? {
            var bitmapForImage:Bitmap? = null
            try {
                val imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath)).build()
                val imagePipeline = Fresco.getImagePipeline()
                val dataSource = imagePipeline.fetchDecodedImage(imageRequest, this)
                dataSource.subscribe(object : BaseBitmapDataSubscriber() {
                    override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage?>>) {
                        dataSource.close()
                    }

                    override fun onNewResultImpl(bitmap: Bitmap?) {
                        try {
                            if (dataSource.isFinished && bitmap != null) {
                                bitmapForImage = bitmap
                                dataSource.close()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }, CallerThreadExecutor.getInstance())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bitmapForImage
        }
    }
}