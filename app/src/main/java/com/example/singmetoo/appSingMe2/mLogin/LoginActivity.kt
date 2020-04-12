package com.example.singmetoo.appSingMe2.mLogin

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.singmetoo.R
import com.example.singmetoo.databinding.LayoutLoginScreenBinding

class LoginActivity : AppCompatActivity(){

    private lateinit var mLayoutBinding: LayoutLoginScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding = DataBindingUtil.setContentView(this,R.layout.layout_login_screen)

        initObjects()
        initViews()
        initListeners()
    }

    private fun initViews() {
        mLayoutBinding.getStartedTv.startAnimation(AnimationUtils.loadAnimation(this,R.anim.anim_fade_in))
    }

    private fun initListeners() {

    }

    private fun initObjects() {

    }
}