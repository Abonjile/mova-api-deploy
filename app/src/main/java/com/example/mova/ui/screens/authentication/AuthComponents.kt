package com.example.mova.ui.screens.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.mova.R
import com.example.mova.ui.theme.*

/**
 * Top banner with the MOVA star banner and circular logo overlay.
 * Shared by both Login and Register screens.
 */
@Composable
fun TopBannerSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.banner_image),
            contentDescription = "MOVA banner",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        Surface(
            shape = CircleShape,
            shadowElevation = 8.dp,
            color = MovaOffWhite,
            modifier = Modifier
                .size(110.dp)
                .align(Alignment.BottomCenter)
                .offset(y = 40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.logo_image),
                    contentDescription = "MOVA logo",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}

/**
 * Shared text field with optional error state.
 */
@Composable
fun MovTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = MovaGray) },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MovaOffWhite,
                unfocusedContainerColor = MovaOffWhite,
                focusedBorderColor = MovaTeal,
                unfocusedBorderColor = Color(0xFFE5E7EB),
                errorBorderColor = MovaError
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = MovaError,
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Row of social sign-in buttons: Apple, Google, Biometric.
 */
@Composable
fun SocialLoginRow(
    onGoogleClick: () -> Unit = {},
    onAppleClick: () -> Unit = {},
    onBiometricClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SocialButton(
            icon = painterResource(id = R.drawable.apple_logo),
            onClick = onAppleClick,
            contentDescription = "Sign in with Apple"
        )
        SocialButton(
            icon = painterResource(id = R.drawable.google_icon),
            onClick = onGoogleClick,
            contentDescription = "Sign in with Google"
        )
        SocialButton(
            icon = painterResource(id = R.drawable.fingerprint_icon),
            onClick = onBiometricClick,
            contentDescription = "Sign in with biometric"
        )
    }
}

@Composable
fun SocialButton(icon: Painter, onClick: () -> Unit, contentDescription: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MovaLightGray,
        modifier = Modifier
            .size(60.dp, 40.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = icon,
                contentDescription = contentDescription,
                tint = MovaBlack,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}