package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.animation.AnimatorSet
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum

abstract class VisEffect {
    abstract var params: Map<String, Any>
    abstract var brushes: Map<String, Paint>
    abstract var visData: List<EnhancedNotificationDatum>
    abstract var animation: AnimatorSet
    abstract fun drawEffect(canvas: Canvas?)
}