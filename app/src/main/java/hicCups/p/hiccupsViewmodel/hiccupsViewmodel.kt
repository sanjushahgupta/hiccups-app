package hicCups.p.hiccupsViewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.material.internal.ContextUtils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hicCups.p.R
import hicCups.p.forNotification.HiccupsDetails
import hicCups.p.forNotification.NotificationClient
import hicCups.p.forNotification.NotificationData
import hicCups.p.forNotification.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

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
                        val receiveruid = it.get("uid").toString()

                        val ReceiverName = it.get("name").toString()


                        Log.d("receiveruid", receiveruid)

                        val TOPIC = "/topics/$receiveruid"
                        Toast.makeText(context, "Hiccup sent.", Toast.LENGTH_SHORT).show()
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
                                            senderPhone.toString(),
                                            receiverPhoneNumber,
                                            DateandTime,
                                            senderName,
                                            ReceiverName
                                        )

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Something went wrong. Try again !!",
                                            Toast.LENGTH_SHORT
                                        ).show()

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

    fun addToFirebaseHiccupsDetails(
        sender: String,
        receiver: String,
        DateandTime: String,
        senderName: String,
        receiverName: String
    ) {

        val db = FirebaseFirestore.getInstance()


        db.collection("HiccupsDetails")
            .add(HiccupsDetails(sender, receiver, DateandTime, senderName, receiverName))
            .addOnSuccessListener { documentReference ->

                Log.d("tag", "added ")


            }.addOnFailureListener { e -> Log.w("sen", "Error adding document", e) }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState")
    @Composable
    fun FetchSenderDataFromFireBaseDB() {

        val senderNameList = mutableListOf<String>("")
        val senderPhoneNumberList = mutableListOf<String>("")
        val senderDateTimeList = mutableListOf<String>("")
        val senderNameListState = remember { mutableStateOf(mutableListOf("")) }
        val senderPhoneNumberListState = remember { mutableStateOf(mutableListOf("")) }
        val senderDateTimeListState = remember { mutableStateOf(mutableListOf("")) }
        val context = LocalContext.current
        var sendAgainBtn = remember {
            mutableStateOf(false)
        }
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val phon = auth.currentUser?.phoneNumber

        CoroutineScope(Dispatchers.IO).launch {
            db.collection("HiccupsDetails").get().addOnSuccessListener {
                it.documents.forEach {
                    if (it.get("sender").toString().equals(phon.toString())) {


                        val receiverph = it.get("receiver").toString()
                        val receiver_name = it.get("receiverName").toString()
                        var dateTime = it.get("dateandTime").toString()
                        dateTime = dateTime.substring(0, 10) + "  " + dateTime.subSequence(11, 19)

                        senderNameList.add(receiver_name)
                        senderPhoneNumberList.add(receiverph)
                        senderDateTimeList.add(dateTime)

                    }
                    senderDateTimeListState.value = senderDateTimeList
                    senderNameListState.value = senderNameList
                    senderPhoneNumberListState.value = senderPhoneNumberList
                }

            }
        }

        Column {

            val sizeOfList = senderNameListState.value.size
            if (sizeOfList == 1) {
                Spacer(modifier = Modifier.padding(top = 70.dp))

                Text(
                    "No hiccups sent.", fontSize = 20.sp, modifier = Modifier.padding(
                        top = 15.dp, start = 15.dp, end = 15.dp, bottom = 15.dp
                    ), color = Color.Gray
                )

            } else {

                Text(
                    text = "Sent hiccups",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
                )



                LazyColumn(
                    Modifier.fillMaxWidth()
                ) {
                    items(sizeOfList) { it ->
                        if (it > 1) {
                            Row {

                                val name = senderNameListState.value[it]
                                val ph = senderPhoneNumberListState.value[it]
                                val dateTime = senderDateTimeListState.value[it]
                                Column {
                                    Text(
                                        "$name, $ph",
                                        modifier = Modifier.padding(start = 18.dp, top = 15.dp)
                                    )
                                    Text(
                                        dateTime,
                                        modifier = Modifier.padding(start = 18.dp, bottom = 10.dp),
                                        color = Color.Gray
                                    )
                                }
                                Button(
                                    onClick = { sendAgainBtn.value = true },
                                    modifier = Modifier.padding(
                                        start = 18.dp, top = 7.dp, bottom = 10.dp
                                    ),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                                ) {
                                    Text("Send Again", color = colorResource(id = R.color.LogiTint))
                                }

                                if (sendAgainBtn.value) {
                                    sendHiccups(ph, context)
                                    sendAgainBtn.value = false
                                }

                            }
                            Divider()
                        }
                    }


                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun ReceivedDetailsListFromFirebaseDB() {
        val receiverNameList = mutableListOf<String>()
        val receiverNameListState = remember { mutableStateOf(mutableListOf("")) }
        val receiverPhoneNumberList = mutableListOf<String>()
        val receiverPhoneNumberListState = remember { mutableStateOf(mutableListOf("")) }
        val receiverDateTimeList = mutableListOf<String>()
        val receiverDateTimeListState = remember { mutableStateOf(mutableListOf("")) }
        val context = LocalContext.current


        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val phon = auth.currentUser?.phoneNumber
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("HiccupsDetails").get().addOnSuccessListener {
                it.documents.forEach {

                    if (it.get("receiver").toString().equals(phon.toString())) {
                        val number = it.get("sender").toString()
                        val dateTime = it.get("dateandTime").toString()
                        val senderName = it.get("senderName").toString()

                        receiverNameList.add(senderName)
                        receiverPhoneNumberList.add(number)
                        receiverDateTimeList.add(
                            dateTime.substring(
                                0, 10
                            ) + "  " + dateTime.substring(11, 19)
                        )


                    }

                }

                receiverNameListState.value = receiverNameList
                receiverPhoneNumberListState.value = receiverPhoneNumberList
                receiverDateTimeListState.value = receiverDateTimeList
            }
        }

        if (receiverPhoneNumberListState.value.size == 0) {
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
                "Received hiccups",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 13.dp)
            )
            var sendbytap = remember {
                mutableStateOf(false)
            }
            LazyColumn(
                Modifier.fillMaxWidth()
            ) {
                var sizeOfList = receiverPhoneNumberListState.value.size
                items(sizeOfList) { it ->
                    Row {
                        var phoneNumber = receiverPhoneNumberListState.value[it]
                        val name = receiverNameListState.value[it]
                        val dateTime = receiverDateTimeListState.value[it]
                        Column {
                            Text("$name, $phoneNumber", modifier = Modifier.padding(top = 15.dp))
                            Text(
                                "$dateTime",
                                modifier = Modifier.padding(bottom = 10.dp),
                                color = Color.Gray
                            )
                        }
                        Button(
                            onClick = { sendbytap.value = true },
                            modifier = Modifier.padding(start = 18.dp, top = 7.dp, bottom = 10.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        ) {
                            Text("Send Hiccup", color = colorResource(id = R.color.LogiTint))
                        }
                        if (sendbytap.value) {

                            sendHiccups(phoneNumber, context)
                            sendbytap.value = false
                        }
                    }
                    Divider()
                }

            }
            Spacer(modifier = Modifier.padding(top = 15.dp))
        }
    }


    @SuppressLint("RestrictedApi")
    fun generateOtpCodeForSignUp(
        phoneNumber: String, name: String, context: Context, navController: NavController
    ) {

        lateinit var callBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
        CoroutineScope(Dispatchers.IO).launch {
            callBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("GFG", "onVerificationCompleted Success")

                }

                override fun onVerificationFailed(e: FirebaseException) {

                    Log.d("GFG", "onVerificationFailed $e")


                }

                override fun onCodeSent(
                    VerificationId: String,
                    token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
                ) {

                    super.onCodeSent(VerificationId, token)
                    val ide = VerificationId
                    val token = token
                    navController.navigate("otp/${phoneNumber}/$ide/$token/${name}")


                }
            }

            val auth = Firebase.auth
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber) // Phone number to verify
                .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS) // Timeout and unit
                .setActivity(ContextUtils.getActivity(context)!!)// Activity (for callback binding)
                .setCallbacks(callBacks) // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }
    }

}
