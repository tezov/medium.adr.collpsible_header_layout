package com.tezov.medium.adr.collpsible_header_layout

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tezov.medium.adr.collpsible_header_layout.ExtensionDensity.toDp
import com.tezov.medium.adr.collpsible_header_layout.ExtensionDensity.toPx
import kotlin.math.abs
import kotlin.math.sign

object ColumnCollapsibleHeader {

    data class Properties(
        val min: Dp = 0.dp,
        val max: Dp = Dp.Infinity,
    )

    @Composable
    private fun rememberCollapsibleHeaderState(
        maxPxToConsume: Float,
        initialProgress: Float = 1f,
        initialScroll: Int = 0,
    ) = remember {
        CollapsibleHeaderState(maxPxToConsume, initialProgress, initialScroll)
    }

    private class CollapsibleHeaderState(
        private val maxPxToConsume: Float,
        initialProgress: Float,
        initialScroll: Int = 0,
    ) : NestedScrollConnection {

        val scrollState = ScrollState(initialScroll)
        private val _progress = mutableStateOf(initialProgress)

        var progress: Float
            get() = _progress.value
            set(value) {
                pxConsumed = maxPxToConsume * (1f - value)
            }

        private var pxConsumed: Float = maxPxToConsume * (1f - initialProgress)
            set(value) {
                field = value
                _progress.value = (1f - value / maxPxToConsume)
            }

        val progressPx get() = maxPxToConsume * progress

        private fun isDirectionPositive(value: Float) = value.sign < 0f

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            return if (isDirectionPositive(available.y)) {
                onDirectionPositive(available)
            } else {
                onDirectionNegative(available)
            }
        }

        private fun onDirectionPositive(available: Offset): Offset {
            if (progress <= 0f) {
                return Offset.Zero
            }
            val allowedToBeConsumed = maxPxToConsume - pxConsumed
            val notConsumed = (abs(available.y) - allowedToBeConsumed)
            if (notConsumed <= 0f) {
                pxConsumed -= available.y
                return available
            }
            pxConsumed = maxPxToConsume.toFloat()
            return Offset(0f, -allowedToBeConsumed)
        }

        private fun onDirectionNegative(available: Offset): Offset {
            if (progress >= 1f) {
                return Offset.Zero
            }
            val availableToBeConsumed = available.y - scrollState.value
            if (availableToBeConsumed <= 0f) {
                return Offset.Zero
            }
            val allowedToBeConsumed = pxConsumed
            val notConsumed = availableToBeConsumed - allowedToBeConsumed
            if (notConsumed <= 0) {
                pxConsumed -= availableToBeConsumed
                return available
            }
            pxConsumed = 0f
            return Offset(0f, allowedToBeConsumed)

        }
    }

    @Composable
    operator fun invoke(
        modifier: Modifier = Modifier,
        properties: Properties,
        header: @Composable BoxScope.(progress: Float, progressDp: Dp) -> Unit,
        body: @Composable ColumnScope.() -> Unit,
    ) {
        val density = LocalDensity.current.density
        val sizePx = remember {
            (properties.max - properties.min).toPx(density)
        }
        val collapsibleHeaderState =
            rememberCollapsibleHeaderState(sizePx)
        Column(
            modifier = modifier
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                header(
                    collapsibleHeaderState.progress,
                    properties.min + collapsibleHeaderState.progressPx.toDp(density)
                )
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(collapsibleHeaderState)
                    .verticalScroll(collapsibleHeaderState.scrollState)
            ) {
                body()
            }
        }

    }
}