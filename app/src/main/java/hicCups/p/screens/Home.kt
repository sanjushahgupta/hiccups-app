package hicCups.p.screens

import android.annotation.SuppressLint
import android.content.ContentValues
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
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hicCups.p.forNotification.HiccupsDetails
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

            Divider()
           getUserdata()

            if (send.value) {
                sendNotification(enterPhoneNumber.value.toString())
            }

        }

    }

}


@Composable
fun getUserdata() {
    var receiverList = mutableListOf<String>()
    var senderList = mutableListOf<String>()
    var receiverListState = remember {
        mutableStateOf<String>("")
    }
    var senderListState = remember{ mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val phon = auth.currentUser?.phoneNumber

    db.collection("HiccupsDetails").get().addOnSuccessListener {
        it.documents.forEach {
            //if(it.get("Receiver").toString().equals(phon.toString())){

            var x = it.get("receiver")
            var y = it.get("sender")
            receiverList.add(x.toString())
            senderList.add(y.toString())

        }

        receiverListState.value= receiverList.toString()
        senderListState.value = senderList.toString()

    }
    Text("Receiver  : ${receiverListState.value}")
    Text("Sender : ${senderListState.value}")
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
                            addToFirebaseHiccupsDetails(senderPhone.toString(), receiverPhoneNumber)
                            Log.e("send", "send")
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


 fun addToFirebaseHiccupsDetails(sender: String, receiver: String ){

    val db = FirebaseFirestore.getInstance()


//    val phoneNumber = auth.currentUser!!.phoneNumber
//    val uid = auth.currentUser!!.uid
    //  val user: MutableMap<String, Any> = HashMap()
    //  user["uid"] = uid
    //  user["phonenumber"] = phoneNumber.toString()


    db.collection("HiccupsDetails").add( HiccupsDetails(sender,receiver)).addOnSuccessListener { documentReference ->

        Log.d("tag", "added ")




    }.addOnFailureListener { e -> Log.w("sen", "Error adding document", e)}


}
