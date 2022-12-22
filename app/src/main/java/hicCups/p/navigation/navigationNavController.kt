package hicCups.p.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hicCups.p.screens.Home
import hicCups.p.screens.Otp
import hicCups.p.screens.ReceivedDetails
import hicCups.p.screens.SignUp


@Composable
fun navigationNavController() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signUp") {
        composable("signUp") {
            SignUp(navController = navController)
        }

        composable("otp/{phonenumber}/{verificationCode}/{token}") {
            Otp(it.arguments?.getString("phonenumber").toString(),
                it.arguments?.getString("verificationCode").toString(),
                it.arguments?.getString("token").toString(),
                navController = navController)
        }

        composable("home") {
            Home(navController = navController)
        }

        composable("receivedetails") {
            ReceivedDetails(navController = navController)
        }

    }

}
