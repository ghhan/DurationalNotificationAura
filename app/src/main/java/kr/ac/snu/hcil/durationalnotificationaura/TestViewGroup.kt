package kr.ac.snu.hcil.durationalnotificationaura

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedNotificationAuraView
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisEffect

class TestViewGroup(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet) {

    /* One View -> One Visual Object (Translations, Alpha, Rotation, Scale)
     * ViewGroup Controls Multiple Views
     * ViewGroup Sets Layout of Views, Rendering Order
     */

    private var appPackageName: String? = null

    init{
        setWillNotDraw(false)
    }

    fun setEnhanceData(enhanceData: EnhancedAppNotificationData) {
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

    fun addEnhancedNotificationAuraView(visData: EnhancedNotificationDatum, visEffect: VisEffect){
        addView(
            EnhancedNotificationAuraView(context, null).also{
                    view -> view.setVisualData(visData); view.setVisualEffect(visEffect)
            } as View
        )
    }

    override fun onDescendantInvalidated(child: View, target: View) {
        super.onDescendantInvalidated(child, target)
        invalidate()
    }

    // View Group의 전체적인 배치를 결정하는 모듈
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(changed){
            for(idx in 0..(childCount - 1)){
                val child : View = getChildAt(idx)
                child.layout(l, t, r, b)
            }
        }
    }
}