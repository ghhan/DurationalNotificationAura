package kr.ac.snu.hcil.durationalnotificationaura.utils

import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancementPattern
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import java.util.*

class NotificationRandomGenerator{
    companion object Factory {
        fun newRandomNotification(id: Int, initTime: Long, naturalDecay: Long): NotificationEnhancedData {
            val firsttrend = Random().nextInt() % 3
            val secondtrend = Random().nextInt() % 3

            return NotificationEnhancedData(
                id,
                "default",
                initTime,
                naturalDecay
            ).apply{
                when(firsttrend){
                    0 -> {
                        firstPattern = EnhancementPattern.INC
                    }
                    1 -> {
                        firstPattern = EnhancementPattern.DEC
                        enhanceOffset = 1.0
                        currEnhancement = enhanceOffset
                    }
                    2 -> {
                        firstPattern = EnhancementPattern.EQ
                        enhanceOffset = 0.5
                        currEnhancement = enhanceOffset
                    }
                }

                when(secondtrend){
                    0 -> {firstPattern = EnhancementPattern.INC}
                    1 -> {firstPattern = EnhancementPattern.DEC}
                    2 -> {firstPattern = EnhancementPattern.EQ}
                }
            }
        }
    }
}