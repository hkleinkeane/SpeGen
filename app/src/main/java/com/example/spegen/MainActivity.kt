package com.example.spegen

import android.content.res.Configuration
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import coil3.request.ImageRequest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import kotlin.collections.find
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.geometry.Offset


// Text box text variable
var text: String = ""

// Shared secret which is used for calling for an access token
const val CLIENT_SECRET = "d65234627cc790cba662f6b3"

// Access token that is used for calling to the API
var accesstoken = ""

// These are all the variables that update whenever an image is called based off of its properties to allow for global use.
var id = 0
var symbol_key = ""
var name = ""
var locale = ""
var license = ""
var license_url = ""
var author = ""
var author_url = ""
var source_url: String? = ""
var skins = false
var repo_key = ""
var hc = false
var extension = ""
var image_url = ""
var search_string: String? = ""
var unsafe_result = false
var _href = ""
var details_url = ""

// Variable that will update if the image is not found or is empty. Used in the LoadImages function.
var empty = false

// Screen height and width variables as determined by GetScreenDimensions()
var screenHeight = 0.dp
var screenWidth = 0.dp

// Ensures that the function that manages image display is always ran through changing this value when OpenSymbolsButton is clicked
var alternate = false

// See above, only this one is for SymbolsButtonExec
var alternate_button = false

// Amount of images that should be displayed on screen when calling for images
var display_images = 8

// Is the device in landscape?
var isLandscape = false

var image_names = mutableListOf("")

var image_urls = mutableListOf("")

var image_number = 0

var maxItems = display_images

val paddingDividend = 50

var static_row_height = 0.dp

var button_boxes_width = 0.dp

val home = menutemplate(1, "Menu", 1, listOf("My", "i", "see", "dog", "moose", "1", "2", "3", "4", "5", "6", "i"), listOf(2, false, false, false, false, false, false, false, false, false, false, false), listOf(false, 1,1,1,1,1,1,1,1,1,1,1, false), listOf(false, true, true, true, true, true, true, true, true, true, true, true, false))

val my = menutemplate(2, "My", 1, listOf("I", "I", "me", "mine", "eye", "1", "2", "10", "4", "5", "6", "1938", "i", "god"), listOf(1, false, false, false, false, false, false, false, false, false, false, false, false, 1), listOf(false, 1,1,1,1,1,1,1,1,1,1,1,1, false), listOf(false, true, true, true, true, true, true, true, true, true, true, true, true, false))

var MenuList = listOf<menutemplate>(home, my)

var box_size = 100.dp

var box_padding = 20.dp

var menu_height = (screenHeight - static_row_height - static_row_height - static_row_height - static_row_height)

var menu_width = screenWidth - (button_boxes_width * 2)

var selected_symbols = mutableStateListOf<String>()

var tts: MutableState<TextToSpeech?> = mutableStateOf(null)

var wordfinder_display = mutableIntStateOf(0)

var wordfinder_display_buttonguide = mutableIntStateOf(0)

var switchmenuparser = mutableStateOf(0)

var linked_menu = mutableStateOf(0)

var modifier_picker: Modifier = Modifier

var menukeylist = mutableListOf<Int>()

var wordfinder_path_ids = mutableStateListOf<Int>()

var wordfinder_path_names = mutableStateListOf<String>()

var createclonefolder = mutableStateOf(false)

var createclonesymbol = mutableStateOf(false)

var wordfinder_target_is_symbol = false

// Text padding for folder/symbol names. Minimum should be 5 dp or else issues will occur
var item_text_padding = 5.dp

