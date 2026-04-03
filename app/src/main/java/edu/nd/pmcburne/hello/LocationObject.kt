package edu.nd.pmcburne.hello


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="locations")
data class LocationObject(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String,
    val longitude: Double,
    val latitude: Double
)
@Entity(tableName = "location_tags", primaryKeys = ["locationId", "tag"])
data class LocationTag(
    val locationId: Int,
    val tag: String
)

