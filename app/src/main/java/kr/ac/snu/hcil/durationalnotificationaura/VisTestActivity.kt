package kr.ac.snu.hcil.durationalnotificationaura

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import kotlinx.android.synthetic.main.activity_vis_test.*
import kr.ac.snu.hcil.durationalnotificationaura.data.AppNotificationsEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenViewModel
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedNotificationAuraView
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.*
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.random.Random

class VisTestActivity : AppCompatActivity() {

    private var paletteMap: MutableMap<String, Palette> = mutableMapOf()
    private var drawableMap: MutableMap<String, Drawable> = mutableMapOf()
    //private var ENAVs: MutableList<EnhancedNotificationAuraView> = mutableListOf()

    private val bWidth = 80
    private val bHeight = 80

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return Bitmap.createScaledBitmap(bmp, bWidth, bHeight, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vis_test)
    }
    override fun onResume() {
        super.onResume()
        /*
        ENAVs.add(testENAV)
        ENAVs.add(testENAV2)
        ENAVs.add(testENAV3)
        */
        val pm = application.packageManager
        val installedApplications = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val installedApplications2 = installedApplications.filter{ it.packageName == "kr.ac.snu.hcil.durationalnotificationaura" }//"com.google.android.youtube" }

        Log.d("AURA_TEST", "onCreate")
        installedApplications2.map { applicationInfo ->
            val appName = applicationInfo.packageName
            val iconDrawable = pm.getApplicationIcon(appName)
            val bitmap = getBitmapFromDrawable(iconDrawable)
            imageView.setImageDrawable(iconDrawable)
            drawableMap[appName] = iconDrawable
            Palette.Builder(bitmap).also { builder ->
                builder.generate { palette ->
                    palette?.let {
                        paletteMap[appName] = it
                        testEAAV.let{
                            view ->
                            AppNotificationsEnhancedData(appName).also {
                                it.notificationData.add(
                                    NotificationEnhancedData(
                                        installedApplications.indexOf(applicationInfo),
                                        "default",
                                        Calendar.getInstance().timeInMillis,
                                        1000L * 60 * 2
                                    ).also {
                                        it.currEnhancement = 0.8
                                        it.timeElapsed = (0.8 * it.naturalDecay).roundToLong()
                                    })
                                it.notificationData.add(
                                    NotificationEnhancedData(
                                        installedApplications.indexOf(applicationInfo),
                                        "default",
                                        Calendar.getInstance().timeInMillis,
                                        1000L * 60 * 2
                                    ).also {
                                        it.currEnhancement = 0.8
                                        it.timeElapsed = (0.8 * it.naturalDecay).roundToLong()
                                    })
                                it.notificationData.add(
                                    NotificationEnhancedData(
                                        installedApplications.indexOf(applicationInfo),
                                        "default",
                                        Calendar.getInstance().timeInMillis,
                                        1000L * 60 * 2
                                    ).also {
                                        it.currEnhancement = 0.8
                                        it.timeElapsed = (0.8 * it.naturalDecay).roundToLong()
                                    })
                                it.notificationData.add(
                                    NotificationEnhancedData(
                                        installedApplications.indexOf(applicationInfo),
                                        "default",
                                        Calendar.getInstance().timeInMillis,
                                        1000L * 60 * 2
                                    ).also {
                                        it.currEnhancement = 0.8
                                        it.timeElapsed = (0.8 * it.naturalDecay).roundToLong()
                                    })
                                it.notificationData.add(
                                    NotificationEnhancedData(
                                        installedApplications.indexOf(applicationInfo),
                                        "default",
                                        Calendar.getInstance().timeInMillis,
                                        1000L * 60 * 2
                                    ).also {
                                        it.currEnhancement = 0.8
                                        it.timeElapsed = (0.8 * it.naturalDecay).roundToLong()
                                    })
                                it.notificationData.add(
                                    NotificationEnhancedData(
                                        installedApplications.indexOf(applicationInfo),
                                        "default",
                                        Calendar.getInstance().timeInMillis,
                                        1000L * 60 * 2
                                    ).also {
                                        it.currEnhancement = 0.8
                                        it.timeElapsed = (0.8 * it.naturalDecay).roundToLong()
                                    })
                                it.notificationData.add(
                                    NotificationEnhancedData(
                                        installedApplications.indexOf(applicationInfo),
                                        "default",
                                        Calendar.getInstance().timeInMillis,
                                        1000L * 60 * 2
                                    ).also {
                                        it.currEnhancement = 0.8
                                        it.timeElapsed = (0.8 * it.naturalDecay).roundToLong()
                                    })
                            }.let {
                                ANED ->
                                val centerX = imageView.x + imageView.width.toFloat() / 2
                                val centerY = imageView.y + imageView.height.toFloat() / 2
                                Log.d("AURA_TEST", "(" + centerX + ", " + centerY + ") / (" + imageView.x + ", " + imageView.y + ") / (" + imageView.layoutParams.width + ", " + imageView.layoutParams.height + ")")

                                testEAAV.pivotX = centerX - testEAAV.x
                                testEAAV.pivotY = centerY - testEAAV.y
                                textView2.text = (ANED.notificationData.size + 2).toString()
                                textView.text = pm.getApplicationLabel(applicationInfo)
                                view.setEnhanceData(ANED)
                                view.setVisualEffects(
                                    List(ANED.notificationData.size) { index ->
                                        val theta = 2 * PI.toFloat() / ANED.notificationData.size.toFloat() * index.toFloat()
                                        DerivedVisEffect3(
                                            palette,
                                            view.getChildAt(index),
                                            mapOf(
                                                // imageView와의 중심 거리가 상대좌표로 0.26일 때 잘 보임
                                                "brightness" to 0f,
                                                "colorAlpha" to 128f,
                                                "colorRed" to 0f,
                                                "colorGreen" to 0f,
                                                "colorBlue" to 255f,
                                                "shape" to (index % 3).toFloat(),
                                                "pivotX" to testEAAV.pivotX,
                                                "pivotY" to testEAAV.pivotY,
                                                "posRadius" to 324f,
                                                "posAngle" to 360f / ANED.notificationData.size.toFloat() * index.toFloat(),
                                                "roundX" to 0.33f,
                                                "roundY" to 0.33f,
                                                "size" to 11f + index * 5f
                                                //"centerX" to testEAAV.pivotX / testEAAV.width.toFloat() + 0.41f * cos(theta),
                                                //"radiusX" to 11f + index * 10f,
                                                //"centerY" to testEAAV.pivotY / testEAAV.height.toFloat() + 0.41f / testEAAV.height.toFloat() * testEAAV.width.toFloat() * sin(theta),
                                                /*"radiusY" to 11f + index * 10f/*,
                                                "left" to installedApplications.indexOf(applicationInfo).toFloat() * 100f,
                                                "right" to (installedApplications.indexOf(applicationInfo).toFloat() + 1f) * 100f,
                                                "top" to 409f,
                                                "bottom" to 309f*/*/
                                            ),
                                            mapOf(
                                                /*
                                                AnimationTypes.ALPHA to
                                                        AnimationParams(
                                                            arrayOf(0f, 1f).toFloatArray(),
                                                            3000,
                                                            AccelerateDecelerateInterpolator()
                                                        ),
                                                AnimationTypes.SCALE_X to
                                                        AnimationParams(
                                                            arrayOf(0f, 1f).toFloatArray(),
                                                            3000,
                                                            LinearInterpolator()
                                                        ),
                                                AnimationTypes.TRANSLATION_X to
                                                        AnimationParams(
                                                            arrayOf(-100f, 300f).toFloatArray(),
                                                            1000 * (installedApplications.indexOf(applicationInfo)).toLong(),
                                                            LinearInterpolator()
                                                        )
                                                        */
                                            )
                                        )
                                    }.mapIndexed { index, derivedVisEffect3 ->
                                        if (index == 0) {
                                            DerivedVisEffect3(
                                                palette,
                                                view.getChildAt(index),
                                                mapOf(
                                                    // imageView와의 중심 거리가 상대좌표로 0.26일 때 잘 보임
                                                    "brightness" to 2f,
                                                    "shape" to 1f,
                                                    "centerX" to testEAAV.pivotX / testEAAV.width.toFloat() + 0.41f * cos(
                                                        2 * PI.toFloat() / ANED.notificationData.size.toFloat() * index
                                                    ),
                                                    "radiusX" to 40f,
                                                    "centerY" to testEAAV.pivotY / testEAAV.height.toFloat() + 0.41f / testEAAV.height.toFloat() * testEAAV.width.toFloat() * sin(
                                                        2 * PI.toFloat() / ANED.notificationData.size.toFloat() * index
                                                    ),
                                                    "radiusY" to 40f
                                                ),
                                                mapOf()
                                            )
                                        } else derivedVisEffect3
                                    }
                                )
                                ObjectAnimator.ofFloat(testEAAV, "rotation", 0f, 360f).let {
                                    anim ->
                                    anim.setDuration(10000)
                                    anim.repeatCount = ValueAnimator.INFINITE
                                    anim.interpolator = LinearInterpolator()
                                    anim.start()
                                }
                                Log.d("AURA_TEST", "addENAV");
                            }
                        }
                    }
                }
            }
        }

    }

}
