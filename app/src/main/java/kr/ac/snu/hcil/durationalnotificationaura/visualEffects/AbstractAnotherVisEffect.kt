package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.animation.*
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v7.graphics.Palette
import android.view.View
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationLifeCycle
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData

enum class ColorSwatches{
    LIGHT_VIBRANT,
    VIBRANT,
    DARK_VIBRANT,
    LIGHT_MUTED,
    MUTED,
    DARK_MUTED
}

enum class AnimationTypes{
    ALPHA,
    ROTATION,
    SCALE_X,
    SCALE_Y,
    TRANSLATION_X,
    TRANSLATION_Y
}
data class AnimationParams(
    val values: FloatArray,
    val duration: Long,
    val interpolator: TimeInterpolator,
    val sustained: List<EnhancedNotificationLifeCycle> = listOf(
        EnhancedNotificationLifeCycle.STATE_1,
        EnhancedNotificationLifeCycle.STATE_2,
        EnhancedNotificationLifeCycle.STATE_3,
        EnhancedNotificationLifeCycle.STATE_4,
        EnhancedNotificationLifeCycle.STATE_5
    ),
    val repeatCount: Int = ObjectAnimator.INFINITE,
    val repeatMode: Int = ObjectAnimator.REVERSE
){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

abstract class AbstractAnotherVisEffect(
    private var palette: Palette,
    private var targetView: View,
    private var visParams: Map<String, Float>,
    private var animParams: Map<AnimationTypes, AnimationParams>
    ) {
    companion object {
        const val TAG = "ANOTHER_VIS_EFFECT"
        const val DEFAULT_COLOR = Color.LTGRAY
        val animTypeToProp = mapOf(
            AnimationTypes.ALPHA to View.ALPHA,
            AnimationTypes.TRANSLATION_X to View.TRANSLATION_X,
            AnimationTypes.TRANSLATION_Y to View.TRANSLATION_Y,
            AnimationTypes.ROTATION to View.ROTATION,
            AnimationTypes.SCALE_X to View.SCALE_X,
            AnimationTypes.SCALE_Y to View.SCALE_Y
        )
    }

    private lateinit var paintMap: Map<ColorSwatches, Paint>
    private lateinit var animatorSet: AnimatorSet
    private lateinit var animationMap: Map<EnhancedNotificationLifeCycle, List<Animator>>
    private lateinit var currentLifeStage: EnhancedNotificationLifeCycle

    private fun initializePalette() {
        paintMap = mapOf(
            ColorSwatches.LIGHT_VIBRANT to Paint().also{
                it.isAntiAlias = true
                it.color = palette.getLightVibrantColor(DEFAULT_COLOR)
            },
            ColorSwatches.VIBRANT to Paint().also{
                it.isAntiAlias = true
                it.color = palette.getVibrantColor(DEFAULT_COLOR)
            },
            ColorSwatches.DARK_VIBRANT to Paint().also{
                it.isAntiAlias = true
                it.color = palette.getVibrantColor(DEFAULT_COLOR)
            },
            ColorSwatches.LIGHT_MUTED to Paint().also{
                it.isAntiAlias = true
                it.color = palette.getLightMutedColor(DEFAULT_COLOR)
            },
            ColorSwatches.MUTED to Paint().also{
                it.isAntiAlias = true
                it.color = palette.getDarkMutedColor(DEFAULT_COLOR)
            })
    }
    private fun initializeAnimation(){

        val myMap: Map<EnhancedNotificationLifeCycle, MutableList<Animator>> = mapOf(
            EnhancedNotificationLifeCycle.STATE_1 to mutableListOf(),
            EnhancedNotificationLifeCycle.STATE_2 to mutableListOf(),
            EnhancedNotificationLifeCycle.STATE_3 to mutableListOf(),
            EnhancedNotificationLifeCycle.STATE_4 to mutableListOf(),
            EnhancedNotificationLifeCycle.STATE_5 to mutableListOf()
        )

        animParams.map{
            entry ->
            entry.value.sustained.map{
                myMap[it]!!.add(
                    ObjectAnimator.ofFloat(targetView, animTypeToProp[entry.key], *entry.value.values)
                        .apply{
                            repeatMode = entry.value.repeatMode
                            repeatCount = entry.value.repeatCount
                            duration = entry.value.duration
                        }
                )
            }
        }

        animationMap = myMap.mapValues { it.value.toList() }
    }

    init{
        initializePalette()
        initializeAnimation()
        animatorSet = AnimatorSet()
        setCurrentStage(EnhancedNotificationLifeCycle.STATE_1)
    }

    fun setPalette(p: Palette) {
        palette = p
        initializePalette()
    }
    fun setTargetView(v: View){
        targetView = v
        //initializeAnimation()

        animatorSet.pause()
        animatorSet.setTarget(targetView)
        animatorSet.resume()
    }
    fun setVisParams(m: Map<String, Float>){
        visParams = m
    }

    fun setAnimParams(m: Map<AnimationTypes, AnimationParams>){
        animParams = m
        initializeAnimation()

        animatorSet.cancel()
        animatorSet = AnimatorSet().apply{
            playTogether(animationMap[currentLifeStage])
            start()
        }
    }

    fun setCurrentStage(lifeCycle: EnhancedNotificationLifeCycle){
        currentLifeStage = lifeCycle
        animatorSet.cancel()
        animatorSet = AnimatorSet().apply{
            playTogether(animationMap[lifeCycle])
            start()
        }
    }

    fun getCurrLifeCycle() = currentLifeStage
    fun getPaintMap() = paintMap
    fun getAnimatorSet() = animatorSet

    abstract fun drawVisualEffect(data: NotificationEnhancedData, canvas: Canvas)
}

class DerivedVisualEffect(
    palette: Palette,
    targetView: View,
    visParams: Map<String, Float>,
    animParams: Map<AnimationTypes, AnimationParams>)
    :AbstractAnotherVisEffect(palette, targetView, visParams, animParams){

    override fun drawVisualEffect(data: NotificationEnhancedData, canvas: Canvas) {
        val paintMap = getPaintMap()
        canvas.let{
            val cx = it.width/2.toFloat()
            val cy = it.height/2.toFloat()
            val radius = minOf(it.width, it.height).toFloat() * 0.5f * data.currEnhancement.toFloat()

            it.drawCircle(
                cx, cy, radius,
                paintMap[ColorSwatches.LIGHT_VIBRANT]!!
            )
            it.drawCircle(
                cx, cy, radius,
                paintMap[ColorSwatches.DARK_VIBRANT]!!.apply{
                    style = Paint.Style.STROKE
                }
            )
        }
    }
}