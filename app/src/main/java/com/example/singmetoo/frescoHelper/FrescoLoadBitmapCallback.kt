package com.example.singmetoo.frescoHelper

import android.graphics.Bitmap
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.imagepipeline.image.CloseableImage

interface FrescoLoadBitmapCallback {
    fun onBitmapLoadSuccess (bitmap: Bitmap?)
    fun onBitmapLoadFailure (dataSource: DataSource<CloseableReference<CloseableImage?>>?)
}