val item_positions = mutableStateMapOf<String, Offset>()




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val a = remember { mutableIntStateOf(0) }
            MenuKeyGen()
            Screen()
            if (switchmenuparser.value > 0 && (wordfinder_display.intValue == a.intValue)) {
                Column(modifier = modifier_picker) {
                    MenuParser(MenuFinder(linked_menu.value))
                }
            }
            Box()
            {
                if (createclonefolder.value) {
                    if (wordfinder_path_ids.isNotEmpty() && wordfinder_path_names.isNotEmpty()) {
                        var index = 0
                        for (i in 0 until (MenuFinder(wordfinder_path_ids[0]).item_list.size)) {
                            if (wordfinder_path_names.size > 1) {
                                if (MenuFinder(wordfinder_path_ids[0]).item_list[i] == wordfinder_path_names[1] && !MenuFinder(
                                        wordfinder_path_ids[0]
                                    ).item_type[i]
                                ) {
                                    index = i
                                }
                            } else {
                                if (MenuFinder(wordfinder_path_ids[0]).item_list[i] == wordfinder_path_names[0] && !MenuFinder(
                                        wordfinder_path_ids[0]
                                    ).item_type[i]
                                ) {
                                    index = i
                                }
                            }
                        }
                        if (!MenuFinder(wordfinder_path_ids[0]).item_type[index]) {
                            var total_box_size = box_size + (box_padding * 2)
                            val itemKey = "${wordfinder_path_ids[0]}-$index"
                            val captured = item_positions[itemKey]
                            val density = LocalDensity.current
                            val x_offset = captured?.let { with(density) { it.x.toDp() } } ?: 0.dp
                            val y_offset = captured?.let { with(density) { it.y.toDp() } } ?: (button_boxes_width * 2)
                            val menu = MenuFinder(wordfinder_path_ids[0])
                            val folder_name = menu.item_list[index]
                            var folder_image_url by remember { mutableStateOf("") }
                            val folder_menu = menu.pointers[index]
                            val vertical_stretch =
                                ((menu_height) - ((((menu_height) / (total_box_size)).toInt()) * total_box_size))
                            LaunchedEffect(folder_name) {
                                val res = useApiWithToken(accesstoken, folder_name)
                                folder_image_url = res?.image_url ?: ""
                            }
                            Surface(color = Color.Transparent) {
                                Folder(
                                    folder_name,
                                    folder_image_url,
                                    folder_menu as Int,
                                    vertical_stretch,
                                    x_offset,
                                    y_offset,
                                    Modifier.zIndex(100f)
                                )
                            }
                        }
                    }
                }
                if (createclonesymbol.value) {
                    var index = 0
                    val lookupName = if (wordfinder_path_names.size > 1)
                        wordfinder_path_names[1]
                    else
                        wordfinder_path_names[0]
                    for (i in 0 until (MenuFinder(wordfinder_path_ids[0]).item_list.size)) {
                        if (MenuFinder(wordfinder_path_ids[0]).item_list[i] == lookupName && MenuFinder(
                                wordfinder_path_ids[0]
                        ).item_type[i]
                            ) {
                            index = i
                        }
                    }
                    if (MenuFinder(wordfinder_path_ids[0]).item_type[index]) {
                        var total_box_size = box_size + (box_padding * 2)
                        val itemKey = "${wordfinder_path_ids[0]}-$index"
                        val captured = item_positions[itemKey]
                        val density = LocalDensity.current
                        val x_offset = captured?.let { with(density) { it.x.toDp() } } ?: 0.dp
                        val y_offset = captured?.let { with(density) { it.y.toDp() } } ?: (button_boxes_width * 2)
                        val menu = MenuFinder(wordfinder_path_ids[0])
                        val symbol_name = menu.item_list[index]
                        var symbol_image_url by remember { mutableStateOf("") }
                        val vertical_stretch =
                            ((menu_height) - ((((menu_height) / (total_box_size)).toInt()) * total_box_size))
                        val tts_type = menu.tts[index] as Int
                        LaunchedEffect(symbol_name) {
                            val res = useApiWithToken(accesstoken, symbol_name)
                            symbol_image_url = res?.image_url ?: ""
                        }
                        Surface(color = Color.Transparent) {
                            Symbol(
                                symbol_name,
                                symbol_image_url,
                                vertical_stretch,
                                tts_type,
                                x_offset,
                                y_offset,
                                Modifier.zIndex(100f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GetScreenDimensions() {
    // Function that gets the dimensions of the screen for later use in UI scaling
    var configuration = LocalConfiguration.current
    screenWidth = configuration.screenWidthDp.dp
    screenHeight = configuration.screenHeightDp.dp
    configuration = LocalConfiguration.current
    isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
fun rememberTextToSpeech(): MutableState<TextToSpeech?> {
    // Handles TTS and its properties
    val context = LocalContext.current
    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale.US
            }
        }

        tts.value = textToSpeech

        onDispose {
            textToSpeech.stop()
        }
    }
    return tts
}

data class AccessTokenResponse(
    // Data class for getAccessToken to allow to parse the response data
    val access_token: String,
    val expires_in: Long
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class ApiSymbolResponse(
    // Data class for useApiWithToken to allow to parse the response data
    val id: Int,
    val symbol_key: String,
    val name: String,
    val locale: String,
    val license: String,
    val license_url: String,
    val author: String,
    val author_url: String,
    val source_url: String? = null,
    val skins: Boolean? = false,
    val repo_key: String,
    val hc: Boolean? = false,
    val extension: String,
    val image_url: String,
    val search_string: String? = null,
    val unsafe_result: Boolean,
    val _href: String,
    val details_url: String
)

suspend fun getAccessToken(): AccessTokenResponse? {
    // Gets a new access token using the shared secret
    return withContext(Dispatchers.IO) {
        val params = listOf(
            "secret" to CLIENT_SECRET
        )
        val (_, _, result) = Fuel.post("https://www.opensymbols.org/api/v2/token", params)
            .responseObject<AccessTokenResponse>()

        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                println("Failed to get access token: ${ex.message}")
                null
            }

            is Result.Success -> {
                val tokenResponse = result.get()
                accesstoken = tokenResponse.access_token
                tokenResponse
            }
        }
    }
}


suspend fun useApiWithToken(token: String?, search: String): ApiSymbolResponse? {
    return withContext(Dispatchers.IO) {
        val params = listOf(
            "q" to search,
            "locale" to "en",
            "safe" to "0",
            "access_token" to token
        )

        val (_, _, result) = Fuel.get("https://www.opensymbols.org/api/v2/symbols", params)
            .responseString()

        when (result) {
            is Result.Failure -> {
                println("API call failed: ${result.getException().message}")
                null
            }
            is Result.Success -> {
                var symbolstring = (result.get()).replace("[", "").replace("]", "").split("},")[0]

                if (symbolstring.length > 1) {
                    symbolstring += "}"
                }

                if (symbolstring.contains("}")) {
                    // Clean up the string to ensure valid JSON for a single object
                    symbolstring = symbolstring.dropLast(symbolstring.count { it == '}' } - 1)

                    // 3. Return the decoded object
                    Json.decodeFromString<ApiSymbolResponse>(symbolstring)
                } else {
                    null // Return null if no valid symbol found
                }
            }
        }
    }
}



// Function that creates the static row of always accessible words at the bottom of the screen for easy access with for loop that allows for customization through variables
@Composable
fun Static_Row_Needs() {
    tts = rememberTextToSpeech()
    val static_terms: MutableList<String> = mutableListOf("Yes", "No", "Food", "Water", "I need my parent", "I use a talker to communicate")
    var text_color = Color.Black // Set as var to be able to be customized by user later
    var text_alignment = Alignment.Center // Set as var to be able to be customized by user later
    var box_color = Color.White // Set as var to be able to be customized by user later
    var border_size = 2.dp // Set as var to be able to be customized by user later
    var border_color = Color.Black // Set as var to be able to be customized by user later
    var width = (screenWidth/static_terms.size.dp).dp // Determine width of boxes by dividing screen width by total number of boxes which is equal to number of needed terms
    static_row_height = (screenHeight.value*((70.dp/screenHeight).dp).value).dp // Fraction determined by base value of 70.dp then converted to fraction and applied to screen height to (hopefully) make box height scale with screen height
    var y_offset = (screenHeight-static_row_height) // Determines Y offset by subtracting height from the total screen width
    var x_offset = (0).dp // Determines X offset. Not needed since the first box starts at the left edge of the screen.
    for (i in 0 until static_terms.size) // For loop to create modular number of boxes. Starts at zero due to X offset calculations and ends at the number of terms minus 1 since it starts at zero
        Column() {
            val text = static_terms[i]
            Box(
                // FIX Y OFFSET
                modifier = Modifier
                    .offset((x_offset+(width*i)), y_offset)
                    .width(width)
                    .height(static_row_height)
                    .background(color = box_color)
                    .border(border = BorderStroke(border_size, border_color))
                    .clickable(onClick = {
                        if (tts.value?.isSpeaking == true) {
                            tts.value?.stop()
                        } else tts.value?.speak(
                            text, TextToSpeech.QUEUE_FLUSH, null, ""
                        )
                    })
            ) {
                Text(text = static_terms[i], color = text_color, modifier = Modifier.align(text_alignment))
            }
        }
}

@Composable
fun InputBox(modifier: Modifier) {
    val tts = rememberTextToSpeech()

    LaunchedEffect(Unit) {
        getAccessToken()
    }

    Row {
        LazyRow(
            modifier = modifier
                .width(screenWidth - (button_boxes_width * 2))
                .height(button_boxes_width * 2)
                .background(Color.White)
                .border(4.dp, Color.Black)
                .clickable {
                    val speech = selected_symbols.joinToString(" ")
                    tts.value?.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "")
                }
        ) {
            // This condition ensures nothing is shown until the loop finishes
            items(selected_symbols.size) { index ->
                InputBox_Symbol(index)
            }

        }
    }
}

@Composable
fun InputBox_Symbol(index: Int) {

    var name by remember {mutableStateOf("")}
    var url by remember {mutableStateOf("")}
    LaunchedEffect(selected_symbols) {
        val res = useApiWithToken(accesstoken, selected_symbols[index])
        name = res?.name ?: ""
        url = res?.image_url ?: ""
    }

    name = name.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase()
        else it.toString()
    }
    var height_dp = 16
    var width_dp = height_dp * 3.0625
    Box {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .build(),
            "Picture of $name",
            modifier = Modifier
                .background(Color.White)
                .padding(box_padding)
                .scale(1f)
                .size(box_size)
        )
        Text(
            text = name,
            color = Color.Black,
            modifier = Modifier.padding(2.dp).height(height_dp.dp).width(width_dp.dp)
                .align(Alignment.BottomCenter),
            textAlign = TextAlign.Center)
    }
}

