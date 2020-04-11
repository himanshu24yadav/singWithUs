package com.example.singmetoo.testing

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.singmetoo.R
import com.example.singmetoo.dbHelper.AppDatabase
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

//room integration with coroutine
class SampleRoomFragment : Fragment(),CoroutineScope{

    private var mContext:Context? = null
    private var rootView: View? = null
    private var userNameEt: EditText? = null
    private var userAddressEt: EditText? = null
    private var showDataBtn: Button? = null
    private var saveData: Button? = null
    private var showDataTv: TextView? = null
    private var appDatabase: AppDatabase? =null
    private var coroutineExceptionHandler:CoroutineExceptionHandler? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.sample_room_fragment_layout, container, false)

        initObj()
        initViews()
        initExceptionHandler()
        initListeners()

        return rootView
    }

    private fun initExceptionHandler() {
        coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Toast.makeText(mContext,"Error Occurred due to : ${throwable.message}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun initObj() {
        appDatabase = AppDatabase.getInstance(mContext)
    }

    private fun initViews() {
        userAddressEt = rootView?.findViewById(R.id.user_address)
        userNameEt = rootView?.findViewById(R.id.userName)
        showDataBtn = rootView?.findViewById(R.id.get_data_btn)
        saveData = rootView?.findViewById(R.id.insert_btn)
        showDataTv = rootView?.findViewById(R.id.show_data_tv)
    }

    private fun initListeners() {

        val dateStr = "29/03/1996"
        val currentFormatter = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
        val dateOfBirth: Date? = currentFormatter.parse(dateStr)

        saveData?.setOnClickListener {
            showDataBtn?.isEnabled = false
            val testingUser = TestingUserModel(userName = userNameEt?.text.toString().trim(),
                                               userAddress = userAddressEt?.text.toString().trim(),
                                               dob = dateOfBirth)

            saveUserDataToDB(testingUser)

        }

        showDataBtn?.setOnClickListener {
            showUserDataFromDB()
        }

    }

    private fun saveUserDataToDB(testingUser: TestingUserModel) {
        launch (SupervisorJob()) {
            withContext(Dispatchers.IO) {
                appDatabase?.daoTestingUser()?.insertUser(testingUser)
            }
            showDataBtn?.isEnabled = true
            userNameEt?.setText("")
            userAddressEt?.setText("")
            Toast.makeText(mContext,"Data inserted",Toast.LENGTH_SHORT).show()
            this.cancel()
        }
    }

    private fun showUserDataFromDB() {
        launch (SupervisorJob()) {
            var listOfUsers: List<TestingUserModel>? = null
            withContext(Dispatchers.IO) {
                 listOfUsers = appDatabase?.daoTestingUser()?.getTestingUsers()
            }

            var strToShow:String? = ""
            for (item in listOfUsers!!) {
                strToShow += " ${item.userName} "
            }
            showDataTv?.text = strToShow
            Toast.makeText(mContext,"Data fetched",Toast.LENGTH_SHORT).show()

            this.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job() + coroutineExceptionHandler!!
}