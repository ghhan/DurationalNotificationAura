package kr.ac.snu.hcil.durationalnotificationaura

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.graphics.Palette
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_color_test.*
import java.text.DecimalFormat

class ColorTestActivity : AppCompatActivity() {

    private var paletteMap: MutableMap<String, Palette> = mutableMapOf()
    private val bWidth = 80
    private val bHeight = 80

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return Bitmap.createScaledBitmap(bmp, bWidth, bHeight, false)
    }

    private fun getSwatchPopulationInPercent(swatch: Palette.Swatch, size: Int): String{
        val df = DecimalFormat("#.##")
        return "${df.format(swatch.population.toFloat() / size * 100) }%"
    }

    private fun getMostPopularSwatch(palette: Palette): MutableList<Palette.Swatch> = palette.swatches.sortedByDescending {
            swatch: Palette.Swatch? ->  swatch?.population ?: 0
    }.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_test)

        packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES)
            .also{
                installedPackages -> installedPackages
                .map{
                        pi ->
                    val bitmap = getBitmapFromDrawable(packageManager.getApplicationIcon(pi.packageName))
            Palette.Builder(bitmap).also{builder -> builder.maximumColorCount(8)}.generate{ p -> p?.let{ palette ->
                paletteMap[pi.packageName] = palette
                colorGrid
                    .let{
                        gridLayout -> val mWidth = 160; val mHeight = 80
                        gridLayout.addView(ImageView(applicationContext).apply{ setImageBitmap(bitmap) })

                        var count = 0
                        getMostPopularSwatch(palette).forEach { swatch ->
                            gridLayout.addView(TextView(applicationContext).apply {
                                width = mWidth; height = mHeight
                                background = ColorDrawable(swatch.rgb)
                                text = getSwatchPopulationInPercent(swatch, bWidth * bHeight)
                            })
                            count++
                        }

                        while(count < 16){
                            gridLayout.addView(TextView(applicationContext).apply{
                                width = mWidth; height = mHeight
                                background = ColorDrawable(Color.LTGRAY)
                                text = "0.00%"
                            })
                            count++
                        }


                        /*
                        val df = DecimalFormat("#.##")
                        gridLayout.addView(TextView(applicationContext).apply{
                            width = mWidth; height = mHeight
                            background = ColorDrawable(palette.getLightVibrantColor(Color.LTGRAY))
                            text = df.format((palette.lightVibrantSwatch?.population ?: 0).toFloat() / (bWidth * bHeight) * 100f)
                        })
                        gridLayout.addView(TextView(applicationContext).apply{
                            width = mWidth; height = mHeight
                            background = ColorDrawable(palette.getVibrantColor(Color.LTGRAY))
                            text = df.format((palette.vibrantSwatch?.population ?: 0).toFloat() / (bWidth * bHeight) * 100f)
                        })
                        gridLayout.addView(TextView(applicationContext).apply{
                            width = mWidth; height = mHeight
                            background = ColorDrawable(palette.getDarkVibrantColor(Color.LTGRAY))
                            text = df.format((palette.darkVibrantSwatch?.population ?: 0).toFloat() / (bWidth * bHeight) * 100f)
                        })
                        gridLayout.addView(TextView(applicationContext).apply{
                            width = mWidth; height = mHeight
                            background = ColorDrawable(palette.getLightMutedColor(Color.LTGRAY))
                            text = df.format((palette.lightMutedSwatch?.population ?: 0).toFloat() / (bWidth * bHeight) * 100f)
                        })
                        gridLayout.addView(TextView(applicationContext).apply{
                            width = mWidth; height = mHeight
                            background = ColorDrawable(palette.getMutedColor(Color.LTGRAY))
                            text = df.format((palette.mutedSwatch?.population ?: 0).toFloat() / (bWidth * bHeight) * 100f)
                        })
                        gridLayout.addView(TextView(applicationContext).apply{
                            width = mWidth; height = mHeight
                            background = ColorDrawable(palette.getDarkMutedColor(Color.LTGRAY))
                            text = df.format((palette.darkMutedSwatch?.population ?: 0).toFloat() / (bWidth * bHeight) * 100f)
                        })
                        */
                    }
            } } }
        }
    }
}