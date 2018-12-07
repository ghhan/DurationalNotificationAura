package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisEffect

class EnhancedNotificationAuraView(context: Context, attrs: AttributeSet?): View(context, attrs){

    companion object {
        const val TAG = "NOTIFICATION_AURA_VIEW"
    }
    private var visualData: NotificationEnhancedData? = null
    private var visualEffect: VisEffect? = null
    private var dirtyBit:Boolean = true

    init{
    }

    fun setVisualData(visData: NotificationEnhancedData){
        visualData = visData
        visualEffect?.visData = visData
        dirtyBit = true
        //invalidate()
        //requestLayout()
    }

    fun setVisualEffect(visEffect: VisEffect){
        visualEffect = visEffect
        visualData?.let{
            visualEffect!!.visData = it
        }
        dirtyBit = true
        visualEffect!!.initializeAnimator(this)
        //invalidate()
        //requestLayout()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
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
        canvas?.let{
            it.drawRGB(255, 0, 0)
            visualEffect?.drawVisualization(it)
            Log.d(TAG, "visbility: ${visibility == View.VISIBLE}, $tag, canvas size: ${it.width}, ${it.height}, view size: $width, $height")
        }

        if(dirtyBit){
            visualEffect?.animator?.let{
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