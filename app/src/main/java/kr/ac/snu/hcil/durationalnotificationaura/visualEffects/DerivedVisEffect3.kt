package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.graphics.Canvas
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kotlin.math.roundToInt

class DerivedVisEffect3(
    palette: Palette,
    targetView: View,
    visParams: Map<String, Float>,
    animParams: Map<AnimationTypes, AnimationParams>)
    :AbstractVisEffect(palette, targetView, visParams, animParams) {

    override fun drawVisualEffect(data: NotificationEnhancedData, canvas: Canvas) {
        val paintMap = getPaintMap()
        canvas.let{
            /*
             * shape
             * 0.5 미만: Oval 또는 Circle
             * 1.5 미만: Rect
             * 2.5 미만: RoundRect
             */
            var shape = 0
            var shapeType = Shapes.OVAL
            if (getVisParams().containsKey("shape")) shape = getVisParams()["shape"]!!.roundToInt()
            if (shape == 1) shapeType = Shapes.RECT
            else if (shape == 2) shapeType = Shapes.ROUND_RECT

            /*
             * cx, cy, top, left, right, bottom
             * 1.0f보다 작거나 같을 경우 화면 대비 비율에 따른 상대적 좌표로 사용(0.0f ~ 1.0f)
             * 1.0f보다 클 경우 절대적 좌표로 사용
             */
            var cx = 0.5f       // radiusX와 함께 사용, shape가 RoundRect일 때 사용 불가
            var cy = 0.5f       // radiusY와 함께 사용, shape가 RoundRect일 때 사용 불가
            var top = 0.9f      // bottom과 함께 사용, cy와 사용 불가
            var left = 0.1f     // right과 함께 사용, cx와 사용 불가
            var right = 0.9f    // left와 함께 사용, cx와 사용 불가
            var bottom = 0.1f   // top과 함께 사용, cy와 사용 불가

            /*
             * radiusX, radiusY
             * 1.0f보다 작거나 같을 경우 화면 대비 비율에 따른 상대적 크기로 사용(0.0f ~ 1.0f)
             * 1.0f보다 클 경우 절대적 크기로 사용
             * shape가 RoundRect이면 둘 다 항상 필요 (꼭지점의 둥근 부분의 반지름을 나타냄)
             * shape가 Circle이려면 Oval에서 top, left, right, bottom을 사용하지 않고
             *     radiusX와 radiusY를 절대적 크기로 같아지도록 설정해야 함
             */
            var radiusX = 0.1f    // cx와 함께 사용, shape가 Circle 또는 RoundRect일 때 필수
            var radiusY = 0.1f    // cy와 함께 사용, shape가 Circle 또는 RoundRect일 때 필수

            var isLR = false
            var isTB = false
            if (getVisParams().containsKey("centerX") && getVisParams().containsKey("radiusX") &&
                    shapeType != Shapes.ROUND_RECT) {
                cx = getVisParams()["centerX"]!!
                radiusX = getVisParams()["radiusX"]!!
            }
            else if (getVisParams().containsKey("left") && getVisParams().containsKey("right")) {
                left = getVisParams()["left"]!!
                right = getVisParams()["right"]!!
                isLR = true
                if (shapeType == Shapes.ROUND_RECT && getVisParams().containsKey("radiusX")) {
                    radiusX = getVisParams()["radiusX"]!!
                }
            }

            if (getVisParams().containsKey("centerY") && getVisParams().containsKey("radiusY") &&
                    shapeType != Shapes.ROUND_RECT) {
                cy = getVisParams()["centerY"]!!
                radiusY = getVisParams()["radiusY"]!!
            }
            else if (getVisParams().containsKey("top") && getVisParams().containsKey("bottom")) {
                bottom = getVisParams()["bottom"]!!
                top = getVisParams()["top"]!!
                isTB = true
                if (shapeType == Shapes.ROUND_RECT && getVisParams().containsKey("radiusY")) {
                    radiusY = getVisParams()["radiusY"]!!
                }
            }

            // 상대적 좌표를 절대적 좌표로 변환
            if (cx <= 1.0f) cx *= it.width.toFloat()
            if (cy <= 1.0f) cy *= it.height.toFloat()
            if (left <= 1.0f) left *= it.width.toFloat()
            if (right <= 1.0f) right *= it.width.toFloat()
            if (top <= 1.0f) top *= it.height.toFloat()
            if (bottom <= 1.0f) bottom *= it.height.toFloat()
            if (radiusX <= 1.0f) radiusX *= it.width.toFloat()
            if (radiusY <= 1.0f) radiusY *= it.height.toFloat()

            // Circle인지 확인
            if (shapeType == Shapes.OVAL && !isLR && !isTB && radiusX == radiusY)
                shapeType = Shapes.CIRCLE

            // 그리기
            if (shapeType == Shapes.CIRCLE) {
                it.drawCircle(
                    cx, cy, radiusX * data.currEnhancement.toFloat(),
                    paintMap[ColorSwatches.LIGHT_VIBRANT]!!
                )
                it.drawCircle(
                    cx, cy, radiusX * 0.9f * data.currEnhancement.toFloat(),
                    paintMap[ColorSwatches.DARK_VIBRANT]!!.apply{}
                )
            }
            else if (shapeType == Shapes.OVAL) {
                if (!isLR) {
                    left = cx - radiusX
                    right = cx + radiusX
                }
                if (!isTB) {
                    bottom = cy - radiusY
                    top = cy + radiusY
                }
                it.drawOval(
                    left, top, right, bottom,
                    paintMap[ColorSwatches.DARK_VIBRANT]!!
                )
            }
            else if (shapeType == Shapes.RECT) {
                if (!isLR) {
                    left = cx - radiusX
                    right = cx + radiusX
                }
                if (!isTB) {
                    bottom = cy - radiusY
                    top = cy + radiusY
                }
                it.drawRect(
                    left, top, right, bottom,
                    paintMap[ColorSwatches.DARK_VIBRANT]!!
                )
            }
            else if (shapeType == Shapes.ROUND_RECT) {
                it.drawRoundRect(
                    left, top, right, bottom,
                    radiusX, radiusY,
                    paintMap[ColorSwatches.DARK_VIBRANT]!!
                )
            }
            Log.d("AURA_EFFECT",  shapeType.name + " cx:" + cx.toString() + " cy:" + cy.toString() + " rx:" + radiusX.toString() +
                    " ry:" + radiusY.toString() + " width:" + it.width.toString() + " height:" + it.height.toString())
        }
    }
}