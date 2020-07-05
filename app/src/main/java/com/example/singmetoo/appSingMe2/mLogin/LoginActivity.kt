package com.example.singmetoo.appSingMe2.mLogin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
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
    private var googleSignInClient: GoogleSignInClient? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private val tag: String = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.login_layout_activity)

        initObjects()
        initViews()
        initListeners()
    }

    override fun onStart() {
        super.onStart()
        currentUser = firebaseAuth?.currentUser
        updateUI()
    }

    private fun initObjects() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(this.fetchString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()
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
            if (currentUser == null) {
                val signInIntent = googleSignInClient?.signInIntent
                startActivityForResult(signInIntent, AppConstants.RC_SIGN_IN)
            } else {
                AppUtil.showToast(this, "Signed in failed")
            }
        }

        mLayoutBinding.skipText.setOnClickListener {
            moveToMainActivity()
            SharedPrefHelper.storeSharedPref(SharedPrefHelper.SF_KEY_SKIP_LOGIN,true)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AppConstants.RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    AppUtil.showToast(this, "Signed in failed")
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                currentUser = firebaseAuth?.currentUser
                updateUI()
            } else {
                AppUtil.showToast(this, "Signed in failed")
            }
        }
    }

    private fun updateUI() {
        if (currentUser != null) {
            AppUtil.showToast(this, "signed in with ${currentUser!!.email}")
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
            startActivity(intent)
        }, 2000)
    }
}