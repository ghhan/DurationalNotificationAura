package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.graphics.Canvas
import android.graphics.Color
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
             * 0.5f 미만(0f): Oval 또는 Circle
             * 1.5f 미만(1f): Rect
             * 2.5f 미만(2f): RoundRect
             */
            var shape = 0
            var shapeType = Shapes.OVAL
            if (getVisParams().containsKey("shape")) shape = getVisParams()["shape"]!!.roundToInt()
            if (shape == 1) shapeType = Shapes.RECT
            else if (shape == 2) shapeType = Shapes.ROUND_RECT

            /*
             * centerX, centerY, top, left, right, bottom
             * 1.0f보다 작거나 같을 경우 화면 대비 비율에 따른 상대적 좌표로 사용(0.0f ~ 1.0f)
             * 1.0f보다 클 경우 절대적 좌표로 사용
             */
            var cx = 0.5f       // 'radiusX'와 함께 사용, 'shape'가 RoundRect일 때 사용 불가
            var cy = 0.5f       // 'radiusY'와 함께 사용, 'shape'가 RoundRect일 때 사용 불가
            var top = 0.9f      // 'bottom'과 함께 사용, 'centerY'와 사용 불가
            var left = 0.1f     // 'right'과 함께 사용, 'centerX'와 사용 불가
            var right = 0.9f    // 'left'와 함께 사용, 'centerX'와 사용 불가
            var bottom = 0.1f   // 'top'과 함께 사용, 'centerY'와 사용 불가

            /*
             * radiusX, radiusY
             * 1.0f보다 작거나 같을 경우 화면 대비 비율에 따른 상대적 크기로 사용(0.0f ~ 1.0f)
             * 1.0f보다 클 경우 절대적 크기로 사용
             * shape가 RoundRect이면 둘 다 항상 필요 (꼭지점의 둥근 부분의 반지름을 나타냄)
             * shape가 Circle이려면 Oval에서 top, left, right, bottom을 사용하지 않고
             *     radiusX와 radiusY를 절대적 크기로 같아지도록 설정해야 함
             */
            var radiusX = 0.1f    // 'centerX'와 함께 사용, 'shape'가 Circle 또는 RoundRect일 때 필수
            var radiusY = 0.1f    // 'centerY'와 함께 사용, 'shape'가 Circle 또는 RoundRect일 때 필수

            /*
             * brightness
             * 0.5f 미만(0f): 'color'의 값 사용
             * 1.5f 미만(1f): dark_vibrant
             * 2.5f 미만(2f): vibrant
             * 3.5f 미만(3f): light_vibrant
             *
             * colorAlpha, colorRed, colorGreen, colorBlue
             * 이것들을 roundToInt()한 결과가 0 ~ 255 사이의 값을 가져야 함
             * 'brightness'가 0이거나 'brightness'를 설정하지 않았을 때에만 작동
             */
            var color = paintMap[ColorSwatches.DARK_VIBRANT]!!
            var colorAlpha = 255
            var colorRed = 0
            var colorGreen = 0
            var colorBlue = 0

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

            if (!getVisParams().containsKey("brightness") ||
                getVisParams().containsKey("brightness") && getVisParams()["brightness"]!!.roundToInt() == 0) {
                if (getVisParams().containsKey("colorAlpha"))
                    colorAlpha = getVisParams()["colorAlpha"]!!.roundToInt().coerceIn(0, 255)
                if (getVisParams().containsKey("colorRed"))
                    colorRed = getVisParams()["colorRed"]!!.roundToInt().coerceIn(0, 255)
                if (getVisParams().containsKey("colorGreen"))
                    colorGreen = getVisParams()["colorGreen"]!!.roundToInt().coerceIn(0, 255)
                if (getVisParams().containsKey("colorAlpha"))
                    colorBlue = getVisParams()["colorBlue"]!!.roundToInt().coerceIn(0, 255)
                color.color = colorAlpha * 16777216 + colorRed * 65536 + colorGreen * 256 + colorBlue
            }
            else {
                if (getVisParams()["brightness"]!!.roundToInt() == 1) {
                    color = paintMap[ColorSwatches.DARK_VIBRANT]!!
                } else if (getVisParams()["brightness"]!!.roundToInt() == 2) {
                    color = paintMap[ColorSwatches.VIBRANT]!!
                } else if (getVisParams()["brightness"]!!.roundToInt() == 3) {
                    color = paintMap[ColorSwatches.LIGHT_VIBRANT]!!
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
                    cx, cy, radiusX,
                    color
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
                    color
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
                    color
                )
            }
            else if (shapeType == Shapes.ROUND_RECT) {
                it.drawRoundRect(
                    left, top, right, bottom,
                    radiusX, radiusY,
                    color
                )
            }
            Log.d("AURA_EFFECT",  shapeType.name + " cx:" + cx.toString() + " cy:" + cy.toString() + " rx:" + radiusX.toString() +
                    " ry:" + radiusY.toString() + " width:" + it.width.toString() + " height:" + it.height.toString())
        }
    }
}