package hicCups.p.Dimension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

class dimension {

    companion object{

        @Composable
        fun width(value: Float): Float{
            val config = LocalConfiguration.current
            return (value * config.screenWidthDp)/ 100
        }

        @Composable
        fun height(value: Float): Float{
            val config = LocalConfiguration.current
            return (value * config.screenHeightDp)/ 100

        }
    }
}
