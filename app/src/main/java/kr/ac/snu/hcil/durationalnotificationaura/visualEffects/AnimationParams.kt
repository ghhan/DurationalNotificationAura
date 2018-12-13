package kr.ac.snu.hcil.durationalnotificationaura.visualEffects

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationLifeCycle

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