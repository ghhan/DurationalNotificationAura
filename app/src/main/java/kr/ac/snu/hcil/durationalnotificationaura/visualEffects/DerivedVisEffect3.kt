package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.graphics.Canvas
import android.support.v7.graphics.Palette
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kotlin.math.roundToInt

class DerivedVisEffect3(
    palette: Palette,
    targetView: View,
    visParams: Map<String, Float>,
    animParams: Map<AnimationTypes, AnimationParams>)
    :AbstractVisEffect(palette, targetView, visParams, animParams) {

    override fun drawVisualEffect(data: NotificationEnhancedData, canvas: Canvas) {
        val paintMap = getPaintMap()
        canvas.let{
            // 화면 대비 비율에 따른 상대적 좌표 사용(0.0f ~ 1.0f)
            var cx = 0.5f
            var cy = 0.5f
            if (getVisParams().containsKey("centerX")) cx = getVisParams()["centerX"]!!
            if (getVisParams().containsKey("centerY")) cy = getVisParams()["centerY"]!!

            var shape = 0
            if (getVisParams().containsKey("shape")) shape = getVisParams()["shape"]!!.roundToInt()

            val radius = minOf(it.width, it.height).toFloat() * 0.5f * 0.85f * data.currEnhancement.toFloat()

            it.drawCircle(
                it.width.toFloat() * cx, it.width.toFloat() * cy, radius,
                paintMap[ColorSwatches.LIGHT_VIBRANT]!!
            )

            it.drawCircle(
                it.width.toFloat() * cx, it.width.toFloat() * cy, radius * 0.9f,
                paintMap[ColorSwatches.DARK_VIBRANT]!!.apply{
                }
            )
        }
    }
}