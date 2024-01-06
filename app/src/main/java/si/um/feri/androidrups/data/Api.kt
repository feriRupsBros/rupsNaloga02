package si.um.feri.androidrups.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import timber.log.Timber

class Api {
    private val client = OkHttpClient()
    private val url = "https://audioxd.ddns.net"
    private val gson = Gson()

    fun login(req: LoginRequest): LoginResponse? {
        val builder = Builder()
        builder.url("$url/user/login")
        builder.post(
            gson.toJson(req).toRequestBody("application/json; charset=utf-8".toMediaType())
        )
        val request: Request = builder.build()

        try {
            val response: Response = client.newCall(request).execute()
            if(response.code == 200) {
                val json: String = response.body?.string() ?: return null
                return gson.fromJson(json, LoginResponse::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.d("$e")
        }
        return null
    }

    fun register(req: RegisterRequest): RegisterResponse? {
        val builder = Builder()
        builder.url("$url/user/register")
        builder.post(
            gson.toJson(req).toRequestBody("application/json; charset=utf-8".toMediaType())
        )
        val request: Request = builder.build()

        try {
            val response: Response = client.newCall(request).execute()
            if(response.code == 200) {
                val json: String = response.body?.string() ?: return null
                return gson.fromJson(json, RegisterResponse::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun addAirPollution(req: RequestAddAirPollution): DataAirPollution? {
        val builder = Builder()
        builder.url("$url/air_pollution")
        builder.post(
            gson.toJson(req).toRequestBody("application/json; charset=utf-8".toMediaType())
        )
        val request: Request = builder.build()

        try {
            val response: Response = client.newCall(request).execute()
            if(response.code == 200) {
                val json: String = response.body?.string() ?: return null
                return gson.fromJson(json, DataAirPollution::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun airPollution(): List<DataAirPollution> {
        val builder = Builder()
        builder.url("$url/air_pollution")
        val request: Request = builder.build()

        try {
            val response: Response = client.newCall(request).execute()
            if(response.code == 200) {
                val json: String = response.body?.string() ?: return listOf()
                val listOfMyClassObject = object : TypeToken<ArrayList<DataAirPollution>>() {}.type
                return gson.fromJson(json, listOfMyClassObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }
}