package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AbstractVisEffect

class EnhancedNotificationAuraView(context: Context, attrs: AttributeSet?): View(context, attrs){

    companion object {
        const val TAG = "NOTIFICATION_AURA_VIEW"
    }

    init{
        clipToOutline = false
    }

    private var visualData: NotificationEnhancedData? = null
    private var visualEffect: AbstractVisEffect? = null

    fun setVisualData(visData: NotificationEnhancedData){
        visualData = visData
        visualEffect?.let{
            if(it.getCurrLifeCycle() != visData.lifeCycle)
                it.setCurrentStage(visData.lifeCycle)
        }
    }

    fun setVisualEffect(visEffect: AbstractVisEffect){
        visualEffect = visEffect
        visualData?.let{
            if(it.lifeCycle != visualEffect!!.getCurrLifeCycle())
                visualEffect!!.setCurrentStage(it.lifeCycle)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        when(visibility){
            View.VISIBLE -> {
                visualEffect?.getAnimatorSet()?.let{
                    if(it.isPaused) it.resume()
                    else it.start()
                }
            }
            View.GONE -> {
                visualEffect?.getAnimatorSet()?.cancel()
            }
            View.INVISIBLE -> {
                visualEffect?.getAnimatorSet()?.let{
                    if(it.isRunning) it.pause()
                }
            }
        }
    }
    
    override fun onDraw(canvas: Canvas?) {
        canvas?.let{
//            it.drawRGB(0, 255, 255) // for debug
            visualData?.let{
                data -> visualEffect?.drawVisualEffect(data, it)
            }
            Log.d(TAG, "visbility: ${visibility == View.VISIBLE}, $tag, canvas size: ${it.width}, ${it.height}, view size: $width, $height")
        }
    }
}