package edu.nd.pmcburne.hello

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(locations: List<LocationObject> )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllTags(tags: List<LocationTag>)

    @Query("SELECT * FROM locations ORDER BY name ASC")
    fun getAllSortedByName(): Flow<List<LocationObject>>

    @Query("SELECT DISTINCT tag FROM location_tags ORDER BY tag ASC")
    fun getAllTags(): Flow<List<String>>

    @Query("SELECT l.* FROM locations l INNER JOIN location_tags t ON l.id = t.locationId WHERE t.tag = :tag ORDER BY l.name ASC")
    fun getLocationsByTag(tag: String): Flow<List<LocationObject>>
}
