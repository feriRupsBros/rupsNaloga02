package si.um.feri.androidrups.data

import java.io.Serializable

data class DataAirPollution(
    val _id: String,
    val name: String,
    val region: String,
    val geo: DataGeoLocation,
    val source: String,
    val reliability: Float,
    val concentrations: DataConcentrations,
    val date_time_of_measurement: DataMeasurementTime,
    val dateTime: String
) : Serializable