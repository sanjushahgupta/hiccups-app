package hicCups.p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hicCups.p.navigation.navigationNavController
import hicCups.p.ui.theme.HicCupsTheme
import javax.security.auth.login.LoginException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Login() }

        }
    }


@Composable
fun Login() {

    navigationNavController()
    /*val db = FirebaseFirestore.getInstance()
    val user: MutableMap<String, Any> = HashMap()
    user["firstName"] = "Sam"
    user["lastName"] = "Sah"

    db.collection("users").add(user).addOnSuccessListener {

    }.addOnFailureListener{}*/

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HicCupsTheme {

    }
}
