package si.um.feri.androidrups.data

import java.io.Serializable

data class DataMeasurementTime(
    val _id: String,
    val measuring_start: String,
    val measuring_end: String
) : Serializable