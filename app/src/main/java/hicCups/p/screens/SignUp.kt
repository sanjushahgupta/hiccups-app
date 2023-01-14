package hicCups.p.screens

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import hicCups.p.R
import hicCups.p.hiccupsViewmodel.hiccupsViewmodel
import java.util.*


@SuppressLint("RestrictedApi", "CoroutineCreationDuringComposition")
@Composable
fun SignUp(navController: NavController) {
    val phoneNumber = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val SignInButtonStatus = remember {
        mutableStateOf(false)
    }
    val focus = LocalFocusManager.current
    val context = LocalContext.current
    val hiccupsViewmodel = hiccupsViewmodel()

    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .wrapContentWidth()
                .height(65.dp), backgroundColor = Color.DarkGray
        ) {
            Text(
                text = "Hiccups",
                modifier = Modifier.padding(start = 20.dp),
                fontSize = 30.sp,
                color = colorResource(id = R.color.LogiTint)
            )

        }
    }) {

        Column(
            modifier = Modifier
                .clickable(
                    MutableInteractionSource(),
                    indication = null,
                    onClick = { focus.clearFocus() })
                .fillMaxSize()
                .background(Color.DarkGray),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                backgroundColor = Color.White, modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    // .fillMaxHeight(0.47f)
                    .wrapContentHeight()
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,

                    ) {
                    OutlinedTextField(
                        value = phoneNumber.value,
                        onValueChange = { phoneNumber.value = it },
                        placeholder = ({
                            Text(
                                "Phone number with country code",
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }),
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = Color.Black,
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.LightGray
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),

                        )


                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it.toString() },
                        placeholder = ({
                            Text(
                                "Enter your name", color = Color.Gray, fontSize = 15.sp
                            )
                        }),
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = Color.Black,
                            backgroundColor = Color.White,
                            focusedIndicatorColor = Color.LightGray
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),

                        )

                    HyperlinkText(
                        fullText = "By registering, you agree to our " + "Terms and Conditions and Privacy Policy.",

                        hyperLinks = mutableMapOf(
                            "Terms and Conditions and Privacy Policy" to "https://digitalaya.com/terms.php",
                        ),
                        textStyle = TextStyle(

                            color = colorResource(id = R.color.gray)
                        ),
                        linkTextColor = colorResource(id = R.color.LogiTint),
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 15.dp, start = 20.dp, end = 20.dp)
                    )

                    Button(
                        onClick = {
                            SignInButtonStatus.value = true
                            focus.clearFocus()
                            Log.d("GFG", "Button clicked")
                        },
                        modifier = Modifier
                            .padding(
                                top = 15.dp, start = 20.dp, end = 20.dp, bottom = 15.dp
                            )
                            .fillMaxWidth(),

                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.LogiTint))

                    ) {
                        Text(text = "Sign In", color = Color.White, fontSize = 15.sp)
                    }

                    if (SignInButtonStatus.value) {
                        if (phoneNumber.value.isEmpty() || name.value.isEmpty()) {

                            Toast.makeText(
                                context, "Please enter your phone number.", Toast.LENGTH_SHORT
                            ).show()
                            SignInButtonStatus.value = false

                        } else {
                            phoneNumber.value = phoneNumber.value.replace("\\s".toRegex(), "")
                            name.value = name.value.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            }
                            hiccupsViewmodel.generateOtpCodeForSignUp(
                                phoneNumber.value, name.value, context, navController
                            )
                            SignInButtonStatus.value = false

                        }
                    }
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun DefaultsPreview() {
    SignUp(navController = rememberNavController())
}

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    fullText: String,
    hyperLinks: Map<String, String>,
    textStyle: TextStyle = TextStyle.Default,
    linkTextColor: Color = Color.Blue,
    linkTextFontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)

        for ((key, value) in hyperLinks) {

            val startIndex = fullText.indexOf(key)
            val endIndex = startIndex + key.length
            addStyle(
                style = SpanStyle(
                    color = linkTextColor,
                    fontSize = fontSize,
                    fontWeight = linkTextFontWeight,

                    ), start = startIndex, end = endIndex
            )
            addStringAnnotation(
                tag = "URL", annotation = value, start = startIndex, end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(
                fontSize = fontSize
            ), start = 0, end = fullText.length
        )
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(modifier = modifier, text = annotatedString, style = textStyle, onClick = {
        annotatedString.getStringAnnotations("URL", it, it).firstOrNull()?.let { stringAnnotation ->
                uriHandler.openUri(stringAnnotation.item)
            }
    })
}
