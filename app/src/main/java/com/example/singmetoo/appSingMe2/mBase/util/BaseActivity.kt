package com.example.singmetoo.appSingMe2.mBase.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.singmetoo.CustomApplicationClass
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mLogin.AppUserInfo
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppConstants
import com.example.singmetoo.appSingMe2.mUtils.helpers.AppUtil
import com.example.singmetoo.appSingMe2.mUtils.helpers.fetchString
import com.example.singmetoo.permissionHelper.PermissionsManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

open class BaseActivity : AppCompatActivity() {
    companion object {
        var mUserInfo: AppUserInfo = AppUserInfo(userName = "User")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        PermissionsManager.handlePermissionResult(requestCode,permissions,grantResults)
    }




    // --------------------------- Signing Logic ---------------------------------------//

    val firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var userLoggedInLiveData: MutableLiveData<Boolean>? = MutableLiveData()
    var currentUser = firebaseAuth?.currentUser
    private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(CustomApplicationClass.applicationContext()?.fetchString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    var googleSignInClient: GoogleSignInClient? = GoogleSignIn.getClient(CustomApplicationClass.applicationContext()!!, gso)

    fun signInUser() {
        if (currentUser == null) {
            val signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, AppConstants.RC_SIGN_IN)
        } else {
            AppUtil.showToast(this, "Signed in failed")
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                currentUser = firebaseAuth.currentUser
                userLoggedInLiveData?.value = true
            } else {
                AppUtil.showToast(this, "Signed in failed")
            }
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
}