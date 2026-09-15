package org.astroalarm.widget

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import org.astroalarm.astro.model.AlarmTarget

/** Color plus shape so alarm marks are not red-only. */
object AlarmDotMark {
    enum class Shape { Circle, Diamond, Square }

    fun shapeOf(target: AlarmTarget): Shape = when (target) {
        is AlarmTarget.Solar, is AlarmTarget.SolarTerm -> Shape.Circle
        is AlarmTarget.Lunar -> Shape.Diamond
        else -> Shape.Square
    }

    fun draw(canvas: Canvas, x: Float, y: Float, r: Float, paint: Paint, shape: Shape) {
        when (shape) {
            Shape.Circle -> canvas.drawCircle(x, y, r, paint)
            Shape.Square -> canvas.drawRect(x - r, y - r, x + r, y + r, paint)
            Shape.Diamond -> {
                val path = Path()
                path.moveTo(x, y - r)
                path.lineTo(x + r, y)
                path.lineTo(x, y + r)
                path.lineTo(x - r, y)
                path.close()
                canvas.drawPath(path, paint)
            }
        }
    }
}
