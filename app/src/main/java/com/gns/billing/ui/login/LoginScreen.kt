package com.gns.billing.ui.login

import android.util.Log
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gns.billing.model.LoginState
import com.gns.billing.session.SessionManager
import com.gns.billing.viewmodel.LoginViewModel
import com.gns.billing.session.SessionProvider

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel()
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {

        when (val state = loginState) {

            is LoginState.Success -> {

                val loginResponse = state.response

                val loginData = loginResponse.data

                if (loginData != null) {

                    sessionManager.saveSession(
                        token = loginData.token,
                        name = loginData.user.name,
                        email = loginData.user.email
                    )
                    SessionProvider.token = loginData.token
                    android.widget.Toast.makeText(
                        context,
                        "Token: ${sessionManager.getToken()}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()

                    Log.d("SESSION", "===================================")
                    Log.d("SESSION", "TOKEN : ${sessionManager.getToken()}")
                    Log.d("SESSION", "NAMA  : ${sessionManager.getName()}")
                    Log.d("SESSION", "EMAIL : ${sessionManager.getEmail()}")
                    Log.d("SESSION", "===================================")

                    navController.navigate("dashboard") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }

                }

            }

            else -> {}

        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "GNS Billing",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Silakan login",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        loginViewModel.login(email, password)
                    }
                ) {
                    Text("LOGIN")
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (loginState) {

                    LoginState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is LoginState.Error -> {
                        Text(
                            text = (loginState as LoginState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    else -> {}
                }

            }

        }

    }

}