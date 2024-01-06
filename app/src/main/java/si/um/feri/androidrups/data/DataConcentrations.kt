package si.um.feri.androidrups.data

import java.io.Serializable

data class DataConcentrations(
    val _id: String,
    val PM10: Float,
    val PM2_5: Float,
    val SO2: Float,
    val CO: Float,
    val O3: Float,
    val NO2: Float,
    val C6H6: Float
) : Serializable