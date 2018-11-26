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

        override var params: Map<String, Any> = mapOf()
        override var brushes: Map<String, Paint> = mapOf(
            "base" to Paint(Paint.ANTI_ALIAS_FLAG)
        )

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
        }

        override var visData: EnhancedNotificationDatum = EnhancedNotificationDatum(
            "", 0, 0
        )

        override fun drawEffect(canvas: Canvas?) {
            if(visData.initTime == 0L)
                return
            canvas?.let{
                val cx = it.width/2.toFloat()
                val cy = it.height/2.toFloat()
                val radius = it.height/2.toFloat() * visData.currEnhancement.toFloat()
                it.drawCircle(
                    cx, cy, radius,
                    brushes["base"]!!.also {
                        p -> p.color = Color.argb((visData.currEnhancement.toFloat() * 255f).roundToInt(),0, 0, 255)
                    }
                )
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item : EnhancedAppNotificationData = getItem(position)!!

        return TestViewGroup(context, null).apply{
            setEnhanceData(item)
            setSameVisualEffect(defaultVisualEffect)
        }
    }
}