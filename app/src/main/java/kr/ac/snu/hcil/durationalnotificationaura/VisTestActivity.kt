package kr.ac.snu.hcil.durationalnotificationaura

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
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.activity_vis_test.*
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenViewModel
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedNotificationAuraView
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AnimationParams
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AnimationTypes
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.DerivedVisEffect
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.DerivedVisEffect2
import java.util.*
import kotlin.math.roundToLong

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

        installedApplications.shuffle()
        /*
        installedApplications.subList(0, 3).map{
                applicationInfo ->
            val appName = applicationInfo.packageName
            val iconDrawable = pm.getApplicationIcon(appName)
            val bitmap = getBitmapFromDrawable(iconDrawable)
            drawableMap[appName] = iconDrawable
            Palette.Builder(bitmap).also{
                    builder -> builder.generate{
                    palette ->
                palette?.let{
                    paletteMap[appName] = it
                    ENAVs[installedApplications.indexOf(applicationInfo)].setVisualEffect(
                        DerivedVisEffect2(
                            palette,
                            ENAVs[installedApplications.indexOf(applicationInfo)],
                            mapOf(),
                            mapOf(
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
                                AnimationTypes.SCALE_Y to
                                        AnimationParams(
                                            arrayOf(0f, 1f).toFloatArray(),
                                            3000,
                                            LinearInterpolator()
                                        ),
                                AnimationTypes.TRANSLATION_X to
                                        AnimationParams(
                                            arrayOf(-500f, 500f).toFloatArray(),
                                            6000,
                                            LinearInterpolator()
                                        )
                            )
                        )
                    )
                }
            }
            }
        }

        ENAVs.forEach{
            it.setVisualData(
                NotificationEnhancedData(
                    ENAVs.indexOf(it),
                    "default",
                    Calendar.getInstance().timeInMillis,
                    1000L * 60 * 2
                ).also{
                    it.currEnhancement = 0.8
                    it.timeElapsed = (0.8 * it.naturalDecay).roundToLong() })
        }
        */

        Log.d("AURA_TEST", "onCreate")
        installedApplications.map { applicationInfo ->
            val appName = applicationInfo.packageName
            val iconDrawable = pm.getApplicationIcon(appName)
            val bitmap = getBitmapFromDrawable(iconDrawable)
            drawableMap[appName] = iconDrawable
            Palette.Builder(bitmap).also { builder ->
                builder.generate { palette ->
                    palette?.let {
                        paletteMap[appName] = it
                        testEAAV.addEnhancedNotificationAuraView(
                            EnhancedNotificationAuraView(testEAAV.context, null).also { view ->
                            view.setVisualData(
                                NotificationEnhancedData(
                                    installedApplications.indexOf(applicationInfo),
                                    "default",
                                    Calendar.getInstance().timeInMillis,
                                    1000L * 60 * 2
                                ).also {
                                    it.currEnhancement = 0.8
                                    it.timeElapsed = (0.8 * it.naturalDecay).roundToLong()
                                })
                            view.setVisualEffect(
                                DerivedVisEffect2(
                                    palette,
                                    view,
                                    mapOf(),
                                    mapOf(
                                        AnimationTypes.ALPHA to
                                                AnimationParams(
                                                    arrayOf(0f, 1f).toFloatArray(),
                                                    3000,
                                                    AccelerateDecelerateInterpolator()
                                                ),
                                        AnimationTypes.SCALE_X to
                                                AnimationParams(
                                                    arrayOf(0f, 2f).toFloatArray(),
                                                    3000,
                                                    LinearInterpolator()
                                                ),
                                        AnimationTypes.SCALE_Y to
                                                AnimationParams(
                                                    arrayOf(0f, 2f).toFloatArray(),
                                                    3000,
                                                    LinearInterpolator()
                                                ),
                                        AnimationTypes.TRANSLATION_X to
                                                AnimationParams(
                                                    arrayOf(-500f, 500f).toFloatArray(),
                                                    100 * (installedApplications.indexOf(applicationInfo)).toLong(),
                                                    LinearInterpolator()
                                                )
                                        )
                                    )
                                )
                            Log.d("AURA_TEST", "addENAV");
                            }
                        )

                    }
                }
            }
        }

    }

}
