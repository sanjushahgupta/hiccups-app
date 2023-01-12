package hicCups.p.hiccupsViewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hicCups.p.forNotification.HiccupsDetails
import hicCups.p.forNotification.NotificationClient
import hicCups.p.forNotification.NotificationData
import hicCups.p.forNotification.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class hiccupsViewmodel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendHiccups(receiverPhoneNumber: String, context: Context) =

        CoroutineScope(Dispatchers.IO).launch {
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
                                    val response =
                                        NotificationClient().getService().postNotification(it)
                                    val DateandTime = LocalDateTime.now().toString()

                                    if (response.isSuccessful) {
                                        addToFirebaseHiccupsDetails(
                                            senderPhone.toString(), receiverPhoneNumber, DateandTime
                                        )
                                        Toast.makeText(context, "hkj", Toast.LENGTH_SHORT).show()

                                    } else {
                                        Toast.makeText(context, "hvbj", Toast.LENGTH_SHORT).show()

                                    }

                                } catch (e: Exception) {
                                    Log.e("exception", e.toString())

                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "User does not exist.", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
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

    @SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState")
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
                        var dateTime = it.get("dateandTime").toString()
                        dateTime = dateTime.substring(0, 10) + "  " + dateTime.subSequence(11, 19)

                        senderList.add("  " + y.toString() + "\n" + "  " + dateTime)

                    }
                    senderListState.value = senderList
                }

            }
        }

        Column {

            if (senderListState.value.size == 1) {
                Spacer(modifier = Modifier.padding(top = 70.dp))

                Text(
                    "No hiccups sent.", fontSize = 20.sp, modifier = Modifier.padding(
                        top = 15.dp, start = 15.dp, end = 15.dp, bottom = 15.dp
                    ), color = Color.Gray
                )

            } else {

                Text(
                    text = "Sent hiccups: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, bottom = 8.dp)
                )
            }
        }

        LazyColumn(
            Modifier.fillMaxWidth()
        ) {
            items(senderListState.value) { it ->

                if (it.isNotEmpty()) {
                    Text(
                        text = it, modifier = Modifier.padding(all = 5.dp)
                    )
                    Divider()

                }

                Spacer(modifier = Modifier.padding(5.dp))

            }

        }

    }


    @SuppressLint("CoroutineCreationDuringComposition")
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
                        var number = it.get("sender").toString()
                        var dateTime = it.get("dateandTime").toString()

                        receiverList.add(
                            number + "\n" + dateTime.substring(
                                0, 10
                            ) + "  " + dateTime.substring(11, 19)
                        )
                    }

                }
                receiverListState.value = receiverList
            }
        }

        if (receiverListState.value.size == 0) {
            Spacer(modifier = Modifier.padding(top = 100.dp))
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "Call them whenever possible, but if you can't, then send them digital hiccups.",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(20.dp),
                    color = Color.Gray

                )
            }
        } else {
            Spacer(modifier = Modifier.padding(top = 30.dp))
            Text(
                "Received hiccups: ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 13.dp)
            )
            LazyColumn(
                Modifier.fillMaxWidth()
            ) {
                items(receiverListState.value) { it ->
                    if (it.isNotEmpty()) {
                        Text("$it", modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                    }
                    Divider()
                }

            }
            Spacer(modifier = Modifier.padding(top = 15.dp))
        }
    }
}
