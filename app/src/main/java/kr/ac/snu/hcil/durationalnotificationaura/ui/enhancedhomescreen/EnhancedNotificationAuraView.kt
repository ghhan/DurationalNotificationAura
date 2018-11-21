package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.StackedCircleEffect
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisEffect
import java.util.*

enum class EnhancedNotificationLifeCycle{
    STATE_1, //Just Triggered
    STATE_2, //Triggered but Not Interacted
    STATE_3, //Just Interacted
    STATE_4, //Interacted Not Decaying
    STATE_5, // Decaying
}

enum class EnhancementPattern{
    EQ, INC, DEC
}

open class EnhancedNotificationAuraView(context: Context, attrs: AttributeSet?): View(context, attrs) {
    companion object {
        const val ENA_VIEW_TAG = "ENA_VIEW_TAG"
        const val ENA_UPDATE_INTERVAL = 1000L * 30
    }
    open var enhancedAppNotificationData =
        EnhancedAppNotificationData("Not Set")

}

class AnimatedENAView(context:Context, attrs: AttributeSet?): EnhancedNotificationAuraView(context, attrs){

    override var enhancedAppNotificationData: EnhancedAppNotificationData
        get() = super.enhancedAppNotificationData
        set(value) {
            visualEffect.visData = value.notificationData
            invalidate()
        }

    var visualEffect: VisEffect = StackedCircleEffect()
        set(value){
            visualEffect.visData = enhancedAppNotificationData.notificationData
            invalidate()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        visualEffect.animation.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        visualEffect.animation.cancel()
    }

    override fun onDraw(canvas: Canvas?) {
        visualEffect.apply{
            drawEffect(canvas)
        }
    }

}