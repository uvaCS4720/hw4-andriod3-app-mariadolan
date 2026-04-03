package edu.nd.pmcburne.hello
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
data class MainUIState(
    val counterValue: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LocationDatabase.getInstance(application)
    private val dao = db.LocationDao()

    private val _selectedTag = MutableStateFlow("core")
    val selectedTag: StateFlow<String> = _selectedTag

    val allTags = dao.getAllTags()
    val filteredLocations = _selectedTag.flatMapLatest { tag -> dao.getLocationsByTag(tag) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val url = "https://www.cs.virginia.edu/~wxt4gm/placemarks.json"

    init {
        fetchLocations()
    }

    fun onTagSelected(tag: String) {
        _selectedTag.value = tag
    }


    fun fetchLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonString = response.body?.string() ?: return@launch
                    val (locations, tags) = parseLocations(jsonString)
                    dao.insertAll(locations)
                    dao.insertAllTags(tags)

                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Exception: ${e.message}")
            }
        }
    }

    fun parseLocations(jsonString: String):Pair<List<LocationObject>, List<LocationTag>>  {
        val locationsArray = JSONArray(jsonString)
        val locationsList = mutableListOf<LocationObject>()
        val tagsList = mutableListOf<LocationTag>()

        for (i in 0 until locationsArray.length()){
            val location = locationsArray.getJSONObject(i)
            val id = location.getInt("id")
            val name = location.getString("name")
            val description = location.getString("description")
            val visual_center = location.getJSONObject("visual_center")
            val latitude = visual_center.getDouble("latitude")
            val longitude = visual_center.getDouble("longitude")

            locationsList.add(LocationObject(id, name, description, longitude, latitude))

            val tagArray = location.getJSONArray("tag_list")
            for( j in 0 until tagArray.length() ){
                tagsList.add(LocationTag(id, tagArray.getString(j)))
            }
        }

        return Pair(locationsList, tagsList)
    }
}
