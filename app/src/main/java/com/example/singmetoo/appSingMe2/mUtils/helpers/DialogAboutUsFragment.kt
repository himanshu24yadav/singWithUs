package com.example.singmetoo.appSingMe2.mUtils.helpers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mUtils.songsRepository.SongModel
import com.example.singmetoo.databinding.LayoutDialogDetailBinding

class DialogAboutUsFragment : DialogFragment() {

    private var mContext: Context? = null
    private lateinit var mLayoutBinding: LayoutDialogDetailBinding

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
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
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLayoutBinding.backIv.setOnClickListener {
            dismiss()
        }
    }
}