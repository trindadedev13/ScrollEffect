package dev.trindadedev.scrolleffect.cupertino

/*
 * Copyright 2025 The Aquiles Trindade
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

import androidx.compose.animation.core.generateDecayAnimationSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

@ExperimentalFoundationApi
@Composable
fun CupertinoColumnScroll(
  modifier: Modifier = Modifier,
  scrollState: ScrollState? = rememberScrollState(),
  content: @Composable ColumnScope.() -> Unit
) {
  val density = LocalDensity.current.density
  val layoutDirection = LocalLayoutDirection.current
  val overscrollEffect = remember { CupertinoOverscrollEffect(density, layoutDirection, false) }
  Column(
    modifier = modifier
      .addIfNotNull(scrollState) {
        this.verticalScroll(
          scrollState!!,
          overscrollEffect = overscrollEffect,
          flingBehavior =
            CupertinoFlingBehavior(
              CupertinoScrollDecayAnimationSpec().generateDecayAnimationSpec()
            ),
          )
      }
      .overscroll(overscrollEffect),
    content = content,
  )
}

inline fun <T> Modifier.addIfNotNull(
  value: T?,
  crossinline factory: Modifier.(T) -> Modifier,
): Modifier = if (value != null) factory(value) else this
