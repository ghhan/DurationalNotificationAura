package kr.ac.snu.hcil.durationalnotificationaura.data

import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedNotificationLifeCycle
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancementPattern
import kotlin.math.roundToLong

abstract class EnhancementDatum{
    abstract val typeOfEnhancement: String
    abstract val initTime: Long
    abstract val naturalDecay: Long
}
data class EnhancedNotificationDatum(
    override val typeOfEnhancement: String,
    override val initTime: Long,
    override val naturalDecay: Long
): EnhancementDatum() {
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

class EnhancedAppNotificationData(var packageName: String) {
    var notificationData : MutableList<EnhancedNotificationDatum> = mutableListOf()
}