@Composable
@NonSkippableComposable
fun Symbol(Name: String, image_url: String, Vertical_Stretch: Dp, tts_type: Int, x_offset: Dp = 0.dp, y_offset: Dp = 0.dp, modifier: Modifier = Modifier) {
    val name = Name.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase()
        else it.toString() }
    var height_dp = 16
    var width_dp = height_dp*3.0625
    tts = rememberTextToSpeech()
    Box(modifier = modifier)  {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image_url)
                .build(),
            "Picture of $Name",
            modifier = modifier
                .offset(x_offset, y_offset)
                .height(box_size+Vertical_Stretch+(box_padding*3))
                .background(Color.White)
                .border(width = 4.dp, color = Color.Black, shape = RoundedCornerShape(40.dp))
                .padding(box_padding)
                .scale(1f)
                .width(box_size)
                .clickable(onClick = {
                    if (tts_type == 0) {
                        if (tts.value?.isSpeaking == true) {
                            tts.value?.stop()
                        } else tts.value?.speak(
                            (name), TextToSpeech.QUEUE_FLUSH, null, ""
                        )
                    }
                    if (tts_type == 1) {
                        selected_symbols += name
                    }
                    if (tts_type == 2) {
                        if (tts.value?.isSpeaking == true) {
                            tts.value?.stop()
                        } else tts.value?.speak(
                            (name), TextToSpeech.QUEUE_FLUSH, null, ""
                        )
                        selected_symbols += name
                    }
                    if (!wordfinder_path_ids.isEmpty()) {
                        if (wordfinder_path_ids.size <= 1) {
                            wordfinder_path_ids.removeAt(0)
                            wordfinder_path_ids.clear()
                            wordfinder_path_names.clear()
                            wordfinder_display_buttonguide.intValue = 0
                            createclonefolder.value = false
                            createclonesymbol.value = false
                        }
                    }
                })
        )
        Text(text = name, color = Color.Black, modifier = Modifier.padding(item_text_padding).height(height_dp.dp).width(width_dp.dp).align(Alignment.BottomCenter), textAlign = TextAlign.Center)
        if (x_offset > 0.dp || y_offset > 0.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                if (wordfinder_display_buttonguide.intValue >= 1) {
                    ButtonGuide_Wordfinder()
                }
            }
        }
    }
}

