package hicCups.p.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import hicCups.p.R
import hicCups.p.util.userPreference
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("RestrictedApi", "CoroutineCreationDuringComposition")
@Composable
fun SignUp(navController: NavController) {
    val phoneNumber = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val SignInButtonStatus = remember {
        mutableStateOf(false)
    }
    var focus = LocalFocusManager.current

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
                .clickable(
                    MutableInteractionSource(),
                    indication = null,
                    onClick = { focus.clearFocus() })
                .fillMaxSize()
                .background(colorResource(id = R.color.backgroundColor)),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(top = 60.dp))
            Image(
                painter = painterResource(id = R.drawable.missing),
                contentDescription = "hiccupsIcon",
                modifier = Modifier
                    .height(50.dp)
                    .width(70.dp)

            )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            OutlinedTextField(
                value = phoneNumber.value,
                onValueChange = { phoneNumber.value = it.toString() },
                placeholder = ({ Text("Phonenumber with country code") }),
                modifier = Modifier.wrapContentSize(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),

                )
            Spacer(modifier = Modifier.padding(top = 10.dp))
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it.toString() },
                placeholder = ({ Text("Enter your name") }),
                modifier = Modifier.wrapContentSize(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),

                )

            Button(
                onClick = {
                    SignInButtonStatus.value = true
                    focus.clearFocus()
                    Log.d("GFG", "Button clicked")
                },
                modifier = Modifier.padding(top = 15.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.purple_700))

            ) {
                Text(text = "Sign In")
            }
        }


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

                SignInButtonStatus.value = false
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
                navController.navigate("otp/${phoneNumber.value}/$ide/$token/${name.value}")


            }
        }
        if (errorToast.value) {
            Toast.makeText(LocalContext.current, "Invalid phone number", Toast.LENGTH_SHORT).show()
            sendOptToast.value = false
        }
        if (sendOptToast.value) {
            Toast.makeText(LocalContext.current, "An sms is sent to ${phoneNumber.value}", Toast.LENGTH_SHORT).show()
            sendOptToast.value = false
        }


        if (SignInButtonStatus.value) {

            phoneNumber.value = phoneNumber.value.replace("\\s".toRegex(), "")
            if (phoneNumber.value.isEmpty()) {

                Toast.makeText(LocalContext.current, "Please enter your phone number.", Toast.LENGTH_SHORT)
                    .show()
                SignInButtonStatus.value = false

            }else {
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
}


@Preview(showBackground = true)
@Composable
fun DefaultsPreview() {
    SignUp(navController = rememberNavController())
}
