package dev.trindadedev.scrolleffect.cupertino

/*
 * Copyright 2025 The Nxoim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProvideCupertinoOverscrollFactory() =
  LocalOverscrollFactory provides rememberCupertinoOverscrollFactory()

@Composable
fun rememberCupertinoOverscrollFactory(): CupertinoOverscrollEffectFactory {
  val density = LocalDensity.current
  val layoutDirection = LocalLayoutDirection.current

  return remember { CupertinoOverscrollEffectFactory(density, layoutDirection) }
}

data class CupertinoOverscrollEffectFactory(
  private val density: Density,
  private val layoutDirection: LayoutDirection,
) : OverscrollFactory {
  @OptIn(ExperimentalFoundationApi::class)
  override fun createOverscrollEffect() =
    CupertinoOverscrollEffect(density.density, layoutDirection, applyClip = false)
}
