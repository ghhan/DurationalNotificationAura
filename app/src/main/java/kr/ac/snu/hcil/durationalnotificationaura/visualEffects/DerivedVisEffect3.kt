package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.graphics.Canvas
import android.graphics.Color
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_vis_test.*
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kotlin.math.*

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
             * pivotX, pivotY
             * 앱 아이콘의 중심 좌표를 절대적 좌표로 지정
             */
            var pivotX = 0.5f * it.width.toFloat()  // 'pivotY'와 함께 사용
            var pivotY = 0.5f * it.height.toFloat() // 'pivotX'와 함께 사용

            /*
             * posRadius
             * 앱 아이콘 중심과 새로 그릴 도형의 중심과의 거리를 절대적 크기로 지정
             *
             * posAngle
             * 앱 아이콘 중심을 기준으로 새로 그릴 도형의 각 위치 지정(0f ~ 360f)
             *
             * 전부 'centerX', 'centerY', 'top', 'left', 'right', 'bottom'과 함께 사용 불가
             */
            var posRadius = 0f  // 'pivotX', 'pivotY'와 함께 사용
            var posAngle = 0.0f   // 'pivotX', 'pivotY', 'posRadius'와 함께 사용

            /*
             * centerX, centerY, top, left, right, bottom
             * 1.0f보다 작거나 같을 경우 화면 대비 비율에 따른 상대적 좌표로 사용(0.0f ~ 1.0f)
             * 1.0f보다 클 경우 절대적 좌표로 사용
             * 전부 'posRadius'와 함께 사용 불가
             */
            var cx = 0.5f       // 'radiusX' 또는 'size'와 함께 사용
            var cy = 0.5f       // 'radiusY' 또는 'size'와 함께 사용
            var top = 0.9f      // 'bottom'과 함께 사용, 'centerY'와 사용 불가
            var left = 0.1f     // 'right'과 함께 사용, 'centerX'와 사용 불가
            var right = 0.9f    // 'left'와 함께 사용, 'centerX'와 사용 불가
            var bottom = 0.1f   // 'top'과 함께 사용, 'centerY'와 사용 불가

            /*
             * radiusX, radiusY
             * 1.0f보다 작거나 같을 경우 화면 대비 비율에 따른 상대적 크기로 사용(0.0f ~ 1.0f)
             * 1.0f보다 클 경우 절대적 크기로 사용
             * shape가 Circle이려면 Oval에서 top, left, right, bottom을 사용하지 않고
             *     radiusX와 radiusY를 절대적 크기로 같아지도록 설정해야 한다.
             *     또는 size를 설정해야 한다.
             *
             * roundX, roundY
             * 1.0f보다 작거나 같을 경우 도형의 가로 길이와 세로 길이 중
             *     작은 길이에 대한 상대적 크기로 사용(0.0f ~ 1.0f)
             * 1.0f보다 클 경우 절대적 크기로 사용
             * shape가 RoundRect일 때만 사용하며, 꼭지점의 둥근 부분의 반지름을 지정
             */
            var radiusX = 0.1f    // 'centerX'와 함께 사용, 'size'와 사용 불가
            var radiusY = 0.1f    // 'centerY'와 함께 사용, 'size'와 사용 불가
            var roundX = 0.25f
            var roundY = 0.25f

            /*
             * size
             * 원의 지름 또는 정사각형의 한 변의 길이를 지정
             * 1.0f보다 작거나 같을 경우 화면 너비(width) 대비 비율에 따른 상대적 크기로 사용(0.0f ~ 1.0f)
             * 1.0f보다 클 경우 절대적 크기로 사용
             *
             */
            var size = 0.1f     // 'radiusX', 'radiusY'와 사용 불가

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

            var isPolar = true
            var isLR = false
            var isTB = false
            var isRegular = true

            /*
             * 위치 우선순위
             * 1. posRadius, posAngle (pivotX, pivotY 필요)
             * 2. centerX / centerY
             * 3. left, right / top, bottom
             *
             * 크기 우선순위
             * 1. size
             * 2. radiusX / radiusY
             * 3. left, right / top, bottom
             */
            if (getVisParams().containsKey("pivotX") && getVisParams().containsKey("pivotY")) {
                pivotX = getVisParams()["pivotX"]!!
                pivotY = getVisParams()["pivotY"]!!
            }

            if (getVisParams().containsKey("size")) {
                size = getVisParams()["size"]!!
            }
            else {
                isRegular = false
                if (getVisParams().containsKey("radiusX")) {
                    radiusX = getVisParams()["radiusX"]!!
                }
                if (getVisParams().containsKey("radiusY")) {
                    radiusY = getVisParams()["radiusY"]!!
                }
            }

            if (getVisParams().containsKey("posRadius")) {
                posRadius = getVisParams()["posRadius"]!!
                if (getVisParams().containsKey("posAngle")) {
                    posAngle = getVisParams()["posAngle"]!!
                }
            }
            else {
                isPolar = false
                if (getVisParams().containsKey("centerX") &&
                    (isRegular || getVisParams().containsKey("radiusX"))) {
                    cx = getVisParams()["centerX"]!!
                }
                else if (getVisParams().containsKey("left") && getVisParams().containsKey("right")) {
                    left = getVisParams()["left"]!!
                    right = getVisParams()["right"]!!
                    isLR = true
                }

                if (getVisParams().containsKey("centerY") &&
                    (isRegular || getVisParams().containsKey("radiusY"))) {
                    cy = getVisParams()["centerY"]!!
                }
                else if (getVisParams().containsKey("top") && getVisParams().containsKey("bottom")) {
                    bottom = getVisParams()["bottom"]!!
                    top = getVisParams()["top"]!!
                    isTB = true
                }
            }

            if (getVisParams().containsKey("roundX")) {
                roundX = getVisParams()["roundX"]!!
            }
            if (getVisParams().containsKey("roundY")) {
                roundY = getVisParams()["roundY"]!!
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
                color.color = colorAlpha * 0x1000000 + colorRed * 0x10000 + colorGreen * 0x100 + colorBlue
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

            if (isPolar) {
                //cx = pivotX + posRadius * cos(posAngle * PI.toFloat() / 180f)
                //cy = pivotY + posRadius * sin(posAngle * PI.toFloat() / 180f)
                cx = pivotX + posRadius * cos(0f)
                cy = pivotY + posRadius * sin(0f)
            }
            else {
                // 상대적 좌표를 절대적 좌표로 변환
                if (cx <= 1.0f) cx *= it.width.toFloat()
                if (cy <= 1.0f) cy *= it.height.toFloat()
                if (left <= 1.0f) left *= it.width.toFloat()
                if (right <= 1.0f) right *= it.width.toFloat()
                if (top <= 1.0f) top *= it.height.toFloat()
                if (bottom <= 1.0f) bottom *= it.height.toFloat()
            }
            if (size <= 1.0f) size *= it.width.toFloat()
            if (radiusX <= 1.0f) radiusX *= it.width.toFloat()
            if (radiusY <= 1.0f) radiusY *= it.height.toFloat()

            if (isRegular) {
                radiusX = size
                radiusY = size
            }

            // Circle인지 확인
            if (shapeType == Shapes.OVAL && (isRegular || !isLR && !isTB && radiusX == radiusY))
                shapeType = Shapes.CIRCLE

            // 그리기
            it.save()
            it.rotate(posAngle, pivotX, pivotY)
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
                if (!isLR) {
                    left = cx - radiusX
                    right = cx + radiusX
                }
                if (!isTB) {
                    bottom = cy - radiusY
                    top = cy + radiusY
                }
                if (roundX <= 1.0f) roundX *= min(top - bottom, right - left)
                if (roundY <= 1.0f) roundY *= min(top - bottom, right - left)
                it.drawRoundRect(
                    left, top, right, bottom,
                    roundX, roundY,
                    color
                )
            }
            it.restore()
            Log.d("AURA_EFFECT",  shapeType.name + " cx:" + cx.toString() + " cy:" + cy.toString() + " rx:" + radiusX.toString() +
                    " ry:" + radiusY.toString() + " width:" + it.width.toString() + " height:" + it.height.toString())
        }
    }
}