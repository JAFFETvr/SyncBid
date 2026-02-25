package com.jaffetvr.syncbid.features.auth.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jaffetvr.syncbid.core.ui.theme.*

@Composable
fun SyncBidTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isError: Boolean = false,
    isValid: Boolean = false,
    errorMessage: String? = null,
    placeholder: String = "",
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: () -> Unit = {}
) {
    val borderColor = when {
        isError -> Red
        isValid -> Green
        value.isNotEmpty() -> Gold
        else -> White06
    }
    val iconTint = when {
        isError -> Red.copy(alpha = 0.8f)
        isValid -> Green.copy(alpha = 0.8f)
        value.isNotEmpty() -> Gold.copy(alpha = 0.7f)
        else -> White30
    }

    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = White30,
        modifier = modifier.padding(bottom = 7.dp)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (value.isNotEmpty() && !isError) Gold.copy(alpha = 0.04f) else White03,
                shape = RoundedCornerShape(12.dp)
            )
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 13.dp, vertical = 11.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(14.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = White90),
                cursorBrush = SolidColor(Gold),
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = White30
                        )
                    }
                    innerTextField()
                }
            )
            if (trailingIcon != null) {
                IconButton(
                    onClick = onTrailingIconClick,
                    modifier = Modifier.size(14.dp)
                ) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }

    if (errorMessage != null && isError) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(top = 5.dp)
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                color = Red
            )
        }
    }
}

@Composable
fun SyncBidDivider(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(White06)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = White30
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(White06)
        )
    }
}
