package com.milo.app.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.domain.models.UserAccount
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*

@Composable
fun AuthDialog(
    onLogin: (String, String) -> Result<UserAccount>,
    onRegister: (String, String, String) -> Result<UserAccount>,
    onGuest: () -> Unit,
    onDismiss: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MiloCardDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MiloCompanion(
                    emotion = MiloEmotion.Curious,
                    size = 56.dp
                )

                Text(
                    text = if (isRegisterMode) "Create Your Account" else "Welcome to MILO",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MiloWhite
                )

                Text(
                    text = if (isRegisterMode) 
                        "Sync your focus metrics securely across your devices." 
                    else 
                        "Sign in to access your habits, timeblocks, and cloud backup.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MiloZinc400
                )

                // Mode switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MiloBlack, RoundedCornerShape(14.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isRegisterMode) MiloZinc800 else MiloBlack)
                            .clickable {
                                isRegisterMode = false
                                errorMessage = null
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (!isRegisterMode) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (!isRegisterMode) MiloWhite else MiloZinc400
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isRegisterMode) MiloZinc800 else MiloBlack)
                            .clickable {
                                isRegisterMode = true
                                errorMessage = null
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Register",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isRegisterMode) MiloWhite else MiloZinc400
                        )
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MiloAmber,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = null },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MiloWhite,
                            unfocusedTextColor = MiloWhite,
                            focusedBorderColor = MiloWhite,
                            unfocusedBorderColor = MiloZinc700,
                            focusedLabelColor = MiloWhite,
                            unfocusedLabelColor = MiloZinc400
                        )
                    )
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = null },
                    label = { Text("Email Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MiloWhite,
                        unfocusedTextColor = MiloWhite,
                        focusedBorderColor = MiloWhite,
                        unfocusedBorderColor = MiloZinc700,
                        focusedLabelColor = MiloWhite,
                        unfocusedLabelColor = MiloZinc400
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password (min 6 characters)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MiloWhite,
                        unfocusedTextColor = MiloWhite,
                        focusedBorderColor = MiloWhite,
                        unfocusedBorderColor = MiloZinc700,
                        focusedLabelColor = MiloWhite,
                        unfocusedLabelColor = MiloZinc400
                    )
                )

                Button(
                    onClick = {
                        isLoading = true
                        val result = if (isRegisterMode) {
                            onRegister(name, email, password)
                        } else {
                            onLogin(email, password)
                        }
                        isLoading = false
                        if (result.isSuccess) {
                            onDismiss()
                        } else {
                            errorMessage = result.exceptionOrNull()?.message ?: "Authentication failed"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MiloWhite, contentColor = MiloBlack),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isRegisterMode) "CREATE ACCOUNT" else "SIGN IN",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                TextButton(
                    onClick = {
                        onGuest()
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Continue as Guest (Offline Mode)",
                        color = MiloZinc400,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
