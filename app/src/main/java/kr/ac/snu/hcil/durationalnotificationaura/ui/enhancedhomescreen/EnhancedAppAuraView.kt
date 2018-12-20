package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import kr.ac.snu.hcil.durationalnotificationaura.data.AppNotificationsEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AbstractVisEffect

class EnhancedAppAuraView(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet) {

    /* One View -> One Visual Object (Translations, Alpha, Rotation, Scale)
     * ViewGroup Controls Multiple Views
     * ViewGroup Sets Layout of Views, Rendering Order
     */
    companion object {
        const val TAG = "APP_AURA_VIEW"
    }

    init{
        clipChildren = false
        clipToOutline = false
        clipToPadding = false
    }

    private var appPackageName: String? = null

    fun setEnhanceData(enhanceData: AppNotificationsEnhancedData) {
        appPackageName = enhanceData.packageName

        enhanceData.notificationData.forEach{
            addView(
                EnhancedNotificationAuraView(context, null).also{
                        view -> view.setVisualData(it)
                    view.tag = appPackageName + "_child"
                },
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    fun setVisualEffects(visualEffects: List<AbstractVisEffect>){
        visualEffects.forEachIndexed{
            index, visEffect -> (getChildAt(index) as EnhancedNotificationAuraView).setVisualEffect(visEffect)
        }
    }

    fun addEnhancedNotificationAuraView(visData: NotificationEnhancedData, visEffect: AbstractVisEffect){
        addView(
            EnhancedNotificationAuraView(context, null).also{
                    view -> view.setVisualData(visData); view.setVisualEffect(visEffect)
            } as View
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthpixels = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthmode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightpixels = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightmode = View.MeasureSpec.getMode(heightMeasureSpec)

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    // View Group의 전체적인 배치를 결정하는 모듈
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if(changed){
            Log.d(TAG, "# of Children in a view of id $tag: $childCount")
            for(idx in 0..(childCount - 1)){
                val child : View = getChildAt(idx)
                Log.d(TAG, "${child.id} -  l: $l, t: $t, r: $r, b: $b")
                child.layout(0, 0, r - l, b - t)
            }
        }
    }
}