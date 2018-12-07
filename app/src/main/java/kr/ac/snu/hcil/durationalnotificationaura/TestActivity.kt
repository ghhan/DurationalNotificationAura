package kr.ac.snu.hcil.durationalnotificationaura

import android.content.pm.PackageManager.GET_ACTIVITIES
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.graphics.Palette
import android.widget.Button
import kotlinx.android.synthetic.main.activity_test.*
import kr.ac.snu.hcil.durationalnotificationaura.data.AppNotificationsEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.DefaultVisEffect
import java.util.*
import kotlin.math.roundToLong

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val appEnhancedData = AppNotificationsEnhancedData("kr.ac.snu.hcil.testViewGroup").apply{
            notificationData = mutableListOf(
                NotificationEnhancedData(
                    "default",
                     Calendar.getInstance().timeInMillis,
                    1000L * 60 * 60 * 1
                ).also{
                    it.currEnhancement = 0.8
                    it.timeElapsed = (0.8 * it.naturalDecay).roundToLong() },
                NotificationEnhancedData(
                    "default",
                    Calendar.getInstance().timeInMillis,
                    1000L * 60 * 60 * 1
                ).also{
                    it.currEnhancement = 0.5
                    it.timeElapsed = (0.5 * it.naturalDecay).roundToLong() },
                NotificationEnhancedData(
                    "default",
                    Calendar.getInstance().timeInMillis,
                    1000L * 60 * 60 * 1
                ).also{
                    it.currEnhancement = 0.3
                    it.timeElapsed = (0.3 * it.naturalDecay).roundToLong() }
            )
        }

        testViewGroup1.run{
            setEnhanceData(appEnhancedData)
            setVisualEffects(listOf(DefaultVisEffect(), DefaultVisEffect(), DefaultVisEffect()))
        }

        testViewGroup2.run{
            setEnhanceData(appEnhancedData)
            setVisualEffects(listOf(DefaultVisEffect(), DefaultVisEffect(), DefaultVisEffect()))
        }


    }
}
