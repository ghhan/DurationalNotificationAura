package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
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

    companion object {
        const val TAG = "Notification_Aura_View"
    }
    private var visualData: EnhancedNotificationDatum? = null
    private var visualEffect: VisEffect? = null
    private var dirtyBit:Boolean = true

    init{
        setBackgroundColor(Color.TRANSPARENT)
        setWillNotDraw(false)
    }

    fun setVisualData(visData: EnhancedNotificationDatum){
        visualData = visData
        visualEffect?.visData = visData
        dirtyBit = true
        invalidate()
        //requestLayout()
    }

    fun setVisualEffect(visEffect: VisEffect){
        visualEffect = visEffect
        visualData?.let{
            visualEffect!!.visData = it
        }
        dirtyBit = true
        visualEffect!!.initializeAnimator(this)
        invalidate()
        //requestLayout()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(TAG,"Measure Sequence: $measuredWidth, $measuredHeight")
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        when(visibility){
            View.VISIBLE -> {
                visualEffect?.animator?.start()
            }
            View.GONE -> {
                visualEffect?.animator?.cancel()
            }
            View.INVISIBLE -> {
                visualEffect?.animator?.pause()
            }
        }
    }
    override fun onDraw(canvas: Canvas?) {
        canvas?.apply{
            visualEffect?.drawVisualization(canvas)
        }
        if(dirtyBit){
            visualEffect?.animator?.let{
               if(!it.isRunning){
                    it.start()
                }
                dirtyBit = false
            }
        }
    }
}