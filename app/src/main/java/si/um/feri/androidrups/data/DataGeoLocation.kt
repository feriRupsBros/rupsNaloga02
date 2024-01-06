package si.um.feri.androidrups.data

import java.io.Serializable

data class DataGeoLocation(val _id: String, val coordinates: FloatArray) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataGeoLocation

        if (_id != other._id) return false
        return coordinates.contentEquals(other.coordinates)
    }

    override fun hashCode(): Int {
        var result = _id.hashCode()
        result = 31 * result + coordinates.contentHashCode()
        return result
    }
}