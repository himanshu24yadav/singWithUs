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
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder


class FrescoHelper {
    companion object {

        fun getDraweeController(imageUri: String?,simpleDraweeView: SimpleDraweeView?) : DraweeController? {
            val request: ImageRequest? = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUri)).build()
            return request.let {
                Fresco.newDraweeControllerBuilder()
                .setImageRequest(it)
                .setOldController(simpleDraweeView?.controller).build()
            }
        }

        fun getResizedDraweeController(imageUri: String?,simpleDraweeView: SimpleDraweeView?,resizeOptions: ResizeOptions?) : DraweeController? {
            val request: ImageRequest? = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUri)).setResizeOptions(resizeOptions).build()
            return request.let {
                Fresco.newDraweeControllerBuilder()
                    .setImageRequest(it)
                    .setOldController(simpleDraweeView?.controller).build()
            }
        }

        fun getGenericHierarchyBuilderForSongItemRounded(context: Context,actualScaleType:ScalingUtils.ScaleType = ScalingUtils.ScaleType.CENTER_CROP): GenericDraweeHierarchy? {
            val roundingParams:RoundingParams? = RoundingParams.asCircle().setCornersRadius(2f)
            return GenericDraweeHierarchyBuilder(context.resources)
                .setPlaceholderImage(R.drawable.bg_default_playing_song, ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(actualScaleType)
                .setFailureImage(R.drawable.bg_default_playing_song)
                .setRoundingParams(roundingParams)
                .build()
        }

        fun getGenericHierarchyBuilderForSongItem(context: Context): GenericDraweeHierarchy? {
            return GenericDraweeHierarchyBuilder(context.resources)
                .setPlaceholderImage(R.drawable.bg_default_playing_song, ScalingUtils.ScaleType.CENTER_CROP)
                .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                .setFailureImage(R.drawable.bg_default_playing_song)
                .build()
        }

        fun getBitmapFromImagePath (imagePath:String? = null,
                                    resId: Int? = null,
                                    resizeOptions: ResizeOptions? = ResizeOptions(250,250),
                                    frescoLoadBitmapCallback: FrescoLoadBitmapCallback) {
            try {
                val imageRequest = when {
                    imagePath != null -> {
                        ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath)).setResizeOptions(resizeOptions).build()
                    }
                    resId != null -> {
                        ImageRequestBuilder.newBuilderWithResourceId(resId).setResizeOptions(resizeOptions).build()
                    }
                    else -> {
                        ImageRequestBuilder.newBuilderWithResourceId(R.drawable.bg_default_playing_song).setResizeOptions(resizeOptions).build()
                    }
                }
                val imagePipeline = Fresco.getImagePipeline()
                val dataSource = imagePipeline.fetchDecodedImage(imageRequest, this)
                dataSource.subscribe(object : BaseBitmapDataSubscriber() {
                    override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage?>>) {
                        frescoLoadBitmapCallback.onBitmapLoadFailure(dataSource)
                        dataSource.close()
                    }

                    override fun onNewResultImpl(bitmap: Bitmap?) {
                        try {
                            if (dataSource.isFinished && bitmap != null) {
                                frescoLoadBitmapCallback.onBitmapLoadSuccess(bitmap)
                                dataSource.close()
                            }
                        } catch (e: Exception) {
                            frescoLoadBitmapCallback.onBitmapLoadFailure(null)
                            e.printStackTrace()
                        }
                    }
                }, CallerThreadExecutor.getInstance())
            } catch (e: Exception) {
                frescoLoadBitmapCallback.onBitmapLoadFailure(null)
                e.printStackTrace()
            }
        }
    }
}