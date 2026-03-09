package com.example.mapoffice.screens.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DeleteGroupPopUp(onConfirmDelete: () -> Unit, onDimiss: () -> Unit, name: Any) {
    LazyColumn(
        modifier = Modifier
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.small,
                clip = false
            )
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
            .padding(20.dp),
    ) {
        item {
            Text(text = "Are you sure?", fontWeight = FontWeight.Bold)
            Text("Delete $name")
        }
        item {
            Row {
                Button(
                    onClick = onConfirmDelete,
                    modifier = Modifier
                        .padding(8.dp).width(150.dp),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.error,
                    )
                ) {
                    Text("OK")
                }
                Button(
                    onClick = onDimiss,
                    modifier = Modifier
                        .padding(8.dp).width(150.dp),
                    colors = ButtonDefaults.buttonColors(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}