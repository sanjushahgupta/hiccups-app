package hicCups.p.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@SuppressLint("RestrictedApi")

@Composable
fun SignUp(navController: NavController) {

    val phoneNumber = remember { mutableStateOf("+4915758740660") }
    val SignInButtonStatus = remember {
        mutableStateOf(false)
    }

    lateinit var callBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter phonenumber")
        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it })

        Button(onClick = {SignInButtonStatus.value = true

            Log.d("GFG", "Button clicked")

        }) {
            Text(text = "Submit")
        }

    }



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

                   navController.navigate("otp/${phoneNumber.value}/$ide/$token")


        }
    }


    if(SignInButtonStatus.value){
        val auth = Firebase.auth
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.value) // Phone number to verify
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS) // Timeout and unit
            .setActivity(getActivity(LocalContext.current)!!)// Activity (for callback binding)
            .setCallbacks(callBacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}







@Preview(showBackground = true)
@Composable
fun DefaultsPreview() {
}
