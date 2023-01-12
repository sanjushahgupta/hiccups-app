package hicCups.p.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import hicCups.p.util.userPreference
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("RestrictedApi", "CoroutineCreationDuringComposition")

@Composable
fun Welcome(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val ownUid = auth.currentUser?.uid
    val Topic = "/topics/$ownUid"
    Log.d("topics", "$Topic")

    FirebaseMessaging.getInstance().subscribeToTopic(Topic)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = userPreference(context)

    scope.launch {
        dataStore.loginStatus.collect {
            val token = it.toString()
            async {
                delay(600)
                if (token.equals("loggedIn")) {
                    navController.navigate("home")
                } else {
                    navController.navigate("signUp")
                }
            }.await()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = Modifier.size(64.dp))
    }

}
