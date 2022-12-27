package hicCups.p.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.Dimension
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hicCups.p.Dimension.dimension
import hicCups.p.R
import kotlinx.coroutines.delay

@SuppressLint("RestrictedApi")

@Composable
fun SignUp(navController: NavController) {

    val phoneNumber = remember { mutableStateOf("+4915773507453") }
    val SignInButtonStatus = remember {
        mutableStateOf(false)
    }
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            contentColor = Color.Black,
            backgroundColor = Color(176, 104, 187, 255)

        ) {
            Text(text = "Hiccups", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.backgroundColor)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.missing),
                contentDescription = "hiccupsIcon",
                modifier = Modifier
                    .height(70.dp)
                    .width(70.dp)
                    .padding(start = 15.dp)
            )
            Text(
                text = "Sign In",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 15.dp)
            )
            OutlinedTextField(
                value = phoneNumber.value,
                onValueChange = { phoneNumber.value = it },
                placeholder = ({ stringResource(R.string.Enterphonenumber) })
            )

            Button(
                onClick = {
                    SignInButtonStatus.value = true

                    Log.d("GFG", "Button clicked")
                },
                modifier = Modifier.padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple_700))

            ) {
                Text(text = "Continue")
            }
        }


   lateinit var callBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

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


    if (SignInButtonStatus.value) {
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
}







@Preview(showBackground = true)
@Composable
fun DefaultsPreview() {
    SignUp(navController = rememberNavController())
}
