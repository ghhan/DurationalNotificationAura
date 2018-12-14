package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.graphics.Canvas
import android.support.v7.graphics.Palette
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData

class DerivedVisEffect2(
    palette: Palette,
    targetView: View,
    visParams: Map<String, Float>,
    animParams: Map<AnimationTypes, AnimationParams>)
    :AbstractVisEffect(palette, targetView, visParams, animParams) {

    override fun drawVisualEffect(data: NotificationEnhancedData, canvas: Canvas) {
        val paintMap = getPaintMap()
        canvas.let{
            val cx = it.width/2.toFloat()
            val cy = it.height/2.toFloat()
            val radius = minOf(it.width, it.height).toFloat() * 0.5f * 0.85f * data.currEnhancement.toFloat()

            it.drawCircle(
                cx, cy, radius,
                paintMap[ColorSwatches.LIGHT_VIBRANT]!!
            )

            it.drawCircle(
                cx, cy, radius * 0.9f,
                paintMap[ColorSwatches.DARK_VIBRANT]!!.apply{
                }
            )
        }
    }
}