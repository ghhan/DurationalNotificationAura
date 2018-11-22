package kr.ac.snu.hcil.durationalnotificationaura

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.*
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.AnimatedENAView

class TestViewGroup(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet) {

    /* One View -> One Visual Object (Translations, Alpha, Rotation, Scale)
     * ViewGroup Controls Multiple Views
     * ViewGroup Sets Layout of Views, Rendering Order
     */
    private var enhancedAppNotificationData : EnhancedAppNotificationData? = null

    //
    fun setEnhancedNotificationData(data: EnhancedAppNotificationData){
        // data 무더기를 받아서 View를 생성하는 모듈
        data.notificationData.map{
            addView(
                AnimatedENAView(context, null).apply{ //TODO: View에 data를 먹여줘야 함
                },
                LayoutParams(100, 100) //TODO: Layout Parameter
            )
        }
    }

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        super.addView(child, index, params)
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}