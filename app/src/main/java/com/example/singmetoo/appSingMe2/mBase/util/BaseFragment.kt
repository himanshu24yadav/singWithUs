package com.example.singmetoo.appSingMe2.mBase.util

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.singmetoo.appSingMe2.mBase.interfaces.CommonBaseInterface
import com.example.singmetoo.appSingMe2.mBase.view.MainActivity
import com.example.singmetoo.appSingMe2.mLogin.AppUserInfo

open class BaseFragment : Fragment() {

    var mUserInfo: AppUserInfo = BaseActivity.mUserInfo
    var commonBaseInterface:CommonBaseInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        commonBaseInterface = context as? CommonBaseInterface
    }

    fun setDefaultToolbar(toShowNavigationIcon:Boolean) {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = ""
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = ""
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(toShowNavigationIcon)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayShowHomeEnabled(toShowNavigationIcon)
    }

    fun navigationDrawerListener() : View.OnClickListener {
        return View.OnClickListener { _ ->
            commonBaseInterface?.let {
                if(it.isDrawerOpen()) it.closeDrawer()
                else it.openDrawer()
            }
        }
    }

    fun activityFragmentManager() : FragmentManager?{
        return (activity as? MainActivity)?.supportFragmentManager
    }
}