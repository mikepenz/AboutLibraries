package com.mikepenz.aboutlibraries.sample.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("ObjectPropertyName")
private var _Github: ImageVector? = null

val Github: ImageVector
    get() {
        if (_Github != null) {
            return _Github!!
        }
        _Github = ImageVector.Builder(
            name = "Github",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 2f)
                arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 12f)
                curveTo(2f, 16.42f, 4.87f, 20.17f, 8.84f, 21.5f)
                curveTo(9.34f, 21.58f, 9.5f, 21.27f, 9.5f, 21f)
                curveTo(9.5f, 20.77f, 9.5f, 20.14f, 9.5f, 19.31f)
                curveTo(6.73f, 19.91f, 6.14f, 17.97f, 6.14f, 17.97f)
                curveTo(5.68f, 16.81f, 5.03f, 16.5f, 5.03f, 16.5f)
                curveTo(4.12f, 15.88f, 5.1f, 15.9f, 5.1f, 15.9f)
                curveTo(6.1f, 15.97f, 6.63f, 16.93f, 6.63f, 16.93f)
                curveTo(7.5f, 18.45f, 8.97f, 18f, 9.54f, 17.76f)
                curveTo(9.63f, 17.11f, 9.89f, 16.67f, 10.17f, 16.42f)
                curveTo(7.95f, 16.17f, 5.62f, 15.31f, 5.62f, 11.5f)
                curveTo(5.62f, 10.39f, 6f, 9.5f, 6.65f, 8.79f)
                curveTo(6.55f, 8.54f, 6.2f, 7.5f, 6.75f, 6.15f)
                curveTo(6.75f, 6.15f, 7.59f, 5.88f, 9.5f, 7.17f)
                curveTo(10.29f, 6.95f, 11.15f, 6.84f, 12f, 6.84f)
                curveTo(12.85f, 6.84f, 13.71f, 6.95f, 14.5f, 7.17f)
                curveTo(16.41f, 5.88f, 17.25f, 6.15f, 17.25f, 6.15f)
                curveTo(17.8f, 7.5f, 17.45f, 8.54f, 17.35f, 8.79f)
                curveTo(18f, 9.5f, 18.38f, 10.39f, 18.38f, 11.5f)
                curveTo(18.38f, 15.32f, 16.04f, 16.16f, 13.81f, 16.41f)
                curveTo(14.17f, 16.72f, 14.5f, 17.33f, 14.5f, 18.26f)
                curveTo(14.5f, 19.6f, 14.5f, 20.68f, 14.5f, 21f)
                curveTo(14.5f, 21.27f, 14.66f, 21.59f, 15.17f, 21.5f)
                curveTo(19.14f, 20.16f, 22f, 16.42f, 22f, 12f)
                arcTo(10f, 10f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 2f)
                close()
            }
        }.build()
        return _Github!!
    }

