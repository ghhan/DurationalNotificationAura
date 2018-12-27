package kr.ac.snu.hcil.durationalnotificationaura.data

import kotlin.math.roundToLong

abstract class AbstractEnhancedData{
    abstract val typeOfEnhancement: String
    abstract val initTime: Long
    abstract val naturalDecay: Long
}
data class NotificationEnhancedData(
    val id: Int,
    override val typeOfEnhancement: String,
    override val initTime: Long,
    override val naturalDecay: Long
): AbstractEnhancedData() {
    var lifeCycle: EnhancedNotificationLifeCycle =
        EnhancedNotificationLifeCycle.STATE_1
    var firstPattern = EnhancementPattern.INC
    var secondPattern = EnhancementPattern.INC

    // constant
    var enhanceOffset = 0.0
    var lowerBound = 0.0
    var upperBound = 1.0
    var firstSaturationTime : Long = (naturalDecay.toDouble() * 0.8).roundToLong()
    var secondSaturationTime : Long = (naturalDecay.toDouble() * 0.2).roundToLong()

    // variable
    var slope = 0.0
    var timeElapsed = 0L
    var currEnhancement = enhanceOffset
}

class AppNotificationsEnhancedData(val packageName: String,
                                   var screenNumber: Int = -1,
                                   var positionInScreen: Pair<Int, Int> = Pair(0, 0)) {
    var notificationData : MutableList<NotificationEnhancedData> = mutableListOf()
}