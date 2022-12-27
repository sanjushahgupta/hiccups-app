package hicCups.p.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hicCups.p.forNotification.NotificationClient
import hicCups.p.forNotification.NotificationData
import hicCups.p.forNotification.PushNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit


@Composable
fun Home(navController: NavController){

  Scaffold(topBar = {
      TopAppBar(modifier = Modifier
          .height(30.dp)
          .fillMaxSize()) {
          Text(text = "Hiccups")
          Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more")
          //for logout and Received Hiccups
      }
      
  }) {
      Column(modifier = Modifier
          .fillMaxSize()
          .padding(bottom = 18.dp, start = 18.dp)
      , verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally
      ){
          val send = remember { mutableStateOf(false) }
          Text("Are you missing anyone?")
          val enterPhoneNumber = remember{ mutableStateOf("") }
          
          OutlinedTextField(value = enterPhoneNumber.value, onValueChange = {enterPhoneNumber.value = it}
          , placeholder = { Text(text = "Enter phone number.")})

          Button(onClick = {
              send.value = true
          }) {
              Text(text = "Send Hiccup")

          }
          Divider()
          Text(text = "Recently Sent", fontWeight = FontWeight.Bold, fontSize = 20.sp)
          val TOPIC = "/topics/myTopic"
         // val Topicd = "/topics/$receiveruid"
          if(send.value){
              PushNotification(NotificationData("Hello", "From world"),TOPIC
              ).also {
                  sendNotification(it)
              }
          }
          
          
      }
      
  }

}

fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch{

    try{
val response = NotificationClient().getService().postNotification(notification)
        if(response.isSuccessful){

        }else{
            Log.e("tag", response.errorBody().toString())
        }

    }catch (e: Exception){
        Log.e("tag", e.toString())

    }

}
