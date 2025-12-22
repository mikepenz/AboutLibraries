package com.mikepenz.aboutlibraries.sample.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ToggleableSetting(
    useV3: Boolean,
    title: String,
    icon: ImageVector,
    enabled: Boolean,
    onToggled: (Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(interactionSource = interactionSource, onClick = { onToggled(!enabled) }, indication = ripple())
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        if (useV3) {
            Icon(icon, contentDescription = title)
            Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Switch(interactionSource = interactionSource, checked = enabled, onCheckedChange = {
                onToggled(!enabled)
            })
        } else {
            androidx.compose.material.Icon(icon, contentDescription = title)
            androidx.compose.material.Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            androidx.compose.material.Switch(interactionSource = interactionSource, checked = enabled, onCheckedChange = {
                onToggled(!enabled)
            })
        }
    }
}