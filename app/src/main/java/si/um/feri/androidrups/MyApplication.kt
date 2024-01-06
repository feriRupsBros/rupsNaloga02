package si.um.feri.androidrups

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import si.um.feri.androidrups.data.Api
import si.um.feri.androidrups.data.DataAirPollution
import si.um.feri.androidrups.data.JWTData
import si.um.feri.androidrups.data.LoginRequest
import si.um.feri.androidrups.data.RegisterRequest
import si.um.feri.androidrups.data.RequestAddAirPollution
import timber.log.Timber
import java.util.Base64
import java.util.UUID

const val MY_SHARED_DATA = "myShared.data"

class MyApplication : Application() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var api: Api
    private var user: JWTData? = null
    private var jwtStr: String? = null

    private val _airPollutionData = MutableLiveData<List<DataAirPollution>>().apply { value = listOf() }
    val airPollutionData: LiveData<List<DataAirPollution>> = _airPollutionData

    override fun onCreate() {
        super.onCreate()

        // Start the Timber debug code
        Timber.plant(Timber.DebugTree())
        Timber.d("onCreate() MyApplication started!")

        // Start api
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        api = Api()

        // Start and initialize required data
        initShared()

        // Generate / Fetch unique application UUID
        if (!containsUUID()) {
            saveUUID(UUID.randomUUID().toString())
        }
        Timber.d("ID of app is ${getUUID()}")

        // Set the predefined application theme
        if (!containsChosenTheme()) {
            saveChosenTheme("0")
        }
        when (getChosenTheme()) {
            "0" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "1" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        // Set predefined user session setting
        if(!containsChosenUserSession()){
            saveChosenUserSession("1")
        }
        if(getChosenUserSession() == "1"){
            userLogout()
        }

        // Sync with API
        _airPollutionData.postValue(api.airPollution())
    }

    // Initialize sharedPref
    private fun initShared() {
        sharedPref = getSharedPreferences(MY_SHARED_DATA, Context.MODE_PRIVATE)
    }

    // UUID functions
    private fun getUUID(): String? {
        return sharedPref.getString("UUID", "-1")
    }
    private fun saveUUID(data: String) {
        with(sharedPref.edit()) {
            putString("UUID", data)
            apply()
        }
    }
    private fun containsUUID(): Boolean {
        return sharedPref.contains("UUID")
    }

    // Theme setting functions (0 : light) (1 : dark)
    fun setApplicationTheme(mode: String) {
        when (mode) {
            "0" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "1" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                Timber.e("Error changing theme")
                return
            }
        }
        saveChosenTheme(mode)
        Toast.makeText(this, resources.getString(R.string.setting_saved), Toast.LENGTH_SHORT).show()
    }
    fun getChosenTheme(): String? {
        return sharedPref.getString("theme", "-1")
    }
    private fun saveChosenTheme(data: String) {
        with(sharedPref.edit()) {
            putString("theme", data)
            apply()
        }
    }
    private fun containsChosenTheme(): Boolean {
        return sharedPref.contains("theme")
    }

    // User session functions
    fun setChosenUserSession(selected: String){
        saveChosenUserSession(selected)
        Toast.makeText(this, resources.getString(R.string.setting_saved), Toast.LENGTH_SHORT).show()
    }
    fun getChosenUserSession(): String? {
        return sharedPref.getString("userSession", "-1")
    }
    private fun saveChosenUserSession(data: String) {
        with(sharedPref.edit()) {
            putString("userSession", data)
            apply()
        }
    }
    private fun containsChosenUserSession() : Boolean {
        return sharedPref.contains("userSession")
    }

    // User functions
    fun userVerifyStatus(): Boolean {
        this.jwtStr = null
        this.user = null

        Timber.d("User Verify : ${sharedPref.getString("userUUID", "null")}")
        if (!sharedPref.contains("userUUID")) return false
        try {
            val jwtString = sharedPref.getString("userUUID", "")?: return false

            val jwt = JWT(jwtString)
            if(jwt.isExpired(60 * 60 * 24)) {
                // Expired
                with(sharedPref.edit()) {
                    remove("userUUID")
                    apply()
                }

                Timber.d("Verify User: Expired")
                return false
            }

            val dataString = jwtString.split('.')[1]
            val dataJSON: String = Base64.getUrlDecoder().decode(dataString).decodeToString()
            this.user = Gson().fromJson(dataJSON, JWTData::class.java)
            this.jwtStr = jwtString

            Timber.d("Verify User: ${this.user}")

            return true
        }
        catch(e: Exception) {
            Timber.d("Verify error: $e")
            return false
        }
    }
    fun userLogin(username: String, password: String): Boolean {
        if (username == "" || password == "") {
            Toast.makeText(this, resources.getString(R.string.missing_username_password), Toast.LENGTH_LONG).show()
            return false
        }

        Timber.d("User Login : $username / $password")

        val res = api.login(LoginRequest(username = username, password = password))
        if (res == null){
            Toast.makeText(this, resources.getString(R.string.incorrect_username_password), Toast.LENGTH_LONG).show()
            return false
        }

        with(sharedPref.edit()) {
            putString("userUUID", res.accessToken)
            apply()
        }

        Timber.d("User Login : Success")
        return true
    }
    fun userRegister(username: String, password: String, passwordRepeat: String): Boolean {
        if (username == "" || password == "" || passwordRepeat == "") {
            Toast.makeText(this, resources.getString(R.string.missing_username_password), Toast.LENGTH_LONG).show()
            return false
        }

        Timber.d("User Register : $username / $password / $passwordRepeat")

        if (password != passwordRepeat){
            Toast.makeText(this, resources.getString(R.string.passwords_match_error), Toast.LENGTH_LONG).show()
            return false
        }

        val res = api.register(RegisterRequest(username, password))
        if (res == null) {
            Toast.makeText(this, resources.getString(R.string.users_exists), Toast.LENGTH_LONG).show()
            return false
        }

        with(sharedPref.edit()) {
            putString("userUUID", res.accessToken)
            apply()
        }

        Timber.d("User Register : Success")
        return true
    }

    fun addAirPollution(airPollution: RequestAddAirPollution): DataAirPollution? {
        return api.addAirPollution(airPollution)
    }

    fun userLogout(){
        Timber.d("User Logout : ${sharedPref.getString("userUUID", "null")}")

        with(sharedPref.edit()){
            remove("userUUID")
            apply()
        }

        Timber.d("User Logout : Success")
    }

    fun getUsername(): String {
        return user!!.username
    }
}