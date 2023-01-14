package hicCups.p.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import hicCups.p.R
import hicCups.p.hiccupsViewmodel.hiccupsViewmodel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint(
    "UnusedMaterialScaffoldPaddingParameter",
    "CoroutineCreationDuringComposition",
    "SuspiciousIndentation",
    "UnrememberedMutableState"
)

@Composable
fun Home(navController: NavController) {
    val focus = LocalFocusManager.current

    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .wrapContentHeight()
                .wrapContentHeight(),
            backgroundColor = Color.DarkGray

        ) {

            var expanded = remember { mutableStateOf(false) }

            Image(
                painter = painterResource(id = R.drawable.missing),
                contentDescription = "hiccups image",
                modifier = Modifier
                    .size(50.dp)
                    .padding(top = 5.dp, start = 16.dp)
            )
            Text(
                text = "Hiccups",
                color = colorResource(id = R.color.LogiTint),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { expanded.value = true }) {
                    if (expanded.value) {
                        navController.navigate("receivedetails")
                        expanded.value = false

                    }
                    Icon(
                        painter = painterResource(id = R.drawable.sentlisthiccups),
                        "",
                        tint = colorResource(
                            id = R.color.LogiTint
                        ),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
                .background(Color.White),
            Alignment.TopCenter
        ) {


            Text(
                "Missing someone?",
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 30.dp, start = 20.dp),
                color = colorResource(id = R.color.LogiTint)
            )



            Column(
                modifier = Modifier
                    .clickable(MutableInteractionSource(),
                        indication = null,
                        onClick = { focus.clearFocus() })
                    .fillMaxSize()
                    .padding(bottom = 18.dp, start = 18.dp, end = 18.dp, top = 60.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.padding(8.dp))
                Column(
                    verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start
                ) {
                    UIWithContact()
                }

            }

        }

    }

}

@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.O)
@Preview()
@Composable
fun UIWithContact() {
    val context = LocalContext.current
    var phoneNumber = remember { mutableStateOf("") }
    val focus = LocalFocusManager.current

    //create a intent variable
    val contactIntent = Intent(Intent.ACTION_PICK).apply {
        type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
    }

    val launchContactForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = result.data?.data

            val projection: Array<String> = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )

            contactUri?.let {
                context.contentResolver.query(it, projection, null, null, null).use { cursor ->
                    // If the cursor returned is valid, get the phone number and (or) name
                    if (cursor!!.moveToFirst()) {
                        val numberIndex =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        phoneNumber.value = cursor.getString(numberIndex)

                    }
                }
            }
        }
    }

    val launchContactPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchContactForResult.launch(contactIntent)
        } else {
            Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }

    }

    OutlinedTextField(value = phoneNumber.value,
        onValueChange = { phoneNumber.value = it },
        trailingIcon = {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_import_contacts_24),
                contentDescription = "import contacts",
                tint = colorResource(id = R.color.LogiTint),
                modifier = Modifier.clickable {
                    when (PackageManager.PERMISSION_GRANTED) {
                        //First time asking for permission ... to be granted by user
                        ContextCompat.checkSelfPermission(
                            context, android.Manifest.permission.READ_CONTACTS
                        ) -> {
                            launchContactForResult.launch(contactIntent)
                        }
                        else -> {
                            //If permission has been already granted
                            launchContactPermission.launch(android.Manifest.permission.READ_CONTACTS)
                        }
                    }
                })
        },
        colors = TextFieldDefaults.textFieldColors(cursorColor = Color.Black, backgroundColor = Color.White, focusedIndicatorColor = Color.LightGray),
        placeholder = {
            Text(
                text = "Enter phone number", color = colorResource(id = R.color.LogiTint)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )


    val send = remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val senderPhone = auth.currentUser?.phoneNumber
    val hiccupsViewmodel = hiccupsViewmodel()

    Spacer(modifier = Modifier.padding(4.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(
            onClick = {
                send.value = true
                focus.clearFocus()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            modifier = Modifier.padding(start = 80.dp),
        ) {
            Text(text = "Send Hiccup", color = colorResource(id = R.color.LogiTint))
        }
    }
    if (send.value) {
        if (phoneNumber.value.isEmpty()) {
            Toast.makeText(LocalContext.current, "Invalid input", Toast.LENGTH_SHORT).show()
        } else {
            phoneNumber.value = phoneNumber.value.replace("\\s".toRegex(), "")

            if (phoneNumber.value == senderPhone) {
                Toast.makeText(
                    LocalContext.current, "You cannot send yourself hiccup.", Toast.LENGTH_SHORT
                ).show()
                send.value = false
            } else {

                hiccupsViewmodel.sendHiccups(phoneNumber.value, context)
                send.value = false
            }
        }
    }

    hiccupsViewmodel.ReceivedDetailsListFromFirebaseDB()

}
