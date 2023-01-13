package hicCups.p.navigation

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hicCups.p.screens.*
import kotlin.system.exitProcess


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun navigationNavController() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {

        composable("welcome"){
            Welcome(navController = navController)
            BackHandler() {
                exitProcess(1)
            }
        }


        composable("signUp") {
            SignUp(navController = navController)
            BackHandler() {
                exitProcess(1)
            }
        }

        composable("otp/{phonenumber}/{verificationCode}/{token}/{name}") {
            Otp(
                it.arguments?.getString("phonenumber").toString(),
                it.arguments?.getString("verificationCode").toString(),
                it.arguments?.getString("token").toString(),
                it.arguments?.getString("name").toString(),
                navController = navController
            )
            BackHandler() {
            }
        }

        composable("home") {
            Home(navController = navController)
            BackHandler() {
            }
        }

        composable("receivedetails") {
            ReceivedDetails(navController = navController)

        }

    }

}
