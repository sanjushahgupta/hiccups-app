package hicCups.p

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import hicCups.p.navigation.navigationNavController
import hicCups.p.ui.theme.HicCupsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Login()
        }
    }

}


@Composable
fun Login() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        navigationNavController()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HicCupsTheme {

    }
}
