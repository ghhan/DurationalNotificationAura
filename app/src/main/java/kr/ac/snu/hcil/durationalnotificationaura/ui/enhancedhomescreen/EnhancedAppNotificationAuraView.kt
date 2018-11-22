package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.StackedCircleGroupEffect
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisEffect
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisGroupEffect

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

open class EnhancedAppNotificationAuraView(context: Context, attrs: AttributeSet?): View(context, attrs) {
    companion object {
        const val ENA_VIEW_TAG = "ENA_VIEW_TAG"
        const val ENA_UPDATE_INTERVAL = 1000L * 30
    }
    open var enhancedAppNotificationData = EnhancedAppNotificationData("Not Set")
}
class AnimatedENAView(context:Context, attrs: AttributeSet?): EnhancedAppNotificationAuraView(context, attrs){

    override var enhancedAppNotificationData: EnhancedAppNotificationData
        get() = super.enhancedAppNotificationData
        set(value) {
            visualGroupEffect.visData = value.notificationData
            invalidate()
        }

    var visualGroupEffect: VisGroupEffect = StackedCircleGroupEffect()
        set(value){
            visualGroupEffect.visData = enhancedAppNotificationData.notificationData
            invalidate()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        visualGroupEffect.animation.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        visualGroupEffect.animation.cancel()
    }

    override fun onDraw(canvas: Canvas?) {
        visualGroupEffect.apply{
            drawEffect(canvas)
        }
    }

}

class EnhancedNotificationAuraView(context: Context, attrs: AttributeSet?): View(context, attrs){

    private var visualData: EnhancedNotificationDatum? = null
    private var visualEffect: VisEffect? = null
    private var dirtyBit:Boolean = true

    fun setVisualData(visData: EnhancedNotificationDatum){
        visualData = visData
        visualEffect?.visData = visData
        dirtyBit = true
        invalidate()
    }

    fun setVisualEffect(visEffect: VisEffect){
        visualEffect = visEffect
        visualData?.let{
            visualEffect!!.visData = it
        }
        dirtyBit = true
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        visualEffect?.animation?.start()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        visualEffect?.animation?.cancel()
    }
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when(visibility){
            View.VISIBLE -> {
                visualEffect?.animation?.start()
            }
            View.GONE -> {
                visualEffect?.animation?.cancel()
            }
            View.INVISIBLE -> {
                visualEffect?.animation?.pause()
            }
        }
    }
    override fun onDraw(canvas: Canvas?) {
        canvas?.apply{
            visualEffect?.drawEffect(canvas)
        }
        if(dirtyBit){
            visualEffect?.animation?.let{
                if(it.isRunning){
                    it.cancel()
                    it.start()
                }
                else{
                    it.start()
                }
                dirtyBit = false
            }
        }
    }
}

