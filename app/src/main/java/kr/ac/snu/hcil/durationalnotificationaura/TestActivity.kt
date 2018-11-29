package kr.ac.snu.hcil.durationalnotificationaura

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_test.*
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancementPattern
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.TestVisEffect
import java.util.*
import kotlin.math.roundToLong

class TestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val appEnhancedData = EnhancedAppNotificationData("kr.ac.snu.hcil.testViewGroup").apply{
            notificationData = mutableListOf(
                EnhancedNotificationDatum(
                    "default",
                     Calendar.getInstance().timeInMillis,
                    1000L * 60 * 60 * 1
                ).also{
                    it.currEnhancement = 0.8
                    it.timeElapsed = (0.8 * it.naturalDecay).roundToLong() },
                EnhancedNotificationDatum(
                    "default",
                    Calendar.getInstance().timeInMillis,
                    1000L * 60 * 60 * 1
                ).also{
                    it.currEnhancement = 0.5
                    it.timeElapsed = (0.5 * it.naturalDecay).roundToLong() },
                EnhancedNotificationDatum(
                    "default",
                    Calendar.getInstance().timeInMillis,
                    1000L * 60 * 60 * 1
                ).also{
                    it.currEnhancement = 0.3
                    it.timeElapsed = (0.3 * it.naturalDecay).roundToLong() }
            )
        }

        testViewGroup.run{
            setEnhanceData(appEnhancedData)
            setVisualEffects(listOf(TestVisEffect(), TestVisEffect(), TestVisEffect()))
        }
    }
}
