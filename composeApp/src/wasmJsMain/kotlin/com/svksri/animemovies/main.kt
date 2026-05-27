package com.svksri.animemovies

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    onWasmReady {
        ComposeViewport("ComposeTarget") {
            MoviesApp(
                onNavHostReady = { navController ->
                    navController.bindToBrowserNavigation()
                }
            )
        }
    }
}
