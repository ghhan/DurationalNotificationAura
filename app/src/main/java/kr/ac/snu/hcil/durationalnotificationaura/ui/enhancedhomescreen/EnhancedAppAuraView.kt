package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kr.ac.snu.hcil.durationalnotificationaura.data.AppNotificationsEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisEffect

class EnhancedAppAuraView(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet) {

    /* One View -> One Visual Object (Translations, Alpha, Rotation, Scale)
     * ViewGroup Controls Multiple Views
     * ViewGroup Sets Layout of Views, Rendering Order
     */
    
    companion object {
        const val TAG = "APP_AURA_VIEW"
    }

    private val testColor = ColorDrawable(Color.RED)
    init{
        clipChildren = false
        clipToPadding = false
        clipToOutline = false
    }

    private var appPackageName: String? = null

    fun setEnhanceData(enhanceData: AppNotificationsEnhancedData) {
        appPackageName = enhanceData.packageName
        enhanceData.notificationData.forEach{
            addView(
                EnhancedNotificationAuraView(context, null).also{ view -> view.setVisualData(it) },
                LayoutParams(200, 200)
            )
        }
    }

    fun setVisualEffects(visualEffects: List<VisEffect>){
        visualEffects.forEachIndexed{
            index, visEffect -> (getChildAt(index) as EnhancedNotificationAuraView).setVisualEffect(visEffect)
        }
    }

    fun setSameVisualEffect(visualEffect: VisEffect) {
        for (idx in 0 ..(childCount - 1)){
            (getChildAt(idx) as EnhancedNotificationAuraView).setVisualEffect(visualEffect)
        }
    }

    fun addEnhancedNotificationAuraView(visData: NotificationEnhancedData, visEffect: VisEffect){
        addView(
            EnhancedNotificationAuraView(context, null).also{
                    view -> view.setVisualData(visData); view.setVisualEffect(visEffect)
            } as View
        )
    }

    override fun onDescendantInvalidated(child: View, target: View) {
        super.onDescendantInvalidated(child, target)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    // View Group의 전체적인 배치를 결정하는 모듈
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(changed){
            for(idx in 0..(childCount - 1)){
                val child : View = getChildAt(idx)
                child.layout(0, 0, width, height)
            }
        }
    }
}