package com.example.singmetoo.appSingMe2.mUtils.helpers

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.databinding.LayoutDialogDetailBinding

class DialogDetailFragment : DialogFragment() {

    private var mContext: Context? = null
    private var mSongModel: SongModel? = null
    private lateinit var mLayoutBinding: LayoutDialogDetailBinding

    fun setData(songModel: SongModel?) {
        mSongModel = songModel
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.WRAP_CONTENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val window = dialog?.window
        window?.setLayout(width, height)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDetach() {
        super.onDetach()
        mContext = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val labelsDialog = super.onCreateDialog(savedInstanceState)
        if (labelsDialog.window != null) {
            labelsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            labelsDialog.window!!.setGravity(Gravity.CENTER)
            labelsDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            labelsDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        }
        return labelsDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.layout_dialog_detail, container, false)
        init()
        return mLayoutBinding.root
    }

    private fun init() {

        mLayoutBinding.dialogIv.setAlbumImage(AppUtil.getImageUriFromAlbum(mSongModel?.songAlbumId ?: -1))

        mLayoutBinding.songTitleTv.text = mSongModel?.songTitle ?: ""
        mLayoutBinding.songTitleTv.visibility = if(AppUtil.checkIsNotNull(mSongModel?.songTitle)) View.VISIBLE else View.GONE
        mLayoutBinding.songTitleTv.isSelected = true

        mLayoutBinding.songArtistTv.text = mSongModel?.songArtist ?: ""
        mLayoutBinding.songArtistTv.visibility = if(AppUtil.checkIsNotNull(mSongModel?.songArtist)) View.VISIBLE else View.GONE
        mLayoutBinding.songArtistTv.isSelected = true

        mLayoutBinding.songAlbumTv.text = mSongModel?.songAlbum ?: ""
        mLayoutBinding.songAlbumTv.visibility = if(AppUtil.checkIsNotNull(mSongModel?.songAlbum)) View.VISIBLE else View.GONE
        mLayoutBinding.songAlbumTv.isSelected = true
    }

}