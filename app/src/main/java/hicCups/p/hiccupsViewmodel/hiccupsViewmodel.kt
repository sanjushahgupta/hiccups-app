package hicCups.p.hiccupsViewmodel

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hicCups.p.R
import hicCups.p.forNotification.HiccupsDetails
import hicCups.p.forNotification.NotificationClient
import hicCups.p.forNotification.NotificationData
import hicCups.p.forNotification.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class hiccupsViewmodel: ViewModel() {


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendHiccups(receiverPhoneNumber: String) = CoroutineScope(Dispatchers.IO).launch {


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
                                val DateandTime = LocalDateTime.now().toString()

                                if (response.isSuccessful) {
                                    addToFirebaseHiccupsDetails(
                                        senderPhone.toString(), receiverPhoneNumber, DateandTime
                                    )
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


    fun addToFirebaseHiccupsDetails(sender: String, receiver: String, DateandTime: String) {

        val db = FirebaseFirestore.getInstance()


//    val phoneNumber = auth.currentUser!!.phoneNumber
//    val uid = auth.currentUser!!.uid
        //  val user: MutableMap<String, Any> = HashMap()
        //  user["uid"] = uid
        //  user["phonenumber"] = phoneNumber.toString()

        db.collection("HiccupsDetails").add(HiccupsDetails(sender, receiver, DateandTime))
            .addOnSuccessListener { documentReference ->

                Log.d("tag", "added ")


            }.addOnFailureListener { e -> Log.w("sen", "Error adding document", e) }


    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun FetchSenderDataFromFireBaseDB() {

        var senderList = mutableListOf<String>("")
        var senderListState = remember { mutableStateOf(mutableListOf("")) }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val phon = auth.currentUser?.phoneNumber

        CoroutineScope(Dispatchers.IO).launch {
        db.collection("HiccupsDetails").get().addOnSuccessListener {
            it.documents.forEach {
                if (it.get("sender").toString().equals(phon.toString())) {


                    var y = it.get("receiver")
                    var dateTime = it.get("dateandTime")

                    senderList.add(y.toString() + " " + dateTime)


                }
                senderListState.value = senderList
            }

        }
    }
        if (senderListState.value.isEmpty()) {
            Text("No hiccups sent.")
        } else {
            LazyColumn {
                items(senderListState.value) { it ->
                    Card(
                        elevation = 3.dp,
                        shape = RoundedCornerShape(2.dp),
                    ) {
                        Text("$it", modifier = Modifier.padding(8.dp))
                    }

                }
            }
        }
    }

    @Composable
    fun ReceivedDetailsListFromFirebaseDB() {
        var receiverList = mutableListOf<String>()
        var receiverListState = remember { mutableStateOf(mutableListOf("")) }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val phon = auth.currentUser?.phoneNumber
        CoroutineScope(Dispatchers.IO).launch {
        db.collection("HiccupsDetails").get().addOnSuccessListener {
            it.documents.forEach {

                if (it.get("receiver").toString().equals(phon.toString())) {
                    var x = it.get("sender")
                    receiverList.add(x.toString())
                }

            }

            receiverListState.value = receiverList

        }
    }
        Text("Received From")
        Spacer(modifier = Modifier.padding(bottom = 25.dp))
        if(receiverListState.value.isEmpty()){
            Text("No hiccups received.")
        }else {
            LazyColumn {
                items(receiverListState.value) { it ->
                    Card() {
                        Text("$it")
                    }
                }
            }
        }

    }



}