@Composable
@NonSkippableComposable
fun Folder(Name: String, image_url: String, LinkedMenu: Int, Vertical_Stretch: Dp, x_offset: Dp = 0.dp, y_offset: Dp = 0.dp, modifier: Modifier = Modifier) {
    val name = Name.replaceFirstChar {
        if (it.isLowerCase())
            it.titlecase()
        else it.toString() }
    var height_dp = 16
    var width_dp = height_dp*3.0625
    Box(modifier = modifier.clip(RoundedCornerShape(40.dp)))
    {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image_url)
                .build(),
            "Picture of $name",
            modifier = Modifier
                .offset(x_offset, y_offset)
                .height(box_size+Vertical_Stretch+(box_padding*3))
                .background(Color.White)
                .border(width = 4.dp, color = Color.Black, shape = RoundedCornerShape(40.dp))
                .padding(box_padding)
                .scale(1f)
                .width(box_size)
                .clickable(onClick = {
                    if (!wordfinder_path_ids.isEmpty()) {
                        if (wordfinder_path_ids.size >= 2) {
                            if (LinkedMenu == wordfinder_path_ids[1]) {
                                wordfinder_path_ids.removeAt(0)
                                wordfinder_path_names.removeAt(0)
                                linked_menu.value = LinkedMenu
                                switchmenuparser.value += 1
                                wordfinder_manager()
                            }
                        }
                    }
                    else
                    {
                        linked_menu.value = LinkedMenu
                        switchmenuparser.value += 1
                    }
                })
        )
        Text(
            text = name,
            color = Color.Black,
            modifier = Modifier.padding(item_text_padding).height(height_dp.dp).width(width_dp.dp).align(Alignment.BottomCenter),
            textAlign = TextAlign.Center
        )
        if (x_offset > 0.dp || y_offset > 0.dp) {
            Row(modifier = Modifier.fillMaxSize())
            {
                if (wordfinder_display_buttonguide.intValue >= 1) {
                    ButtonGuide_Wordfinder()
                }
            }
        }
    }
}


