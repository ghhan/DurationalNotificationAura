package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.util.AttributeSet
import android.widget.GridLayout

class EnhancedAppGridLayout(context: Context,
                            attributeSet: AttributeSet?
) : GridLayout(
    context,
    attributeSet
) {

    companion object {
        const val DEFAULT_COLUMN_COUNT = 4
        const val DEFAULT_ROW_COUNT = 5
    }

    private var appPositionMap: MutableMap<String, Pair<Int, Int>> = mutableMapOf()

    init{
        val gridSize = DEFAULT_COLUMN_COUNT * DEFAULT_ROW_COUNT

        for(index:Int in (0..gridSize)) {
            addView(
                EnhancedAppAuraView(context, null)
            )
        }
    }

    fun initialzeAppPositions(appPositions: MutableMap<String, Pair<Int, Int>>){
        appPositionMap = appPositions
        for(entry in appPositionMap){
            val packageName = entry.key
            val position = entry.value
            (getChildAt(position.first + position.second * columnCount) as EnhancedAppAuraView?)?.let{
                it.appPackageName = packageName
                it.appPosition = position
            }
        }
    }
}