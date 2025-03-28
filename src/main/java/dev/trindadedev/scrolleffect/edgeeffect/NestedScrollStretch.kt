package dev.trindadedev.scrolleffect.edgeeffect

/*
 * Copyright 2021, Lawnchair.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Velocity

/**
 * Creates a custom overscroll effect based off the Android 12 "stretch" animation.
 *
 * @param content The content to animate.
 *
 * TODO: Allow horizontal stretch
 */
@Composable
fun NestedScrollStretch(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val invalidateTick = remember { mutableIntStateOf(0) }
  val invalidate = Runnable { invalidateTick.intValue++ }

  val context = LocalContext.current
  val connection = remember { NestedScrollStretchConnection(context, invalidate) }

  val tmpOut = remember { FloatArray(5) }

  Box(
    modifier =
      modifier
        .nestedScroll(connection)
        .onSizeChanged {
          connection.height = it.height
          connection.topEdgeEffect.setSize(it.width, it.height)
          connection.bottomEdgeEffect.setSize(it.width, it.height)
        }
        .drawWithContent {
          // Redraw when this value changes
          invalidateTick.intValue

          connection.topEdgeEffect.draw(tmpOut, StretchEdgeEffect.POSITION_TOP, this) {
            connection.bottomEdgeEffect.draw(tmpOut, StretchEdgeEffect.POSITION_BOTTOM, this) {
              drawContent()
            }
          }
        }
  ) {
    content()
  }
}

private inline fun StretchEdgeEffect.draw(
  tmpOut: FloatArray,
  @StretchEdgeEffect.EdgeEffectPosition position: Int,
  scope: DrawScope,
  crossinline block: () -> Unit,
) {
  if (isFinished) {
    block()
    return
  }

  tmpOut[0] = 0f
  getScale(tmpOut, position)
  if (tmpOut[0] == 1f) {
    scope.scale(tmpOut[1], tmpOut[2], pivot = Offset(tmpOut[3], tmpOut[4])) { block() }
  } else {
    block()
  }
}

private class NestedScrollStretchConnection(context: Context, invalidate: Runnable) :
  NestedScrollConnection {

  var height = 0

  val topEdgeEffect = StretchEdgeEffect(context, invalidate, invalidate)
  val bottomEdgeEffect = StretchEdgeEffect(context, invalidate, invalidate)

  override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
    val availableY = available.y
    when {
      source != NestedScrollSource.UserInput || height == 0 -> return Offset.Zero
      availableY != 0f -> {
        if (availableY < 0f) {
          val consumed = topEdgeEffect.onPullDistance(availableY / height, 0f)
          if (topEdgeEffect.distance == 0f) topEdgeEffect.onRelease()
          return Offset(0f, consumed * height)
        }
        if (availableY > 0f) {
          val consumed = bottomEdgeEffect.onPullDistance(-availableY / height, 0f)
          if (bottomEdgeEffect.distance == 0f) bottomEdgeEffect.onRelease()
          return Offset(0f, -consumed * height)
        }
      }
    }
    return Offset.Zero
  }

  override fun onPostScroll(
    consumed: Offset,
    available: Offset,
    source: NestedScrollSource,
  ): Offset {
    val availableY = available.y
    when {
      source != NestedScrollSource.UserInput || height == 0 -> return Offset.Zero
      availableY != 0f -> {
        if (availableY > 0f) {
          topEdgeEffect.onPull(availableY / height)
          return Offset.Zero
        } else {
          bottomEdgeEffect.onPull(-availableY / height)
        }
      }
      else -> {
        topEdgeEffect.onRelease()
        bottomEdgeEffect.onRelease()
      }
    }
    return available
  }

  override suspend fun onPreFling(available: Velocity): Velocity {
    topEdgeEffect.onRelease()
    bottomEdgeEffect.onRelease()
    return Velocity.Zero
  }

  override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
    if (height == 0) return Velocity.Zero
    val availableY = available.y
    if (availableY > 0f) {
      topEdgeEffect.onAbsorb(availableY.toInt())
      return Velocity.Zero
    } else {
      bottomEdgeEffect.onAbsorb(-availableY.toInt())
    }
    return Velocity(0f, availableY)
  }
}
