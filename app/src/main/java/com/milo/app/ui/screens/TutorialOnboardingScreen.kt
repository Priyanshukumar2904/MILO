package com.milo.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.milo.app.domain.models.MiloEmotion
import com.milo.app.domain.models.UserAccount
import com.milo.app.ui.mascot.MiloCompanion
import com.milo.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TutorialOnboardingScreen(
    initialPage: Int = 0,
    isReauthMode: Boolean = false,
    onLogin: (String, String) -> Result<UserAccount>,
    onRegister: (String, String, String) -> Result<UserAccount>,
    onGuest: () -> Unit,
    onComplete: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    val totalPages = 4
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { totalPages })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MiloBlack),
        containerColor = MiloBlack,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left badge or back / close button
                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .background(MiloCardDark, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MiloWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(MiloGreen, CircleShape)
                        )
                        Text(
                            text = "MILO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = MiloZinc400
                        )
                    }
                }

                // Skip button (only shown before slide 3)
                if (pagerState.currentPage < 3 && !isReauthMode) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(3)
                            }
                        }
                    ) {
                        Text(
                            text = "Skip",
                            style = MaterialTheme.typography.labelMedium,
                            color = MiloZinc400
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        },
        bottomBar = {
            if (pagerState.currentPage < 3 && !isReauthMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Page indicator dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(totalPages) { pageIndex ->
                            val isSelected = pagerState.currentPage == pageIndex
                            Box(
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(if (isSelected) 24.dp else 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isSelected) MiloWhite else MiloZinc700)
                            )
                        }
                    }

                    // Next button
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MiloWhite,
                            contentColor = MiloBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Next",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            userScrollEnabled = !isReauthMode || pagerState.currentPage < 3
        ) { page ->
            when (page) {
                0 -> WelcomeTutorialSlide()
                1 -> WorkflowTutorialSlide()
                2 -> PrivacyTutorialSlide()
                3 -> AuthorizationSlide(
                    isReauthMode = isReauthMode,
                    onLogin = onLogin,
                    onRegister = onRegister,
                    onGuest = onGuest,
                    onComplete = onComplete
                )
            }
        }
    }
}

@Composable
private fun WelcomeTutorialSlide() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MiloCompanion(
            emotion = MiloEmotion.Welcoming,
            size = 110.dp
        )

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .background(MiloCardDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "MINIMALIST PRODUCTIVITY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MiloZinc400
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Meet Milo",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = MiloWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your quiet, distraction-free companion built to help you master time, build meaningful habits, and reclaim your daily focus.",
            style = MaterialTheme.typography.bodyLarge,
            color = MiloZinc400,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Feature badge pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            PillBadge(label = "Zero Distractions")
            PillBadge(label = "Pure Focus")
            PillBadge(label = "Clean Design")
        }
    }
}

@Composable
private fun WorkflowTutorialSlide() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MiloCompanion(
            emotion = MiloEmotion.Encouraging,
            size = 96.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .background(MiloCardDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "DAILY FLOW & MASTERY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MiloZinc400
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Plan, Act & Reflect",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = MiloWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Three simple pillars designed to keep you moving forward every day.",
            style = MaterialTheme.typography.bodyMedium,
            color = MiloZinc400,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TutorialCard(
                icon = Icons.Default.Timer,
                title = "Live Zen Focus Timer",
                description = "Enter flow states with minimalist countdown & stopwatch sessions."
            )
            TutorialCard(
                icon = Icons.Default.Bolt,
                title = "Micro-Habit Consistency",
                description = "Compound daily streaks with satisfying, zero-friction check-ins."
            )
            TutorialCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "0–100 Momentum Score",
                description = "Objective daily feedback celebrating consistency over burnout."
            )
        }
    }
}