data class menutemplate(
    val id: Int, // ID of the current menu
    val title: String, // Title of the current menu
    val parentId: Int?, // ID of the parent menu
    val item_list: List<String>, // List of the names of all items, both folders and symbols
    val pointers: List<Any>, // Pointers to be used in MenuFinder to find the corresponding menu for a folder to link to. False if item is a symbol since it has no pointer.
    val tts: List<Any>, // 0 is for appending to the input box without instantly playing, 1 is for instantly playing in tts engine without appending to input box, 2 is for both appending to text box and playing in tts engine instantly. If a value is false item is a folder that doesnt have tts.
    val item_type: List<Boolean>, // False is for folder, true is for symbol
)

fun MenuFinder(menu_id: Int?): menutemplate {
    if (menu_id !is Int) {
        return home
    }
    for (i in 0 until MenuList.size) {
        if (MenuList[i].id == menu_id) {
            return MenuList[i]
        }
    }
    return home
}

@Composable
fun MenuKeyGen() {
    menukeylist.clear()
    for (i in 0 until MenuList.size) {
        menukeylist += MenuList[i].id
    }
}

@Composable
@NonSkippableComposable
fun MenuParser(menutemplate: menutemplate, modifier: Modifier = Modifier) {
    var totalitems = ((screenWidth - (button_boxes_width * 2))/(box_size + (box_padding*2)))*((screenHeight-(static_row_height*2))/box_size)
    var total_box_size = box_size+(box_padding*2)
    val vertical_stretch = ((menu_height)-((((menu_height)/(total_box_size)).toInt())*total_box_size))
    var item_names = remember { mutableStateListOf<String>() }
    var item_urls = remember { mutableStateListOf<String>() }
    if (item_text_padding < 5.dp)
    {
        item_text_padding = 5.dp
    }
    LaunchedEffect(Unit) {
        getAccessToken()
    }
    LaunchedEffect(menutemplate) {
        getAccessToken()

        item_names.clear()
        item_urls.clear()

        menutemplate.item_list.forEach { query ->
            val res = useApiWithToken(accesstoken, query)
            item_names.add(res?.name ?: "")
            item_urls.add(res?.image_url ?: "")
        }
    }
    if (switchmenuparser.value > 0) {
        key(switchmenuparser.value) {
            FlowRow(
                modifier = modifier.fillMaxWidth().fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var itemsdisplayed = 0
                for (i in 0 until menutemplate.item_list.size) {
                    if (i >= item_names.size || i >= item_urls.size) break
                    val itemKey = "${menutemplate.id}-$i"
                    Box(modifier = Modifier.onGloballyPositioned { coords ->
                        item_positions[itemKey] = coords.positionInRoot()
                    }) {
                        if (menutemplate.item_type[i]) {
                            Symbol(item_names[i], item_urls[i], vertical_stretch, menutemplate.tts[i] as Int)
                        } else {
                            Folder(item_names[i], item_urls[i], menutemplate.pointers[i] as Int, vertical_stretch)
                        }
                    }
                    itemsdisplayed += 1
                }
                for (i in 0 until totalitems.toInt() - (itemsdisplayed)) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .border(
                                width = 4.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(40.dp)
                            )
                            .padding(box_padding)
                            .scale(1f)
                            .height(box_size + vertical_stretch + box_padding)
                            .width(box_size)
                    )
                }
            }
        }
    }
    else {
        key(menukeylist[MenuList.indexOf(menutemplate)]) {
            FlowRow(
                modifier = modifier.fillMaxWidth().fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                var itemsdisplayed = 0
                for (i in 0 until menutemplate.item_list.size) {
                    if (i >= item_names.size || i >= item_urls.size) break
                    if (menutemplate.item_type[i])
                    {
                        Symbol(
                            item_names[i],
                            item_urls[i],
                            vertical_stretch,
                            menutemplate.tts[i] as Int
                        )
                    }
                    else {
                        Folder(
                            item_names[i],
                            item_urls[i],
                            menutemplate.pointers[i] as Int,
                            vertical_stretch
                        )
                    }
                    itemsdisplayed += 1
                }
                for (i in 0 until totalitems.toInt() - (itemsdisplayed)) {
                    Box(
                        modifier = Modifier
                            .background(Color.White)
                            .border(
                                width = 4.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(40.dp)
                            )
                            .padding(box_padding)
                            .scale(1f)
                            .height(box_size + vertical_stretch + box_padding)
                            .width(box_size)
                    )
                }
            }
        }
    }
}

