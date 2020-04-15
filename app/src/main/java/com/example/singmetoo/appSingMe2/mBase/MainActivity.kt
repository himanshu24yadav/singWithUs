package com.example.singmetoo.appSingMe2.mBase

import android.os.Bundle
import com.example.singmetoo.R
import com.example.singmetoo.appSingMe2.mUtils.AppUtil

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        initToolbar()
    }

    private fun initToolbar() {
        title = "Hi ${AppUtil.getFirstName(mUserInfo.userName)}"
    }
}