@Composable
private fun PrivacyTutorialSlide() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MiloCompanion(
            emotion = MiloEmotion.Proud,
            size = 96.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .background(MiloCardDark, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "PRIVACY BY DESIGN",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MiloGreen
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "100% On-Device & Private",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = MiloWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your thoughts, habits, and schedules belong solely to you.",
            style = MaterialTheme.typography.bodyMedium,
            color = MiloZinc400,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TutorialCard(
                icon = Icons.Default.Security,
                title = "Hardware KeyStore AES-256",
                description = "Credentials & personal vault are secured in on-device hardware."
            )
            TutorialCard(
                icon = Icons.Default.CloudOff,
                title = "Complete Offline Independence",
                description = "Works seamlessly anywhere with zero mandatory internet."
            )
            TutorialCard(
                icon = Icons.Default.Lock,
                title = "Zero Ad Tracking & Telemetry",
                description = "No invasive profiling, analytics cookies, or behavioral trackers."
            )
        }
    }
}

@Composable
private fun AuthorizationSlide(
    isReauthMode: Boolean,
    onLogin: (String, String) -> Result<UserAccount>,
    onRegister: (String, String, String) -> Result<UserAccount>,
    onGuest: () -> Unit,
    onComplete: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(!isReauthMode) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        MiloCompanion(
            emotion = MiloEmotion.Curious,
            size = 72.dp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = if (isReauthMode) "Welcome Back" else "Authorize Your Space",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MiloWhite,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isRegisterMode) 
                "Create your on-device secure profile to begin your journey." 
            else 
                "Sign in with your saved credentials on this device.",
            style = MaterialTheme.typography.bodyMedium,
            color = MiloZinc400,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Mode switch (Sign In / Register)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MiloCardDark, RoundedCornerShape(14.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isRegisterMode) MiloZinc800 else Color.Transparent)
                    .clickable {
                        isRegisterMode = true
                        errorMessage = null
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = if (isRegisterMode) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isRegisterMode) MiloWhite else MiloZinc400
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isRegisterMode) MiloZinc800 else Color.Transparent)
                    .clickable {
                        isRegisterMode = false
                        errorMessage = null
                    }
                    .padding(vertical = 10.dp),
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message banner
        AnimatedVisibility(
            visible = errorMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MiloAmber.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = errorMessage!!,
                        color = MiloAmber,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }

        // Input Fields
        if (isRegisterMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; errorMessage = null },
                label = { Text("Your Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MiloWhite,
                    unfocusedTextColor = MiloWhite,
                    focusedBorderColor = MiloWhite,
                    unfocusedBorderColor = MiloZinc700,
                    focusedLabelColor = MiloWhite,
                    unfocusedLabelColor = MiloZinc400
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Email or Username") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MiloWhite,
                unfocusedTextColor = MiloWhite,
                focusedBorderColor = MiloWhite,
                unfocusedBorderColor = MiloZinc700,
                focusedLabelColor = MiloWhite,
                unfocusedLabelColor = MiloZinc400
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password (min 4 characters)") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = MiloZinc400
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MiloWhite,
                unfocusedTextColor = MiloWhite,
                focusedBorderColor = MiloWhite,
                unfocusedBorderColor = MiloZinc700,
                focusedLabelColor = MiloWhite,
                unfocusedLabelColor = MiloZinc400
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
            onClick = {
                focusManager.clearFocus()
                isSubmitting = true
                val result = if (isRegisterMode) {
                    onRegister(name, email, password)
                } else {
                    onLogin(email, password)
                }
                isSubmitting = false
                if (result.isSuccess) {
                    onComplete()
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Authentication failed"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MiloWhite,
                contentColor = MiloBlack
            ),
            shape = RoundedCornerShape(14.dp),
            enabled = !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MiloBlack,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (isRegisterMode) "GET STARTED" else "SIGN IN",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Guest / Offline fallback
        TextButton(
            onClick = {
                onGuest()
                onComplete()
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

@Composable
private fun PillBadge(label: String) {
    Box(
        modifier = Modifier
            .background(MiloCardDark, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MiloZinc300
        )
    }
}

@Composable
private fun TutorialCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MiloCardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MiloBlack, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MiloWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MiloWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MiloZinc400,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
