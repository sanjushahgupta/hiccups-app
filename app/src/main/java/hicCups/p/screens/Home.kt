package hicCups.p.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hicCups.p.forNotification.NotificationClient
import hicCups.p.forNotification.NotificationData
import hicCups.p.forNotification.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun Home(navController: NavController) {

    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .height(30.dp)
                .fillMaxSize()
        ) {
            Text(text = "Hiccups")
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more")
            //for logout and Received Hiccups
        }

    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 18.dp, start = 18.dp), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val send = remember { mutableStateOf(false) }
            Text("Are you missing anyone?")
            val enterPhoneNumber = remember { mutableStateOf("+4915773507453") }

            OutlinedTextField(
                value = enterPhoneNumber.value,
                onValueChange = { enterPhoneNumber.value = it },
                placeholder = { Text(text = "Enter phone number.") })

            Button(onClick = {
                send.value = true
            }) {
                Text(text = "Send Hiccup")

            }
            Divider()
            Text(text = "Recently Sent", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            if (send.value) {
                sendNotification(enterPhoneNumber.toString())
            }
        }

    }

}


fun sendNotification(receiverPhoneNumber: String) = CoroutineScope(Dispatchers.IO).launch {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val senderPhone = auth.currentUser?.phoneNumber
    db.collection("users").document(receiverPhoneNumber).get().addOnSuccessListener {
        if (it.exists()) {
            var receiveruid = it.get("uid").toString()
            Log.d("receiveruid", receiveruid)

            val TOPIC = "/topics/$receiveruid"

            PushNotification(
                NotificationData("Hiccups", "You are on $senderPhone's mind"), TOPIC
            ).also {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = NotificationClient().getService().postNotification(it)
                        if (response.isSuccessful) {

                        } else {
                            Log.e("tagr", response.errorBody().toString())
                        }

                    } catch (e: Exception) {
                        Log.e("exception", e.toString())

                    }
                }
            }
        } else {
            Log.d("tagdoesnotexists", "error")
        }
    }.addOnFailureListener {
        Log.e("faillisture", "${it.message}")
    }

}
