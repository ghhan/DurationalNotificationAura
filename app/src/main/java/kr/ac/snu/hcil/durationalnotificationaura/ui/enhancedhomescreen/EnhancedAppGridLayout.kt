package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout

class EnhancedAppGridLayout(context: Context,
                            attributeSet: AttributeSet? = null,
                            defStyleAttr: Int = 0,
                            defStyleRes: Int = 0) : GridLayout(context, attributeSet, defStyleAttr, defStyleRes) {
    private var appPositionMap: MutableMap<String, Pair<Int, Int>> = mutableMapOf()

    fun getAppPositionMap() = appPositionMap

    init{
        //TODO: Start with M * N Grid, Use it like ViewHolder. No Need to Use Add or Remove Logic
    }

    private fun decideLayout(){

    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        (child as EnhancedAppAuraView?)?.let{
                eaav -> eaav.getAppPackageName()?.let{
                packageName -> eaav.getAppPosition()?.let{
                position -> appPositionMap[packageName] = position } }
        }
    }

    override fun removeAllViews() {
        super.removeAllViews()
        appPositionMap = mutableMapOf()
    }

    override fun removeView(view: View?) {
        (view as EnhancedAppAuraView?)?.let{
                eaav -> appPositionMap.remove(eaav.getAppPackageName())
        }
        super.removeView(view)
    }
}