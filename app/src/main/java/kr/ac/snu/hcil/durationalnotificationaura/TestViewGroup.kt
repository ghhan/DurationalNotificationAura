package kr.ac.snu.hcil.durationalnotificationaura

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
<<<<<<< HEAD
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.*
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.AnimatedENAView
=======
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedNotificationAuraView
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisEffect

>>>>>>> 0df1416689e5abb992a2dc95121139922364d4b5

class TestViewGroup(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet) {

    /* One View -> One Visual Object (Translations, Alpha, Rotation, Scale)
     * ViewGroup Controls Multiple Views
     * ViewGroup Sets Layout of Views, Rendering Order
     */

    private var appPackageName: String? = null

    fun setEnhanceData(enhanceData: EnhancedAppNotificationData) {
        appPackageName = enhanceData.packageName
        enhanceData.notificationData.forEach{
            addView(
                EnhancedNotificationAuraView(context, null).also{ view -> view.setVisualData(it) }
            )
        }
    }

    fun setVisualEffects(visualEffects: List<VisEffect>){
        visualEffects.forEachIndexed{
            index, visEffect -> (getChildAt(index) as EnhancedNotificationAuraView).setVisualEffect(visEffect)
        }
    }

    fun addEnhancedNotificationAuraView(visData: EnhancedNotificationDatum, visEffect: VisEffect){
        addView(
            EnhancedNotificationAuraView(context, null).also{
                    view -> view.setVisualData(visData); view.setVisualEffect(visEffect)
            }
        )
    }

    // 얘는 각각 child 추가하는 코드
    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        return super.drawChild(canvas, child, drawingTime)
        //child.draw(canvas)
        //내가 어떤 child 그릴 지 선택은 아니므로 다른 함수 확인해야함
    }

    // child가 invalidated일때
    override fun onDescendantInvalidated(child: View, target: View) {
        super.onDescendantInvalidated(child, target)
    }

    // View Group의 전체적인 배치를 결정하는 모듈
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        when(changed){
            true -> {

            }
            false -> {

            }
        }
    }
}