package si.um.feri.androidrups.data

import java.io.Serializable

data class RequestAddAirPollution(
    val name: String,
    val region: String,
    val longtitude: Float,
    val latitude: Float,
    val source: String,
    val reliability: Float,
    val PM10: Float,
    val PM2_5: Float,
    val SO2: Float,
    val CO: Float,
    val O3: Float,
    val NO2: Float,
    val C6H6: Float,
    val measuring_start: String,
    val measuring_end: String
) : Serializable