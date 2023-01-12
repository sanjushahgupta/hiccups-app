package hicCups.p.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hicCups.p.R
import hicCups.p.forNotification.HiccupsDetails
import hicCups.p.hiccupsViewmodel.hiccupsViewmodel

@Composable
fun ReceivedDetails(navController: NavController) {
var hiccupsViewmodel= hiccupsViewmodel()
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            backgroundColor = Color.DarkGray

        ) {
            Text(text = "Hiccups", fontSize = 18.sp, color =colorResource(id = R.color.LogiTint))

        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

           hiccupsViewmodel.FetchSenderDataFromFireBaseDB()

        }
        }
}
