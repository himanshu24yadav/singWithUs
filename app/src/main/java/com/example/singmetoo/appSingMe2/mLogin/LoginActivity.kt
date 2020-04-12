package com.example.singmetoo.appSingMe2.mLogin

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.singmetoo.R
import com.example.singmetoo.databinding.LoginLayoutActivityBinding

class LoginActivity : AppCompatActivity(){

    private lateinit var mLayoutBinding: LoginLayoutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding = DataBindingUtil.setContentView(this,R.layout.login_layout_activity)

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