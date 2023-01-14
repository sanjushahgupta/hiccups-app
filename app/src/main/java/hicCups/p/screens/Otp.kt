package hicCups.p.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hicCups.p.R
import hicCups.p.forNotification.user
import hicCups.p.hiccupsViewmodel.hiccupsViewmodel
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
    name: String,
    navController: NavController
) {

    val otpCode = remember { mutableStateOf("") }
    val submitButtonStatus = remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current
    val resendbuttonClick = remember { mutableStateOf(false) }
    val hiccupsViewmodel = hiccupsViewmodel()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Blue)
            .clickable(
                MutableInteractionSource(),
                indication = null,
                onClick = { focus.clearFocus() })
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = 18.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = otpCode.value,
            onValueChange = { otpCode.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = { Text(text = "Enter otp code") },
            colors = TextFieldDefaults.textFieldColors(
                cursorColor = Color.Black,
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.LightGray
            ),
            modifier = Modifier.padding(bottom = 20.dp)


        )

        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = {
                    submitButtonStatus.value = true
                    focus.clearFocus()
                },
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text("Submit Opt", color = colorResource(id = R.color.LogiTint))
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Text("Resend verification code",
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier
                    .padding(bottom = 12.dp, top = 8.dp)
                    .clickable(onClick = { resendbuttonClick.value = true })
            )
            if (resendbuttonClick.value) {
                hiccupsViewmodel.generateOtpCodeForSignUp(phonenumber, name, context, navController)
                resendbuttonClick.value = false
            }

        }


        if (submitButtonStatus.value) {


            if (otpCode.value.isEmpty()) {

                Toast.makeText(context, "Invalid input.", Toast.LENGTH_SHORT).show()
            } else {
                val credential: PhoneAuthCredential =
                    PhoneAuthProvider.getCredential(verificationcode, otpCode.value)
                SignIn(credential, navController, name)
            }


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
    val context = LocalContext.current
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
                Toast.makeText(context, "SigIn Success", Toast.LENGTH_SHORT).show()
                navController.navigate("home")
                Log.d("tag", "SigIn Success")
            }

            .addOnFailureListener {
                errorToast.value = true
              //  Toast.makeText(context, "SigIn failed", Toast.LENGTH_SHORT).show()
            }


    }.addOnFailureListener {
        Toast.makeText(context, "SigIn failed", Toast.LENGTH_SHORT).show()
        errorToast.value = true
    }


}
