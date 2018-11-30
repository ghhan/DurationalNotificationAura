package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kotlin.math.roundToInt
import kotlin.random.Random

class DefaultVisEffect: VisEffect() {

    companion object {
        const val TAG = "VIS_EFFECT"
    }

    override var visParams: Map<String, Any> = mapOf()
    override var visBrushes: Map<String, Paint> = mapOf(
        "base" to Paint(Paint.ANTI_ALIAS_FLAG).also{it.style = Paint.Style.FILL_AND_STROKE},
        "stroke" to Paint(Paint.ANTI_ALIAS_FLAG).also{it.style= Paint.Style.STROKE; it.color = Color.YELLOW})

    override var visData: NotificationEnhancedData = NotificationEnhancedData("", 0, 0)
    override var animParams: Map<String, Any> = mapOf()
    override var animator = AnimatorSet()

    private fun getScaleXYAnimation(targetView: View) = ObjectAnimator.ofPropertyValuesHolder(
        targetView,
        PropertyValuesHolder.ofFloat(View.SCALE_X, Random.nextFloat(), 1f),
        PropertyValuesHolder.ofFloat(View.SCALE_Y, Random.nextFloat(), 1f)
    ).apply{
        duration = 2000
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = 20
        interpolator = AccelerateDecelerateInterpolator()
    }

    private fun getJitterXYAnimation(targetView: View) = ObjectAnimator.ofPropertyValuesHolder(
        targetView,
        PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 10f, 0f, -10f, 0f),
        PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 10f, 0f, -10f, 0f)
    ).apply{
        duration = 1000
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = 40
        interpolator = AccelerateDecelerateInterpolator()
    }

    override fun initializeAnimator(target: View) {
        animator = AnimatorSet().apply{
            play(getScaleXYAnimation(target)).with(getJitterXYAnimation(target))
        }
    }

    override fun drawVisualization(canvas: Canvas?) {
        if(visData.initTime == 0L)
            return
        canvas?.let{

            val cx = it.width/2.toFloat()
            val cy = it.height/2.toFloat()

            val radius = it.height/2.toFloat() * visData.currEnhancement.toFloat()
            it.drawCircle(
                cx, cy, radius,
                visBrushes["base"]!!.also {
                        p -> p.color = Color.argb(100 + (visData.currEnhancement.toFloat() * 155f).roundToInt(),0, 0, 255)
                }
            )
            it.drawCircle(
                cx, cy, radius,
                visBrushes["stroke"]!!
            )
            Log.d(TAG, "canvas_w: ${it.width}, canvas_h: ${it.height}, cx: $cx, cy: $cy, r: $radius")
        }
    }
}