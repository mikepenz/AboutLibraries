package com.mikepenz.markdown.sample.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Debug: ImageVector
    get() {
        if (_Debug != null) return _Debug!!

        _Debug = ImageVector.Builder(
            name = "Debug",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(3.463f, 12.86f)
                lineToRelative(-0.005f, -0.07f)
                lineToRelative(0.005f, 0.07f)
                close()
                moveToRelative(7.264f, 0.69f)
                lineToRelative(-3.034f, -3.049f)
                lineToRelative(1.014f, -1.014f)
                lineToRelative(3.209f, 3.225f)
                lineToRelative(3.163f, -3.163f)
                lineToRelative(1.014f, 1.014f)
                lineToRelative(-3.034f, 3.034f)
                lineToRelative(3.034f, 3.05f)
                lineToRelative(-1.014f, 1.014f)
                lineToRelative(-3.209f, -3.225f)
                lineTo(8.707f, 17.6f)
                lineToRelative(-1.014f, -1.014f)
                lineToRelative(3.034f, -3.034f)
                close()
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(16.933f, 5.003f)
                verticalLineTo(6f)
                horizontalLineToRelative(1.345f)
                lineToRelative(2.843f, -2.842f)
                lineToRelative(1.014f, 1.014f)
                lineToRelative(-2.692f, 2.691f)
                lineToRelative(0.033f, 0.085f)
                arcToRelative(13.75f, 13.75f, 0f, false, true, 0.885f, 4.912f)
                curveToRelative(0f, 0.335f, -0.011f, 0.667f, -0.034f, 0.995f)
                lineToRelative(-0.005f, 0.075f)
                horizontalLineToRelative(3.54f)
                verticalLineToRelative(1.434f)
                horizontalLineToRelative(-3.72f)
                lineToRelative(-0.01f, 0.058f)
                curveToRelative(-0.303f, 1.653f, -0.891f, 3.16f, -1.692f, 4.429f)
                lineToRelative(-0.06f, 0.094f)
                lineToRelative(3.423f, 3.44f)
                lineToRelative(-1.017f, 1.012f)
                lineToRelative(-3.274f, -3.29f)
                lineToRelative(-0.099f, 0.11f)
                curveToRelative(-1.479f, 1.654f, -3.395f, 2.646f, -5.483f, 2.646f)
                curveToRelative(-2.12f, 0f, -4.063f, -1.023f, -5.552f, -2.723f)
                lineToRelative(-0.098f, -0.113f)
                lineToRelative(-3.209f, 3.208f)
                lineToRelative(-1.014f, -1.014f)
                lineToRelative(3.366f, -3.365f)
                lineToRelative(-0.059f, -0.095f)
                curveToRelative(-0.772f, -1.25f, -1.34f, -2.725f, -1.636f, -4.34f)
                lineToRelative(-0.01f, -0.057f)
                horizontalLineTo(0f)
                verticalLineTo(12.93f)
                horizontalLineToRelative(3.538f)
                lineToRelative(-0.005f, -0.075f)
                arcToRelative(14.23f, 14.23f, 0f, false, true, -0.034f, -0.995f)
                curveToRelative(0f, -1.743f, 0.31f, -3.39f, 0.863f, -4.854f)
                lineToRelative(0.032f, -0.084f)
                lineToRelative(-2.762f, -2.776f)
                lineTo(2.65f, 3.135f)
                lineTo(5.5f, 6f)
                horizontalLineToRelative(1.427f)
                verticalLineToRelative(-0.997f)
                arcToRelative(5.003f, 5.003f, 0f, false, true, 10.006f, 0f)
                close()
                moveToRelative(-8.572f, 0f)
                verticalLineTo(6f)
                horizontalLineTo(15.5f)
                verticalLineToRelative(-0.997f)
                arcToRelative(3.569f, 3.569f, 0f, false, false, -7.138f, 0f)
                close()
                moveToRelative(9.8f, 2.522f)
                lineToRelative(-0.034f, -0.09f)
                horizontalLineTo(5.733f)
                lineToRelative(-0.034f, 0.09f)
                arcToRelative(12.328f, 12.328f, 0f, false, false, -0.766f, 4.335f)
                curveToRelative(0f, 2.76f, 0.862f, 5.201f, 2.184f, 6.92f)
                curveToRelative(1.32f, 1.716f, 3.036f, 2.649f, 4.813f, 2.649f)
                curveToRelative(1.777f, 0f, 3.492f, -0.933f, 4.813f, -2.65f)
                curveToRelative(1.322f, -1.718f, 2.184f, -4.16f, 2.184f, -6.919f)
                curveToRelative(0f, -1.574f, -0.28f, -3.044f, -0.766f, -4.335f)
                close()
            }
        }.build()

        return _Debug!!
    }

private var _Debug: ImageVector? = null

