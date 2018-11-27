package kr.ac.snu.hcil.durationalnotificationaura

import android.animation.*
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ArrayAdapter
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisEffect
import kotlin.math.roundToInt

class EnhancedNotificationDataAdapter(context: Context, resourceId: Int)
    : ArrayAdapter<EnhancedAppNotificationData>(context, resourceId){

    private val defaultVisualEffect = object: VisEffect() {

        override var visParams: Map<String, Any> = mapOf()
        override var visBrushes: Map<String, Paint> = mapOf(
            "base" to Paint(Paint.ANTI_ALIAS_FLAG)
        )
        override var visData: EnhancedNotificationDatum = EnhancedNotificationDatum(
            "", 0, 0
        )

        override var animParams: Map<String, Any> = mapOf()
        override var animator = AnimatorSet()

        private fun getScaleXYAnimation(targetView: View) = ObjectAnimator.ofPropertyValuesHolder(
            targetView,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.3f, 1f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.3f, 1f)
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
                        p -> p.color = Color.argb((visData.currEnhancement.toFloat() * 255f).roundToInt(),0, 0, 255)
                    }
                )
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item : EnhancedAppNotificationData = getItem(position)!!
        val newView = TestViewGroup(context, null).apply{
            setEnhanceData(item)
            setSameVisualEffect(defaultVisualEffect)
        }
        return newView
    }
}