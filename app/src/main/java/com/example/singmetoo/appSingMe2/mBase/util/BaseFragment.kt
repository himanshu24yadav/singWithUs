package com.example.singmetoo.appSingMe2.mBase.util

import androidx.fragment.app.Fragment
import com.example.singmetoo.appSingMe2.mBase.interfaces.CommonBaseInterface
import com.example.singmetoo.appSingMe2.mLogin.AppUserInfo

open class BaseFragment : Fragment() {
    var mUserInfo: AppUserInfo = BaseActivity.mUserInfo
    val commonBaseInterface:CommonBaseInterface? = activity as? CommonBaseInterface
}