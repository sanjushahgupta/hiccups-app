package hicCups.p.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hicCups.p.R

@Composable
fun ReceivedDetails(navController: NavController) {

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
            Text(text = "Received Hiccups")
           getreceiverList()
        }
        }
}

@Composable
fun getreceiverList() {
    var receiverList = mutableListOf<String>()
    var senderList = mutableListOf<String>()
    var receiverListState = remember {
        mutableStateOf<String>("")
    }
    var senderListState = remember{ mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val phon = auth.currentUser?.phoneNumber

    db.collection("HiccupsDetails").get().addOnSuccessListener {
        it.documents.forEach {
           // if(it.get("Receiver").toString().equals(phon.toString())){
            if (it.get("receiver").toString().equals(phon.toString())) {
                var x = it.get("sender")
                receiverList.add(x.toString())
            }



        }

        receiverListState.value= receiverList.toString()

    }
    Text("Received From")
    Spacer(modifier = Modifier.padding(bottom = 25.dp))
    Text("${receiverListState.value} ")

}
