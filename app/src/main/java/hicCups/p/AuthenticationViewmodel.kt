package hicCups.p

import android.content.ContentValues
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

/*class AuthenticationViewmodel: ViewModel() {

 fun SignIn(phoneNumber:String,credential: PhoneAuthCredential, navController: NavController) {
     val auth = Firebase.auth
     val db = FirebaseFirestore.getInstance()

     auth.signInWithCredential(credential).addOnSuccessListener {

         val uid = auth.currentUser!!.uid

         if (phoneNumber != null) {
             val user: MutableMap<String, Any> = HashMap()
             user["uid"] = uid
             user["phonenumber"] = phoneNumber
             db.collection("users")
                 .add(user)
                 .addOnSuccessListener { documentReference ->
                     navController.navigate("home")
                     Log.d("tag", "SigIn Success")
                 }
                 .addOnFailureListener { e ->
                     Log.w(
                         ContentValues.TAG,
                         "Error adding document",
                         e
                     )
                 }


         }

     }
         .addOnFailureListener {
             Log.d("tagg", "SignIn Failed")

         }


    }
}*/
