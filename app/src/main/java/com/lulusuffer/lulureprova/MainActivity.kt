package com.lulusuffer.lulureprova

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lulusuffer.lulureprova.ui.theme.LuluReprovaTheme

data class ReprovandoVitimas(val name: String, val n1: Float?, val n2: Float?, val n3: Float?)

class MainActivity : ComponentActivity() {
    private val sharedPreferences : SharedPreferences by lazy {
        getSharedPreferences("com.lulureprova.vitimas.google.com", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LuluReprovaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Column {
                        MyTabs(sharedPreferences = sharedPreferences)
                    }
                    
                }
            }
        }
    }
}

//@Composable
//fun CustomTab() {
//    Box(modifier = Modif)
//}

@Composable
fun MyTabs(sharedPreferences: SharedPreferences) {
    var selectedIndex by remember {
        mutableStateOf(0)
    }

    val listTab = listOf(
        "Adicionar", "Alunos"
    )

    Column(verticalArrangement = Arrangement.Bottom) {
        when (selectedIndex) {
            0 -> AddStudents(sharedPreferences = sharedPreferences)
            1 -> StudentsScreen(sharedPreferences = sharedPreferences)
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .weight(1f))
        TabRow(selectedTabIndex = selectedIndex, containerColor = Color(0xff2b2d42), indicator = {}, modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(25.dp))) {
            listTab.forEachIndexed{ index, text ->
                val currentTab = index == selectedIndex
                Tab(selected = currentTab, modifier = if (currentTab) Modifier
                    .padding(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color = Color.White)
                else Modifier
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(50)
                    )
                    .background(color = Color(0xff2b2d42)), onClick = { selectedIndex = index }, text = { Text(text = text, color = Color(0xffb8bad8)) })
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudents(sharedPreferences: SharedPreferences, modifier: Modifier = Modifier) {
    var text by remember {
        mutableStateOf("")
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .fillMaxWidth()) {
        Text(
            text = "Adicionar Alunos",
            modifier = modifier,
            fontSize = 18.sp
        )
        OutlinedTextField(value = text, onValueChange = { text = it }, maxLines = 1, label = { Text(text = "Nome") }, singleLine = true)
        Button(onClick = {
            if (text.isNotEmpty()) {
                var estudantes = sharedPreferences.getString("vitimas", null)
                val editor = sharedPreferences.edit()
                val studentsList = if (estudantes.isNullOrEmpty()) mutableListOf<ReprovandoVitimas>() else Gson().fromJson<MutableList<ReprovandoVitimas>>(estudantes, mutableListOf<ReprovandoVitimas>()::class.java)
                val gson = GsonBuilder().serializeNulls().create()
                studentsList.add(ReprovandoVitimas(name = text.toString(), n1 = null, n2 = null, n3 = null))
                val listJson = gson.toJson(studentsList)
                editor.putString("vitimas", listJson).apply()
            }

        }, content = { Text(text = "Adicionar") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3db4f2)))
    }
}

@Composable
fun StudentsScreen(sharedPreferences: SharedPreferences) {
    val students = sharedPreferences.getString("vitimas", null)
    val type = object : TypeToken<MutableList<ReprovandoVitimas>>() {}.type
    val studentList = if (students.isNullOrEmpty()) mutableListOf<ReprovandoVitimas>() else Gson().fromJson<MutableList<ReprovandoVitimas>>(students, type)
    val lazyState = remember { studentList.toMutableStateList() }

    LazyColumn() {
        if (!students.isNullOrEmpty()) {
            items(lazyState) { items ->
                StudentsContainer(sharedPreferences = sharedPreferences, vitimas = items, studentList = lazyState)
            }
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsContainer(sharedPreferences: SharedPreferences, studentList: SnapshotStateList<ReprovandoVitimas>, vitimas: ReprovandoVitimas) {
    var n1 by remember {
        mutableStateOf(vitimas.n1)
    }

    var n2 by remember {
        mutableStateOf(vitimas.n2)
    }

    var n3 by remember {
        mutableStateOf(vitimas.n3)
    }

    val editor = sharedPreferences.edit()

    Column {
        Text(text = vitimas.name, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            TextField(value = if (n1 != null) n1.toString() else "", modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 2.dp), label = { Text(text = "Nota 1") }, onValueChange = { n1 = it.toFloat() }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            TextField(value = if (n2 != null) n2.toString() else "", modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 2.dp), label = { Text(text = "Nota 2") }, onValueChange = { n2 = it.toFloat() }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
            TextField(value = if (n3 != null) n3.toString() else "", modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 2.dp), label = { Text(text = "Nota 3") }, onValueChange = { n3 = it.toFloat() }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Button(onClick = {
                studentList.set(studentList.indexOf(vitimas), ReprovandoVitimas(vitimas.name, n1 = n1, n2 = n2, n3 = n3))
                val gson = GsonBuilder().serializeNulls().create()
                val studentJson = gson.toJson(studentList)
                editor.putString("vitimas", studentJson).apply()
            }, content = { Text(text = "Atualizar") })
            Button(onClick = {
                studentList.remove(vitimas)
                val gson = GsonBuilder().serializeNulls().create()
                val studentJson = gson.toJson(studentList)
                editor.putString("vitimas", studentJson).apply()
            }, content = { Text(text = "Remover") })
        }
    }

}
