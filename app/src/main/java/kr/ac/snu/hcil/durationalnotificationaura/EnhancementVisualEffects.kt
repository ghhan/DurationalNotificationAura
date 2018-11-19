package kr.ac.snu.hcil.durationalnotificationaura

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.roundToInt

abstract class VisEffect(view: View) {
    abstract var params: Map<String, Any>
    abstract var brushes: Map<String, Paint>
    abstract fun drawEffect(canvas: Canvas?)
}

class StackedCircleEffect(view: View): VisEffect(view){

    override var params: Map<String, Any> = mapOf()
    override var brushes: Map<String, Paint> = mapOf()
    private val targetView = view

    var visData = EnhancedAppNotificationData()

    private fun allocateRadius(minVal: Float, maxVal: Float): List<Float> {
        return List(visData.notificationData.size){index -> minVal + index * (maxVal - minVal) / visData.notificationData.size}
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

    override fun drawEffect(canvas: Canvas?) {
        if(visData.notificationData.size == 0)
            return
        val width = targetView.width
        val height = targetView.height
        val allocatedRadius = allocateRadius(width/4f, width/2f)
        canvas?.apply{
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