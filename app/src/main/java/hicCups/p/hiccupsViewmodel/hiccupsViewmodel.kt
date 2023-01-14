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
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                }

                db.collection("users").document(receiverPhoneNumber).get().addOnSuccessListener {
                    if (it.exists()) {
                        val receiveruid = it.get("uid").toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        val ReceiverName = it.get("name").toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

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
                                        Toast.makeText(context, "Something went wrong. Try again !!", Toast.LENGTH_SHORT).show()

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


    @SuppressLint("CoroutineCreationDuringComposition", "MutableCollectionMutableState")
    @Composable
    fun FetchSenderDataFromFireBaseDB() {

        val senderList = mutableListOf<String>("")
        val senderListState = remember { mutableStateOf(mutableListOf("")) }

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val phon = auth.currentUser?.phoneNumber

        CoroutineScope(Dispatchers.IO).launch {
            db.collection("HiccupsDetails").get().addOnSuccessListener {
                it.documents.forEach {
                    if (it.get("sender").toString().equals(phon.toString())) {


                        val y = it.get("receiver")
                        val receiver_name = it.get("receiverName").toString()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        var dateTime = it.get("dateandTime").toString()
                        dateTime = dateTime.substring(0, 10) + "  " + dateTime.subSequence(11, 19)

                        senderList.add("  " + y.toString() + " (" + receiver_name + ")" + "\n" + "  " + dateTime)

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
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, bottom = 3.dp)
                )
            }
        }

        LazyColumn(
            Modifier.fillMaxWidth()
        ) {
            items(senderListState.value) { it ->

                if (it.isNotEmpty()) {
                    Text(
                        text = it, modifier = Modifier.padding(start = 5.dp, bottom = 8.dp)
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
        val receiverList = mutableListOf<String>()
        val receiverListState = remember { mutableStateOf(mutableListOf("")) }

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

                        receiverList.add(
                            number + " (" + senderName + ")" + "\n" + dateTime.substring(
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
                    Toast.makeText(
                        context, "An sms is sent to ${phoneNumber}", Toast.LENGTH_SHORT
                    ).show()
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
