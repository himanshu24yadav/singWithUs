package com.example.singmetoo.appSingMe2.mLogin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mBase.util.BaseActivity
import com.example.singmetoo.appSingMe2.mBase.view.MainActivity
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppConstants
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppUtil
import com.example.singmetoo.appSingMe2.mUtils.helpers.SharedPrefHelper
import com.example.singmetoo.appSingMe2.mUtils.helpers.fetchString
import com.example.singmetoo.databinding.LoginLayoutActivityBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : BaseActivity() {

    private lateinit var mLayoutBinding: LoginLayoutActivityBinding
    private val tag: String = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.login_layout_activity)

        initViews()
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        currentUser = firebaseAuth?.currentUser
        updateUI()
    }

    private fun initViews() {
        mLayoutBinding.getStartedTv.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.anim_fade_in
            )
        )
        mLayoutBinding.baseAppIcon.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.anim_fade_in
            )
        )
    }

    private fun initListeners() {
        mLayoutBinding.googleSignBtn.setOnClickListener {
            signInUser()
        }

        userLoggedInLiveData?.observe(this, Observer { success ->
            if(success) updateUI()
        })

        mLayoutBinding.skipText.setOnClickListener {
            moveToMainActivity()
            SharedPrefHelper.storeSharedPref(SharedPrefHelper.SF_KEY_SKIP_LOGIN,true)
        }
    }

    private fun updateUI() {
        if (currentUser != null) {
            //AppUtil.showToast(this, "signed in with ${currentUser!!.email}")
            storeUserInfoDetails()
            moveToMainActivity()
        } else if(SharedPrefHelper.getSharedPref(SharedPrefHelper.SF_KEY_SKIP_LOGIN,false) as Boolean) {
            moveToMainActivity()
        }
    }

    private fun storeUserInfoDetails() {
        mUserInfo.apply {
            userName = currentUser?.displayName
            userEmail = currentUser?.email
            userId = currentUser?.uid
            isUserLoggedIn = true
        }
    }

    private fun moveToMainActivity() {
        mLayoutBinding.loginOptionsGroup.visibility = View.INVISIBLE
        mLayoutBinding.progress.visibility = View.VISIBLE
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }, 1000)
    }
}