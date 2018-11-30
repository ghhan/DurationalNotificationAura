package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.animation.AnimatorSet
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData

abstract class VisGroupEffect {
    abstract var params: Map<String, Any>
    abstract var brushes: Map<String, Paint>
    abstract var visData: List<NotificationEnhancedData>
    abstract var animation: AnimatorSet
    abstract fun drawEffect(canvas: Canvas?)
}

abstract class VisEffect{
    abstract var visParams: Map<String, Any>
    abstract var visBrushes: Map<String, Paint>
    abstract var visData: NotificationEnhancedData
    abstract fun drawVisualization(canvas: Canvas?)
    
    abstract var animParams: Map<String, Any>
    abstract var animator: AnimatorSet
    abstract fun initializeAnimator(target: View)
}