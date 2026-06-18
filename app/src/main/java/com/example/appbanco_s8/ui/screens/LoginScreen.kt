package com.example.appbanco_s8.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import com.example.appbanco_s8.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.appbanco_s8.ui.theme.AzulOscuroGNB
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appbanco_s8.ui.theme.*
import com.example.appbanco_s8.ui.viewmodel.AuthUiState
import com.example.appbanco_s8.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (token: String, email: String) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val s = uiState as AuthUiState.Success
            onLoginSuccess(s.token, s.email)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(FondoCrema, VerdeSage, FondoCrema)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo GNB (Recreación visual premium orgánica de alto contraste) ──
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn() + slideInVertically(initialOffsetY = { -40 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    
                    Surface(
                        modifier = Modifier.size(100.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                        shape = RoundedCornerShape(28.dp),
                        shadowElevation = 3.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.gnb_logo),
                                contentDescription = "GNB Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "BANCO",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Light,
                        color = VerdeBosqueOscuro,
                        letterSpacing = 6.sp
                    )
                    Text(
                        text = "GNB",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = VerdeBotonForest,
                        modifier = Modifier.offset(y = (-6).dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Card de login ───────────────────────────────
            Surface(
                shape          = RoundedCornerShape(32.dp),
                color          = Color.White,
                border         = androidx.compose.foundation.BorderStroke(1.dp, BordeSuave),
                modifier       = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text       = "Ingreso Clientes",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = VerdeBosqueOscuro,
                        textAlign  = TextAlign.Center,
                        modifier   = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Usuario / Email") },
                        leadingIcon   = {
                            Icon(Icons.Default.Email, null, tint = VerdeBotonForest)
                        },
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction    = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(50.dp),
                        colors   = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = FondoCrema,
                            focusedTextColor     = VerdeBosqueOscuro,
                            unfocusedTextColor   = VerdeBosqueOscuro,
                            focusedLabelColor    = VerdeBotonForest,
                            unfocusedLabelColor  = GrisSage,
                            focusedBorderColor   = VerdeBotonForest,
                            unfocusedBorderColor = BordeSuave,
                            cursorColor          = VerdeBotonForest,
                            focusedLeadingIconColor   = VerdeBotonForest,
                            unfocusedLeadingIconColor = GrisSage
                        )
                    )

                    OutlinedTextField(
                        value         = password,
                        onValueChange = { password = it },
                        label         = { Text("Contraseña") },
                        leadingIcon   = {
                            Icon(Icons.Default.Lock, null, tint = VerdeBotonForest)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff
                                    else
                                        Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = GrisSage
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.login(email.trim(), password)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(50.dp),
                        colors   = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = FondoCrema,
                            focusedTextColor     = VerdeBosqueOscuro,
                            unfocusedTextColor   = VerdeBosqueOscuro,
                            focusedLabelColor    = VerdeBotonForest,
                            unfocusedLabelColor  = GrisSage,
                            focusedBorderColor   = VerdeBotonForest,
                            unfocusedBorderColor = BordeSuave,
                            cursorColor          = VerdeBotonForest,
                            focusedLeadingIconColor   = VerdeBotonForest,
                            unfocusedLeadingIconColor = GrisSage
                        )
                    )

                    AnimatedVisibility(visible = uiState is AuthUiState.Error) {
                        Text(
                            text     = (uiState as? AuthUiState.Error)?.mensaje ?: "",
                            color    = RojoError,
                            fontSize = 13.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick  = {
                            focusManager.clearFocus()
                            viewModel.login(email.trim(), password)
                        },
                        enabled  = uiState !is AuthUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape  = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeBotonForest,
                            contentColor   = Color.White,
                            disabledContainerColor = VerdeBotonForest.copy(alpha = 0.5f),
                            disabledContentColor   = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("INGRESAR", fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                        }
                    }

                    TextButton(
                        onClick  = { },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("¿Olvidó su contraseña?", color = VerdeBosqueOscuro, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text          = "Banco GNB Perú S.A.",
                fontSize      = 12.sp,
                color         = GrisSage,
                fontWeight    = FontWeight.Bold
            )
        }
    }
}
