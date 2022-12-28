package hicCups.p.screens

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hicCups.p.R
import hicCups.p.forNotification.user
import kotlinx.coroutines.delay


@SuppressLint("SuspiciousIndentation")
@Composable
fun Otp(phonenumber: String, verificationcode: String, token: String, navController: NavController) {
    val otpCode = remember{ mutableStateOf("") }
    val submitButtonStatus = remember{ mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(bottom = 18.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

      //  Icon(painter = painterResource(id = R.drawable.ic_baseline_sms_24), contentDescription = "sms icon" )

        Text(text = "Verification Code", fontSize = 15.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = otpCode.value,
            onValueChange = { otpCode.value = it },
     )


        Button(onClick = { submitButtonStatus.value = true }){
            Text("Submit Opt")
        }

        if(submitButtonStatus.value) {

            Log.d("verifiscode", verificationcode)
             val credential:PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationcode,otpCode.value)
       Log.d("verifis", credential.toString())
            SignIn(credential, navController)
         //   LaunchedEffect(Unit){
             //   navController.navigate("home")
           // }
        }

    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
private fun SignIn(credential: PhoneAuthCredential, navController: NavController){
val auth = Firebase.auth
   val db = FirebaseFirestore.getInstance()
    auth.signInWithCredential(credential).addOnSuccessListener {

        val phoneNumber = auth.currentUser!!.phoneNumber
        val uid = auth.currentUser!!.uid
        val user: MutableMap<String, Any> = HashMap()
            user["uid"] = uid
            user["phonenumber"] = phoneNumber.toString()


            db.collection("users").document(phoneNumber.toString()).set(user(uid, phoneNumber.toString()))
                .addOnSuccessListener { documentReference ->

                        navController.navigate("home")

                    Log.d("tag", "SigIn Success")
                }

                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }




    }
        .addOnFailureListener {
          Log.d("tag", "SignIn Failed")
        }

}
