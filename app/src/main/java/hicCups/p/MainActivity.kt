package hicCups.p

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.common.eventbus.EventBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import hicCups.p.navigation.navigationNavController
import hicCups.p.ui.theme.HicCupsTheme
import java.net.CookieHandler.getDefault
import java.net.ResponseCache.getDefault
import java.util.Locale.getDefault
import java.util.TimeZone.getDefault
import javax.security.auth.login.LoginException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


       val auth= FirebaseAuth.getInstance()
        val ownUid = auth.currentUser?.uid
        val Topic = "/topics/$ownUid"
        Log.d("topics","$Topic")


        // val Topic = "/topics/myTopic"
        FirebaseMessaging.getInstance().subscribeToTopic(Topic)

        setContent {
            Login() }
    }
    }


@Composable
fun Login() {
    navigationNavController()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HicCupsTheme {

    }
}
