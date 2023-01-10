package hicCups.p.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.material.internal.ContextUtils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hicCups.p.R
import hicCups.p.forNotification.user
import hicCups.p.util.userPreference
import kotlinx.coroutines.launch

@SuppressLint(
    "UnusedMaterialScaffoldPaddingParameter",
    "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun Otp(
    phonenumber: String,
    verificationcode: String,
    token: String,
    name: String = "name",
    navController: NavController
) {
    val otpCode = remember { mutableStateOf("") }
    val submitButtonStatus = remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val resendbuttonClick = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clickable(
                MutableInteractionSource(),
                indication = null,
                onClick = { focus.clearFocus() })
            .fillMaxSize()
            .padding(bottom = 18.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Verification Code", fontSize = 15.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = otpCode.value,
            onValueChange = { otpCode.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = ({ stringResource(R.string.Enterphonenumber) })

        )


        Row {
            Button(onClick = {
                submitButtonStatus.value = true
                focus.clearFocus()
            }) {
                Text("Submit Opt")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text("Resend verification code",
                color = Color.Blue,
                modifier = Modifier.clickable(onClick = { resendbuttonClick.value = true }))
            if (resendbuttonClick.value) {
                ResendOtpCode(phonenumber, name, navController)
                resendbuttonClick.value = false
            }

        }


        if (submitButtonStatus.value) {

            val credential: PhoneAuthCredential =
                PhoneAuthProvider.getCredential(verificationcode, otpCode.value)

            SignIn(credential, navController, name)

        }

    }
}


@SuppressLint(
    "UnusedMaterialScaffoldPaddingParameter",
    "CoroutineCreationDuringComposition",
    "SuspiciousIndentation"
)
@Composable
fun SignIn(credential: PhoneAuthCredential, navController: NavController, name: String) {

    val auth = Firebase.auth
    val errorToast = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val dataStore = userPreference(LocalContext.current)
    val db = FirebaseFirestore.getInstance()
    auth.signInWithCredential(credential).addOnSuccessListener {

        val phoneNumber = auth.currentUser!!.phoneNumber
        val uid = auth.currentUser!!.uid
        val user: MutableMap<String, Any> = HashMap()
        name[0].uppercase()
        user["uid"] = uid
        user["phonenumber"] = phoneNumber.toString()
        user["name"] = name


        db.collection("users").document(phoneNumber.toString())
            .set(user(uid, phoneNumber.toString(), name))
            .addOnSuccessListener { documentReference ->


                scope.launch {
                    dataStore.saveLoginStatus("loggedIn")
                }
                navController.navigate("home")
                Log.d("tag", "SigIn Success")
            }

            .addOnFailureListener {
                errorToast.value = true
            }


    }.addOnFailureListener {

            errorToast.value = true
        }

    if (errorToast.value) {
        Toast.makeText(LocalContext.current, "", Toast.LENGTH_SHORT).show()
    }


}

@SuppressLint("RestrictedApi")
@Composable
fun ResendOtpCode(phonenumber: String, name: String, navController: NavController) {
    lateinit var callBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    val errorToast = remember {
        mutableStateOf(false)
    }

    val sendOptToast = remember {
        mutableStateOf(false)
    }
    callBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("GFG", "onVerificationCompleted Success")
            sendOptToast.value = true

        }

        override fun onVerificationFailed(e: FirebaseException) {

            errorToast.value = true
            Log.d("GFG", "onVerificationFailed $e")


        }

        override fun onCodeSent(
            VerificationId: String,
            token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
        ) {

            super.onCodeSent(VerificationId, token)
            val ide = VerificationId
            val token = token
            //    navController.navigate("otp/${phonenumber}/$ide/$token/${name}")


        }
    }
    if (errorToast.value) {
        Toast.makeText(LocalContext.current, "Invalid phone number", Toast.LENGTH_SHORT).show()
        sendOptToast.value = false
    }
    if (sendOptToast.value) {
        Toast.makeText(LocalContext.current, "An sms is sent to ${phonenumber}", Toast.LENGTH_SHORT)
            .show()
        sendOptToast.value = false
    }

    val auth = Firebase.auth
    val options =
        PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phonenumber) // Phone number to verify
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS) // Timeout and unit
            .setActivity(ContextUtils.getActivity(LocalContext.current)!!)// Activity (for callback binding)
            .setCallbacks(callBacks) // OnVerificationStateChangedCallbacks
            .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}
