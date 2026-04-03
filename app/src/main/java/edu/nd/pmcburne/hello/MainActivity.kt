package edu.nd.pmcburne.hello

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import edu.nd.pmcburne.hello.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme() {
                Screen(viewModel)
            }
        }
    }
}

@Composable
fun Screen(viewModel: MainViewModel){
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().background(Color(0xFF232D4B)).padding(16.dp)){
        Text(
            text = "UVA Landmarks Map",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFFE57200),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        DropDown(viewModel)
        Spacer(modifier = Modifier.height(8.dp))
        Map(modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)), viewModel = viewModel)

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown (viewModel: MainViewModel){
    var expanded by remember { mutableStateOf(false) }
    val tags by viewModel.allTags.collectAsState(initial = emptyList())
    val selectedTag by viewModel.selectedTag.collectAsState()

    ExposedDropdownMenuBox(expanded, onExpandedChange = {expanded = it}){
        TextField(
            value = selectedTag,
            readOnly = true ,
            label = {Text("Select A Location Tag From The Dropdown Menu")},
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable))
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = {expanded = false}) {
            for (tag in tags){
                DropdownMenuItem(
                    text = {Text(tag)},
                    onClick = {
                        viewModel.onTagSelected(tag)
                        expanded = false}
                )

            }
        }
    }

}

@Composable
fun Map (viewModel: MainViewModel, modifier: Modifier = Modifier){
    val locations by viewModel.filteredLocations.collectAsState()
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition(LatLng(38.033554,-78.507980), 15f,  0f, 0f)}
    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState){
        for(location in locations){
            Marker(
                state = MarkerState(
                    LatLng(location.latitude, location.longitude)),
                title = location.name,
                snippet = location.description
            )
        }
    }

}




