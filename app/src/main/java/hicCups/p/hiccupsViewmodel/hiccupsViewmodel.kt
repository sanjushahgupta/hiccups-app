package hicCups.p.hiccupsViewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class hiccupsViewmodel: ViewModel() {

    var newStringFromStoredData = mutableStateListOf<String>("")

    init {
        var receiverList = mutableListOf<String>()

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val phon = auth.currentUser?.phoneNumber

        db.collection("HiccupsDetails").get().addOnSuccessListener {
            it.documents.forEach {
                //if(it.get("Receiver").toString().equals(phon.toString())){

                var x = it.get("Receiver")
              //  receiverList.add(x.toString())
                newStringFromStoredData.add(x.toString())


            }
            //    Log.d("hcv1", "$receiverList")


        }

    }
}
