package hicCups.p.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hicCups.p.R
import hicCups.p.forNotification.HiccupsDetails
import hicCups.p.forNotification.NotificationClient
import hicCups.p.forNotification.NotificationData
import hicCups.p.forNotification.PushNotification
import hicCups.p.util.userPreference
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun Home(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = userPreference(context)


    var focus = LocalFocusManager.current
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .height(30.dp)
                .fillMaxSize()
        ) {

            var expanded = remember { mutableStateOf(false) }
            Text(text = "Hiccups")
      Spacer(modifier = Modifier.padding(start = 100.dp))
            IconButton(onClick = { expanded.value = true }) {
                if (expanded.value) {
                    navController.navigate("receivedetails")
                    expanded.value = false

                }
                Text("Received hiccups")

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
                .padding(bottom = 18.dp, start = 18.dp, end = 18.dp), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val send = remember { mutableStateOf(false) }
            val auth = FirebaseAuth.getInstance()
            Image(painter = painterResource(id = R.drawable.missing), contentDescription ="hiccups image" , modifier = Modifier.size(40.dp))
            Text("Are you missing anyone?")
            val enterPhoneNumber = remember { mutableStateOf("") }
            val senderPhone = auth.currentUser?.phoneNumber
            OutlinedTextField(
                value = enterPhoneNumber.value,
                onValueChange = { enterPhoneNumber.value = it },
                placeholder = { Text(text = "Enter phone number.") })

            Button(onClick = {
                send.value = true
                focus.clearFocus()
            }) {
                Text(text = "Send Hiccup")

            }
            Divider()
            Text(text = "Recently Sent", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Divider()
           getUserdata()

            if (send.value) {
                enterPhoneNumber.value = enterPhoneNumber.value.replace("\\s".toRegex(), "")
                if(enterPhoneNumber.value == senderPhone){
                  //  Toast.makeText(LocalContext.current,"$enterPhoneNumber.value",Toast.LENGTH_SHORT).show()
                    send.value = false
                }
                sendNotification(enterPhoneNumber.value.toString())
                send.value = false
            }

        }

    }

}



@Composable
fun getUserdata() {

    var senderList = mutableListOf<String>("")
    var senderListState: MutableState<MutableList<String>> = remember{ mutableStateOf(mutableListOf(""))}

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val phon = auth.currentUser?.phoneNumber

    db.collection("HiccupsDetails").get().addOnSuccessListener {
        it.documents.forEach {
            if(it.get("sender").toString().equals(phon.toString())){


           var y = it.get("receiver")
                var dateTime = it.get("dateandTime")

          senderList.add(y.toString() +" " +dateTime)


        }


            senderListState.value = senderList
        }

    }
   Card(border = BorderStroke(2.dp, Color.Black), modifier = Modifier
       .padding(8.dp)
    ) {
       if (senderListState.value.size == 0) {
           Text("No hiccups sent.", modifier = Modifier.height(50.dp))
       }else{
           LazyColumn {
           items(senderListState.value) { it ->
               Card(
                   elevation = 3.dp,
                   shape = RoundedCornerShape(2.dp),
               ) {
                   Text("$it",  modifier = Modifier.padding(8.dp) )
               }
               Divider()
           }
       }
   }

    }

}


@RequiresApi(Build.VERSION_CODES.O)
fun sendNotification(receiverPhoneNumber: String) = CoroutineScope(Dispatchers.IO).launch {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val senderPhone = auth.currentUser?.phoneNumber


    db.collection("users").document(senderPhone.toString()).get().addOnSuccessListener {
        var senderName = ""
        if (it.exists()) {
            senderName = it.get("name").toString()
        }




    db.collection("users").document(receiverPhoneNumber).get().addOnSuccessListener {
        if (it.exists()) {
            var receiveruid = it.get("uid").toString()

            Log.d("receiveruid", receiveruid)

            val TOPIC = "/topics/$receiveruid"

            CoroutineScope(Dispatchers.IO).launch {
                PushNotification(
                    NotificationData("Hiccups", "You are on $senderName's mind"), TOPIC
                ).also {

                    try {
                        val response = NotificationClient().getService().postNotification(it)
                        val DateandTime =  LocalDateTime.now().toString()

                        if (response.isSuccessful) {
                            addToFirebaseHiccupsDetails(senderPhone.toString(), receiverPhoneNumber, DateandTime)
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

}


 fun addToFirebaseHiccupsDetails(sender: String, receiver: String, DateandTime: String){

    val db = FirebaseFirestore.getInstance()


//    val phoneNumber = auth.currentUser!!.phoneNumber
//    val uid = auth.currentUser!!.uid
    //  val user: MutableMap<String, Any> = HashMap()
    //  user["uid"] = uid
    //  user["phonenumber"] = phoneNumber.toString()


    db.collection("HiccupsDetails").add( HiccupsDetails(sender,receiver,DateandTime)).addOnSuccessListener { documentReference ->

        Log.d("tag", "added ")




    }.addOnFailureListener { e -> Log.w("sen", "Error adding document", e)}


}
