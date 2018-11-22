package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.animation.*
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import kotlin.math.roundToInt

class StackedCircleGroupEffect: VisGroupEffect(){

    override var params: Map<String, Any> = mapOf()
    override var brushes: Map<String, Paint> = mapOf()

    override var visData: List<EnhancedNotificationDatum> = listOf()

    private fun allocateRadius(minVal: Float, maxVal: Float): List<Float> {
        return List(visData.size){index -> minVal + index * (maxVal - minVal) / visData.size}
    }

    private val paintList = listOf(
        Paint(Paint.ANTI_ALIAS_FLAG).also{
            it.color = Color.MAGENTA
        },
        Paint(Paint.ANTI_ALIAS_FLAG).also{
            it.color = Color.MAGENTA
        }
    )

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).also{
        it.style = Paint.Style.FILL
    }

    private val scaleXYAnimation = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.3f, 1f),
        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.3f, 1f)
    ).apply{
        duration = 2000
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = 20
        interpolator = AccelerateDecelerateInterpolator()
    }

    private val jitterXYAnimation = ObjectAnimator.ofPropertyValuesHolder(
        this,
        PropertyValuesHolder.ofFloat(View.TRANSLATION_X, 10f, 0f, -10f, 0f),
        PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 10f, 0f, -10f, 0f)
    ).apply{
        duration = 1000
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = 40
        interpolator = AccelerateDecelerateInterpolator()
    }

    override var animation = AnimatorSet().apply{
        play(jitterXYAnimation).with(scaleXYAnimation)
        addListener(object: AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?, isReverse: Boolean) {
                super.onAnimationStart(animation, isReverse)
            }
            override fun onAnimationPause(animation: Animator?) {
                super.onAnimationPause(animation)
            }
            override fun onAnimationResume(animation: Animator?) {
                super.onAnimationResume(animation)
            }
            override fun onAnimationRepeat(animation: Animator?) {
                super.onAnimationRepeat(animation)
            }
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                super.onAnimationEnd(animation, isReverse)
            }
            override fun onAnimationCancel(animation: Animator?) {
                super.onAnimationCancel(animation)
            }
        })
    }

    override fun drawEffect(canvas: Canvas?) {
        if(visData.isEmpty())
            return
        canvas?.apply{
            val width = canvas.width
            val height = canvas.height
            val allocatedRadius = allocateRadius(width/4f, width/2f)
            for(radius:Float in allocatedRadius){
                drawCircle(
                    width/2.toFloat(),
                    height/2.toFloat(),
                    radius,
                    circlePaint.also{
                        it.color = Color.argb(((radius / (width/2f)) * 255f).roundToInt(),0, 0, 255)
                    }
                )
            }
        }
    }
}