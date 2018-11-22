package kr.ac.snu.hcil.durationalnotificationaura

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.*

class TestViewGroup(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet) {

    // data 무더기를 받아서 View를 생성하는 모듈
    fun setEnhancedNotificationData(data: EnhancedAppNotificationData){
        data.notificationData.map{
            addView(
                AnimatedENAView(context, null).apply{ //TODO: View에 data를 먹여줘야 함
                },
                LayoutParams(100, 100) //TODO: Layout Parameter
            )
        }
    }

    // 얘는 각
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