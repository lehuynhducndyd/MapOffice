package com.example.mapoffice.ui_component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mapoffice.screens.canvas.EditorState
import com.example.mapoffice.screens.canvas.EditorViewModel
import mapoffice.composeapp.generated.resources.Res
import mapoffice.composeapp.generated.resources.ic_oui_close
import org.jetbrains.compose.resources.painterResource

@Composable
fun RightToolbarContent(
    state: EditorState,
    viewModel: EditorViewModel,
    isOpen: () -> Unit,
    navController: NavHostController
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(8.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    navController.navigate("room/detail/price/${state.currentRoom?.id}")
                },
                modifier = Modifier
                    .padding(8.dp).width(150.dp)
            ) {
                Text("Total price")
            }
            IconButton(
                onClick = isOpen,
            ) {
                Icon(
                    painterResource(Res.drawable.ic_oui_close),
                    contentDescription = "close"
                )
            }
        }
        RoomDetail(
            modifier = Modifier,
            viewModel = viewModel,
        )
    }

}


