package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import com.mikhaellopez.circularimageview.CircularImageView
import kr.ac.snu.hcil.durationalnotificationaura.data.AppNotificationsEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AbstractVisEffect

class EnhancedAppAuraView(context: Context, attributeSet: AttributeSet?): ViewGroup(context, attributeSet) {

    /* One View -> One Visual Object (Translations, Alpha, Rotation, Scale)
     * ViewGroup Controls Multiple Views
     * ViewGroup Sets Layout of Views, Rendering Order
     */
    companion object {
        const val TAG = "APP_AURA_VIEW"
    }

    init{
        //clipChildren = false
        //clipToOutline = false
        //clipToPadding = false
    }

    var appPackageName: String? = null
    private var notificationIDs: MutableList<Int> = mutableListOf()
    var appPosition: Pair<Int, Int>? = null

    private fun findViewWithId(id: Int): View?{
        val currentSize = childCount
        for(index:Int in 0..(currentSize-1)){
            val child = getChildAt(index)
            if(!(child is EnhancedNotificationAuraView))
                continue
            if(child.id == id)
                return child
        }
        return null
    }

    fun setImageView(imageView: CircularImageView){
        addView(imageView)
    }

    fun setEnhanceData(enhanceData: AppNotificationsEnhancedData) {
        if(appPackageName == null){
            appPackageName = enhanceData.packageName
            appPosition = enhanceData.positionInScreen
            enhanceData.notificationData.forEach{
                notificationIDs.add(it.id)
                addView(
                    EnhancedNotificationAuraView(context, null)
                        .also { view ->
                            view.setVisualData(it)
                            view.tag = it.id
                            view.id = it.id
                            view.layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                )
            }
        }
        else{
            //update logic
            val update = enhanceData.notificationData.filter{
                data -> (data.id in notificationIDs)
            }

            update.forEach{
                data ->
                findViewWithId(data.id)?.let{
                    view -> (view as EnhancedNotificationAuraView).setVisualData(data)
                }
            }

            val add = enhanceData.notificationData.filterNot{
                    data -> (data.id in notificationIDs)
            }

            add.forEach{
                notificationIDs.add(it.id)
                addView(
                    EnhancedNotificationAuraView(context, null)
                        .also { view ->
                            view.setVisualData(it)
                            view.tag = it.id
                            view.id = it.id
                            view.layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                //, childCount - 1
                )
            }

            //TODO: remove logic
            val remove = notificationIDs.filterNot{
                    id -> id in enhanceData.notificationData.map{notiData -> notiData.id}
            }
        }
    }

    fun setVisualEffects(visualEffects: List<AbstractVisEffect>){
        visualEffects.forEachIndexed{
            index, visEffect ->
            val view = getChildAt(index)
            if(view is EnhancedNotificationAuraView)
                view.setVisualEffect(visEffect)
        }
    }

    fun addEnhancedNotificationAuraView(visData: NotificationEnhancedData, visEffect: AbstractVisEffect){
        addView(
            EnhancedNotificationAuraView(context, null).also{
                    view -> view.setVisualData(visData); view.setVisualEffect(visEffect)
            } as View
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthpixels = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthmode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightpixels = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightmode = View.MeasureSpec.getMode(heightMeasureSpec)

        val columnCount = (parent as GridLayout).columnCount
        //TODO: consider device orientation

        super.onMeasure(
            View.MeasureSpec.makeMeasureSpec(widthpixels/columnCount, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(widthpixels/columnCount, View.MeasureSpec.EXACTLY)
        )


        for(idx in 0..(childCount-1)){
            val child = getChildAt(idx)
            child.measure(
                View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(measuredHeight, View.MeasureSpec.EXACTLY)
            )
        }

    }

    // View Group의 전체적인 배치를 결정하는 모듈
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val defaultLeftPadding = 0
        val defaultRightPadding = 0
        val defaultTopPadding = 0
        val defaultBottomPadding = 0

        val imageLeftPadding = defaultLeftPadding + 50
        val imageRightPadding = defaultRightPadding + 50
        val imageTopPadding = defaultTopPadding + 50
        val imageBottomPadding = defaultBottomPadding + 50

        if(changed){
            Log.d(TAG, "# of Children in a view of id $tag: $childCount")
            for(idx in 0..(childCount - 1)){
                val child : View = getChildAt(idx)
                Log.d(TAG, "${child.id} -  l: $l, t: $t, r: $r, b: $b")
                if(child is EnhancedNotificationAuraView)
                    child.layout(defaultLeftPadding, defaultTopPadding, (r - l) - defaultRightPadding, (b - t) - defaultBottomPadding)
                else
                    child.layout(imageLeftPadding, imageTopPadding, (r - l) - imageRightPadding, (b - t) - imageBottomPadding)
            }
        }
    }

}