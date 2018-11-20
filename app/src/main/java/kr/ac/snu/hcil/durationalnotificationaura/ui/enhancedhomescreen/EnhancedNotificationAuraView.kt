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
    /* Auto Refresh Module
     * Update Enhancement Level For Every ENA_UPDATE_INTERVAL
     * When notiEnhanceList updates,
     */
    private val mHandler = Handler()
    private var lastUpdateInMillis = Calendar.getInstance().timeInMillis
    private fun updateNotiEnhancement(notiData: EnhancedNotificationDatum, updateInterval: Long): EnhancedNotificationDatum {
        when(notiData.lifeCycle){
            EnhancedNotificationLifeCycle.STATE_2 -> {
                if(notiData.timeElapsed >= notiData.naturalDecay){
                    notiData.lifeCycle =
                            EnhancedNotificationLifeCycle.STATE_5
                }
                else {
                    when (notiData.firstPattern) {
                        EnhancementPattern.DEC -> {
                            notiData.currEnhancement -= notiData.slope * updateInterval
                            if (notiData.currEnhancement < notiData.lowerBound)
                                notiData.currEnhancement = notiData.lowerBound
                        }
                        EnhancementPattern.INC -> {
                            notiData.currEnhancement += notiData.slope * updateInterval
                            if (notiData.currEnhancement > notiData.upperBound)
                                notiData.currEnhancement = notiData.upperBound
                        }
                        EnhancementPattern.EQ -> {}
                    }
                }
            }
            EnhancedNotificationLifeCycle.STATE_4 -> {
                if(notiData.timeElapsed >= notiData.naturalDecay){
                    notiData.lifeCycle =
                            EnhancedNotificationLifeCycle.STATE_5
                }
                else{
                    when(notiData.secondPattern){
                        EnhancementPattern.DEC -> {
                            notiData.currEnhancement -= notiData.slope * updateInterval
                            if(notiData.currEnhancement < notiData.lowerBound)
                                notiData.currEnhancement = notiData.lowerBound
                        }
                        EnhancementPattern.INC -> {
                            notiData.currEnhancement += notiData.slope * updateInterval
                            if(notiData.currEnhancement > notiData.upperBound)
                                notiData.currEnhancement = notiData.upperBound
                        }
                        EnhancementPattern.EQ -> {}
                    }
                }
            }
            EnhancedNotificationLifeCycle.STATE_1 -> {
                // Proceed STATE_1 -> STATE_2
                notiData.lifeCycle =
                        EnhancedNotificationLifeCycle.STATE_2
                when(notiData.firstPattern){
                    EnhancementPattern.INC -> notiData.slope = (notiData.upperBound - notiData.enhanceOffset) / notiData.firstSaturationTime
                    EnhancementPattern.DEC -> notiData.slope = (notiData.enhanceOffset - notiData.lowerBound) / notiData.firstSaturationTime
                    EnhancementPattern.EQ -> notiData.slope = 0.0
                }
            }
            EnhancedNotificationLifeCycle.STATE_3 -> {
                // Proceed STATE_3 -> STATE_4
                notiData.lifeCycle =
                        EnhancedNotificationLifeCycle.STATE_4
                when(notiData.secondPattern){
                    EnhancementPattern.INC -> notiData.slope = (notiData.upperBound - notiData.currEnhancement) / notiData.secondSaturationTime
                    EnhancementPattern.DEC -> notiData.slope = (notiData.currEnhancement - notiData.lowerBound) / notiData.secondSaturationTime
                    EnhancementPattern.EQ -> notiData.slope = 0.0
                }
            }
            EnhancedNotificationLifeCycle.STATE_5 -> {
                //currently drop to 0
                notiData.currEnhancement = 0.0
            }
        }
        notiData.timeElapsed += updateInterval
        return notiData
    }
    private val autoUpdateRunnable = object: Runnable{
        override fun run() {
            val nowInMillis = Calendar.getInstance().timeInMillis
            enhancedAppNotificationData.notificationData = enhancedAppNotificationData.notificationData.map{ updateNotiEnhancement(it, nowInMillis - lastUpdateInMillis) }.toMutableList()
            enhancedAppNotificationData.notificationData.removeAll(
                enhancedAppNotificationData.notificationData.filter{it.lifeCycle == EnhancedNotificationLifeCycle.STATE_5 && it.currEnhancement <= 0.0})
            lastUpdateInMillis = nowInMillis
            invalidate()
            mHandler.postDelayed(this,
                ENA_UPDATE_INTERVAL
            )
        }
    }

    /* List Update With Synchronicity
     */
    open fun addEnhancedNotificationData(enhanceDatum: EnhancedNotificationDatum){
        enhancedAppNotificationData.notificationData.add(enhanceDatum)
        /* Added because of the Synchronicity of Update
         */
        mHandler.removeCallbacks(autoUpdateRunnable)
        mHandler.post(autoUpdateRunnable)
    }
    open fun addAllEnhancedNotificationData(vararg enhanceData: EnhancedNotificationDatum){
        enhancedAppNotificationData.notificationData.addAll(enhanceData)
        /* Added because of the Synchronicity of Update
        */
        mHandler.removeCallbacks(autoUpdateRunnable)
        mHandler.post(autoUpdateRunnable)
    }
    open fun deleteEnhancedNotificationData(packageName: String, initTime: Long){
        enhancedAppNotificationData.notificationData.removeAll(
            enhancedAppNotificationData.notificationData.filter{
                it.initTime == initTime
            })
        if(enhancedAppNotificationData.notificationData.size == 0)
            mHandler.removeCallbacks(autoUpdateRunnable)
    }
    open fun clearEnhancedNotificationDataList(){
        enhancedAppNotificationData.notificationData.clear()
        mHandler.removeCallbacks(autoUpdateRunnable)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(enhancedAppNotificationData.notificationData.size != 0)
            mHandler.removeCallbacks(autoUpdateRunnable)
            mHandler.post(autoUpdateRunnable)
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandler.removeCallbacks(autoUpdateRunnable)
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        when (visibility){
            View.GONE -> {
                mHandler.removeCallbacks(autoUpdateRunnable)
            }
            View.INVISIBLE -> {
                mHandler.removeCallbacks(autoUpdateRunnable)
            }
            View.VISIBLE -> {
                mHandler.removeCallbacks(autoUpdateRunnable)
                mHandler.post(autoUpdateRunnable)
            }
        }
    }
}

class AnimatedENAView(context:Context, attrs: AttributeSet?): EnhancedNotificationAuraView(context, attrs){

    override var enhancedAppNotificationData: EnhancedAppNotificationData
        get() = super.enhancedAppNotificationData
        set(value) {
            visualEffect.visData = value.notificationData
            invalidate()
        }

    var visualEffect: VisEffect =
        StackedCircleEffect(this)
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