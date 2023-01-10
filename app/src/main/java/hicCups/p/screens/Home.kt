package hicCups.p.screens

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import hicCups.p.R
import hicCups.p.hiccupsViewmodel.hiccupsViewmodel
import hicCups.p.util.userPreference

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint(
    "UnusedMaterialScaffoldPaddingParameter",
    "CoroutineCreationDuringComposition",
    "SuspiciousIndentation",
    "UnrememberedMutableState"
)

@Composable
fun Home(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = userPreference(context)
    val hiccupsViewmodel = hiccupsViewmodel()


    var focus = LocalFocusManager.current
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .height(30.dp)
                .fillMaxSize()
        ) {

            var expanded = remember { mutableStateOf(false) }
            Text(text = "Hiccups")
            Spacer(modifier = Modifier.padding(start = 250.dp))
            IconButton(onClick = { expanded.value = true }) {
                if (expanded.value) {
                    navController.navigate("receivedetails")
                    expanded.value = false

                }
                Column {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_call_received_24),
                        ""
                    )
                    Icon(painter = painterResource(id = R.drawable.ic_baseline_receipt_24), "")
                }


            }
        }
    }) {
        Column(
            modifier = Modifier
                .clickable(
                    MutableInteractionSource(),
                    indication = null,
                    onClick = { focus.clearFocus() })
                .fillMaxSize()
                .padding(bottom = 18.dp, start = 18.dp, end = 18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val send = remember { mutableStateOf(false) }


            val auth = FirebaseAuth.getInstance()
            Image(
                painter = painterResource(id = R.drawable.missing),
                contentDescription = "hiccups image",
                modifier = Modifier.size(40.dp)
            )
            Text("Are you missing anyone?")


            var enterPhoneNumber = remember { mutableStateOf("") }
            OutlinedTextField(
                value = enterPhoneNumber.value,
                onValueChange = { enterPhoneNumber.value = it },
                placeholder = { Text(text = "Enter Phone number") },
                modifier = Modifier.wrapContentSize())

            val senderPhone = auth.currentUser?.phoneNumber

            Button(onClick = {
                send.value = true
                focus.clearFocus()
            }) {
                Text(text = "Send Hiccup")

            }

            Text(text = "Recently Sent", fontWeight = FontWeight.Bold, fontSize = 20.sp)


           hiccupsViewmodel.FetchSenderDataFromFireBaseDB()

            if (send.value) {
                if (enterPhoneNumber.value.isEmpty()) {
                    Toast.makeText(LocalContext.current, "Invalid input", Toast.LENGTH_SHORT).show()
                } else {
                    enterPhoneNumber.value = enterPhoneNumber.value.replace("\\s".toRegex(), "")

                    if (enterPhoneNumber.value == senderPhone) {
                        Toast.makeText(
                            LocalContext.current,
                            "$enterPhoneNumber.value",
                            Toast.LENGTH_SHORT
                        ).show()
                        send.value = false
                    } else {
                        hiccupsViewmodel.sendHiccups(enterPhoneNumber.value)
                        send.value = false
                    }
                }
            }

        }

    }

}