@Composable
fun Menu(modifier: Modifier) {
    menu_height = (screenHeight - static_row_height - static_row_height - static_row_height - static_row_height)
    menu_width = screenWidth - (button_boxes_width * 2)
    Column(
        modifier = Modifier.alpha(1f)
    ) {
        Column(
            modifier = modifier
                .width(menu_width)
                .height(menu_height)
                .offset(x = 0.dp, y = (static_row_height*2))
        ) {
            MenuParser(MenuFinder(1))
        }
    }
}

@Composable
fun MenuRow(modifier: Modifier) {
    val menu_terms: MutableList<String> =
        mutableListOf("Home", "Temp", "Temp2", "Temp3", "Temp4", "Temp5")
    for (i in 0 until menu_terms.size) // For loop to create modular number of boxes. Starts at zero due to X offset calculations and ends at the number of terms minus 1 since it starts at zero
    {
        Menurowbox(modifier, i, menu_terms)
    }
}

@Composable
fun Menurowbox(modifier: Modifier, i: Int, menu_terms: MutableList<String>) {
    val linked_menus: MutableList<Int?> = mutableListOf(0, null, null, null, null, null)
    var text_color = Color.Black // Set as var to be able to be customized by user later
    var box_color = Color.White // Set as var to be able to be customized by user later
    var border_size = 2.dp // Set as var to be able to be customized by user later
    var border_color = Color.Black // Set as var to be able to be customized by user later
    var width =
        (screenWidth / menu_terms.size) // Determine width of boxes by dividing screen width by total number of boxes which is equal to number of needed terms
    static_row_height =
        (screenHeight.value * ((70.dp / screenHeight).dp).value).dp // Fraction determined by base value of 70.dp then converted to fraction and applied to screen height to (hopefully) make box height scale with screen height
    var y_offset =
        (screenHeight - static_row_height - static_row_height) // Determines Y offset by subtracting height from the total screen width
    var x_offset =
        (0).dp // Determines X offset. Not needed since the first box starts at the left edge of the screen.
    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        Box(
            // FIX Y OFFSET
            modifier = modifier
                .offset((x_offset+(width*i)), y_offset)
                .width(width)
                .height(static_row_height)
                .background(color = box_color)
                .border(border = BorderStroke(border_size, border_color))
                .clickable(onClick = {
                    linked_menu.value = linked_menus[i]?: 0
                    switchmenuparser.value += 1
                })
        ) {
            Text(
                text = menu_terms[i],
                color = text_color,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
    modifier_picker = Modifier.width(screenWidth-(button_boxes_width*2)).height(screenHeight-(static_row_height*4)).offset(0.dp, button_boxes_width*2)
}


// Could make an image override function that lets the user use their own images in place of the default ones
fun ImageOverride() {
}

@Composable
fun WordFinder() {
    var showRow by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    val selectedItems = remember { mutableStateListOf<String>() }
    val box_height = ((screenHeight.value - static_row_height.value) * (0.8)).dp
    val box_width = (screenWidth.value * 0.8).dp
    val row_height = 56.dp
    val flowrow_height_space = box_height-row_height
    val flowrow_width_space = box_width
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize().background(Color(red = 230, green = 227, blue = 227, alpha = 100))) {
        Box(
            modifier = Modifier
                .offset(x = (screenWidth.value * 0.1).dp, y = ((screenHeight.value - static_row_height.value) * 0.1).dp)
                .border(width = 4.dp, color = Color.Black, shape = RoundedCornerShape(40.dp))
                .clip(RoundedCornerShape(40.dp))
                .height(box_height)
                .width(box_width)
                .background(Color.White)
                .padding(horizontal = 15.dp, vertical = 20.dp)
        ) {
            var text by remember { mutableStateOf("") }

            Row(
                modifier = Modifier.fillMaxWidth().height(row_height),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { wordfinder_display.intValue = 0 }
                ) {
                    Text(text = "Close", textAlign = TextAlign.Center)
                }

                TextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    label = { Text("Image Search") },
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                )

                Button(
                    onClick = {
                        searchQuery = text
                        showRow = true
                    }
                ) {
                    Text(text = "Search", textAlign = TextAlign.Center)
                }
            }
            if (showRow) {
                Column {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth().padding(8.dp).offset(y = row_height).verticalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dropdown Menu for Suggestions
                        for (i in 0 until MenuList.size) {
                            for (a in 0 until MenuList[i].item_list.size) {
                                if (MenuList[i].item_list[a].lowercase().replace(" ", "") == searchQuery.lowercase().replace(" ", "")) {
                                    WordFinder_Card(searchQuery, i, MenuList[i].item_type[a], a, flowrow_height_space, box_width)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getMenuPath(menuIndex: Int): String {
    val pathParts = mutableListOf<String>()
    val visited = mutableSetOf<Int>()
    var current: menutemplate? = MenuList[menuIndex]

    wordfinder_path_names.clear()

    while (current != null) {
        if (current.id in visited) break  // stops infinite loop
        visited.add(current.id)
        pathParts.add(0, current.title)
        wordfinder_path_names.add(0, current.title)
        val parentId = current.parentId
        current = if (parentId != null) MenuList.find { it.id == parentId } else null
    }

    return pathParts.joinToString(" > ")
}
fun setWordfinderPath(menuIndex: Int) {
    val pathParts = mutableListOf<String>()
    val visited = mutableSetOf<Int>()
    var current: menutemplate? = MenuList[menuIndex]

    wordfinder_path_ids.clear()

    while (current != null) {
        if (current.id in visited) break  // stops infinite loop
        visited.add(current.id)
        wordfinder_path_ids.add(0, current.id)
        val parentId = current.parentId
        current = if (parentId != null) MenuList.find { it.id == parentId } else null
    }
}

@Composable
fun WordFinder_Card(Name: String, MenuList_element: Int, is_symbol: Boolean, item_position: Int, total_avaliable_height: Dp, total_avaiable_width: Dp) {
    var min_height = 20.dp
    var cards_per_row = 4
    var card_height = 0.dp
    var card_name by remember { mutableStateOf("") }
    var card_url by remember { mutableStateOf("") }
    var box_size = (total_avaliable_height/cards_per_row)
    var box_padding = 20.dp
    val item_path = getMenuPath(MenuList_element)
    if ((total_avaliable_height.value/cards_per_row).dp > min_height) {
        card_height = (total_avaliable_height.value/cards_per_row).dp
    }
    else {
        card_height = min_height
    }
    LaunchedEffect(Unit) {
        val res = useApiWithToken(accesstoken, MenuList[MenuList_element].item_list[item_position])
        card_name = res?.name.toString()
        card_url = res?.image_url.toString()
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 4.dp, color = Color.Black, shape = RoundedCornerShape(40.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(box_padding),
            verticalAlignment = Alignment.Top
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(card_url)
                    .build(),
                contentDescription = "Picture of $card_name",
                modifier = Modifier
                    .size(box_size)
                    .scale(1f)
            )
            val capitalized_name = Name.replaceFirstChar { char ->
                char.titlecase()
            }
            Column()
            {
                Text(
                    text = card_name,
                    fontSize = (box_size.value / 3).sp
                )
                Text(
                    text = item_path,
                    fontSize = (box_size.value / 6).sp,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            var showButtonGuide by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    showButtonGuide = true
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(text = "Find", textAlign = TextAlign.Center)
            }
            if (showButtonGuide) {
                wordfinder_target_is_symbol = is_symbol
                setWordfinderPath(MenuList_element)
                val targetName = MenuList[MenuList_element].item_list[item_position]
                if (wordfinder_path_names.lastOrNull() != targetName) {
                    wordfinder_path_names.add(targetName)
                }
                wordfinder_display_buttonguide.intValue += 1
                wordfinder_display.intValue = 0
                switchmenuparser.value += 1
                wordfinder_manager()
            }
        }
    }
}

fun wordfinder_manager() {
    if (wordfinder_path_ids.size > 1) {
        createclonesymbol.value = false
        createclonefolder.value = true
    } else {
        createclonefolder.value = !wordfinder_target_is_symbol
        createclonesymbol.value = wordfinder_target_is_symbol
    }
}

@Composable
fun ButtonGuide_Wordfinder() {
        Row(
            modifier = Modifier
                .zIndex(2f)
                .fillMaxSize()
                .background(Color.Gray.copy(alpha = 0.5f))
                .clickable {
                    wordfinder_path_ids.clear()
                    wordfinder_display_buttonguide.intValue = 0
                    createclonefolder.value = false
                },
        )
        {}
}


@Composable
fun Buttonboxes() {
    val a = remember {mutableIntStateOf(0)}
    button_boxes_width = 70.dp
    val x_offset = ((screenWidth - button_boxes_width).value).dp
    val y_offset = 0.dp
    var switchmenu by remember { mutableStateOf(false) }
    var switchmenu1 by remember { mutableStateOf(false) }
    if (wordfinder_display.value == a.value) {
        //TOP RIGHT
        Column() {
            Box(
                modifier = Modifier
                    .offset(x_offset, y_offset)
                    .size(button_boxes_width)
                    .background(color = Color.White)
                    .border(border = BorderStroke(2.dp, Color.Black))
                    .clickable(onClick = {
                    })
            ) {
                Text(text = "Settings", color = Color.Black, modifier = Modifier.align(Alignment.Center))
            }
        }
        //BOTTOM RIGHT
        Column() {
            Box(
                modifier = Modifier
                    .offset(x_offset, y_offset+140.dp)
                    .size(button_boxes_width)
                    .background(color = Color.White)
                    .border(border = BorderStroke(2.dp, Color.Black))
                    .clickable(onClick = {
                        if (tts.value?.isSpeaking == true) {
                            tts.value?.stop()
                        }
                    })
            ) {
                Text(text = "Stop", color = Color.Black, modifier = Modifier.align(Alignment.Center))
            }
        }
        //TOP LEFT
        Column() {
            Box(
                modifier = Modifier
                    .offset(x_offset-70.dp)
                    .size(button_boxes_width)
                    .background(color = Color.White)
                    .border(border = BorderStroke(2.dp, Color.Black))
                    .clickable(onClick = {
                    })
            ) {
                Text(text = "Keyboard", color = Color.Black, modifier = Modifier.align(Alignment.Center))
            }}
        //MIDDLE LEFT
        Column() {
            Box(
                modifier = Modifier
                    .offset(x_offset-70.dp, y_offset+140.dp)
                    .size(button_boxes_width)
                    .background(color = Color.White)
                    .border(border = BorderStroke(2.dp, Color.Black))
                    .clickable(onClick = {
                        selected_symbols.clear()
                    })
            ) {
                Text(text = "Clear", color = Color.Black, modifier = Modifier.align(Alignment.Center))
            }

        }
        //BOTTOM LEFT
        Column() {
            Box(
                modifier = Modifier
                    .offset(x_offset - 70.dp, y_offset + 70.dp)
                    .size(button_boxes_width)
                    .background(color = Color.White)
                    .border(border = BorderStroke(2.dp, Color.Black))
                    .clickable(onClick = {
                        if (selected_symbols.size >= 1) {
                            selected_symbols.removeAt(selected_symbols.lastIndex)
                        }
                    })
            ) {
                Text(text = "Delete", color = Color.Black, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
    //MIDDLE RIGHT
    Column() {
        Box(
            modifier = Modifier
                .offset(x_offset, y_offset+70.dp)
                .size(button_boxes_width)
                .background(color = Color.White)
                .border(border = BorderStroke(2.dp, Color.Black))
                .clickable(onClick = {
                    wordfinder_display.intValue = 1
                })
        ) {
            Text(text = "Search", color = Color.Black, modifier = Modifier.align(Alignment.Center))
        }
    }
}



@Composable
fun Screen() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        tts = rememberTextToSpeech()
        val a = remember { mutableIntStateOf(0) }
        GetScreenDimensions()
        Static_Row_Needs()
        if (wordfinder_display.intValue != a.intValue) {
            WordFinder()
        } else {
            Buttonboxes()
            MenuRow(Modifier)
            InputBox(Modifier)
            Menu(Modifier)
        }
    }
}