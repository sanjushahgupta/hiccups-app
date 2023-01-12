package hicCups.p.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import java.util.jar.Manifest

@SuppressLint("RestrictedApi")

class contactsDetails: ComponentActivity()  {

    // on below line we are creating variable
        // for contact name and contact number
        var contactName = mutableStateOf("")
        var contactNumber = mutableStateOf("")





    // on below line we are calling on activity result method.
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            // on below line we are checking if result code is ok or not.
            if (resultCode != Activity.RESULT_OK) return

            // on below line we are checking if data is not null.
            if (requestCode === 1 && data != null) {
                // on below lin we are getting contact data
                val contactData: Uri? = data.data

                // on below line we are creating a cursor
                val cursor: Cursor = managedQuery(contactData, null, null, null, null)

                // on below line we are moving cursor.
                cursor.moveToFirst()

                // on below line we are getting our
                // number and name from cursor
                val number: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val name: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

                // on the below line we are setting values.
                contactName.value = name
                contactNumber.value = number

            }

        }
    }

    // on below line we are creating
// contact picker function.
    @Composable
    fun contactPicker(
        context: Context,
        contactName: String,
        contactNumber: String,
    ) {
        // on below line we are creating variable for activity.
        val activity = LocalContext.current as Activity

        // on below line we are creating a column,
        Column(
            // on below line we are adding a modifier to it,
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {








            // on below line adding a spacer


            // on below line creating a button to pick contact.
            Button(
                // on below line adding on click for button.
                onClick = {
                    // on below line checking if permission is granted.
                    if (hasContactPermission(context)) {
                        // if permission granted open intent to pick contact/
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                        startActivityForResult(activity, intent, 1, null)
                    } else {
                        // if permission not granted requesting permission .
                        requestContactPermission(context, activity)
                    }
                },

            ) {
                // displaying text in our button.
                Text(text = "Pick Contact")
                Text("$contactNumber")

            }
        }
    }

    fun hasContactPermission(context: Context): Boolean {
        // on below line checking if permission is present or not.
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) ==
              PackageManager.PERMISSION_GRANTED;
    }

    fun requestContactPermission(context: Context, activity: Activity) {
        // on below line if permission is not granted requesting permissions.
        if (!hasContactPermission(context)) {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.READ_CONTACTS), 1)
        }
    }
