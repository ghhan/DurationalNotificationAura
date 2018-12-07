package kr.ac.snu.hcil.durationalnotificationaura

import android.content.pm.PackageManager
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
import kotlinx.android.synthetic.main.activity_color_test.*

class ColorTestActivity : AppCompatActivity() {

    private var paletteMap: MutableMap<String, Palette> = mutableMapOf()

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return Bitmap.createScaledBitmap(bmp, 50, 50, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_test)

        packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES).also{
                installedPackages -> installedPackages.map{
                pi ->
            val bitmap = getBitmapFromDrawable(packageManager.getApplicationIcon(pi.packageName))
            Palette.from(bitmap).generate{
                    p -> p?.let{
                paletteMap[pi.packageName] = it
                colorGrid.let{
                        gridLayout ->
                    val mWidth = 20
                    val mHeight = 50

                    gridLayout.addView(Button(applicationContext).apply{
                        width = 50
                        height = 50
                        background = BitmapDrawable(resources, bitmap)
                    })

                    gridLayout.addView(Button(applicationContext).apply{
                        width = mWidth
                        height = mHeight
                        background = ColorDrawable(it.getLightVibrantColor(Color.LTGRAY))
                        text = "light_vibrant"
                    })
                    gridLayout.addView(Button(applicationContext).apply{
                        width = mWidth
                        height = mHeight
                        background = ColorDrawable(it.getVibrantColor(Color.LTGRAY))
                        text = "vibrant"
                    })
                    gridLayout.addView(Button(applicationContext).apply{
                        width = mWidth
                        height = mHeight
                        background = ColorDrawable(it.getDarkVibrantColor(Color.LTGRAY))
                        text = "dark_vibrant"
                    })
                    gridLayout.addView(Button(applicationContext).apply{
                        width = mWidth
                        height = mHeight
                        background = ColorDrawable(it.getLightMutedColor(Color.LTGRAY))
                        text = "light_muted"
                    })
                    gridLayout.addView(Button(applicationContext).apply{
                        width = mWidth
                        height = mHeight
                        background = ColorDrawable(it.getMutedColor(Color.LTGRAY))
                        text = "muted"
                    })
                    gridLayout.addView(Button(applicationContext).apply{
                        width = mWidth
                        height = mHeight
                        background = ColorDrawable(it.getDarkMutedColor(Color.LTGRAY))
                        text = "dark_muted"
                    })
                }

            } } } }
    }
}
