package kr.ac.snu.hcil.durationalnotificationaura

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.TestVisEffect
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.VisEffect

class EnhancedNotificationDataAdapter(context: Context, resourceId: Int)
    : ArrayAdapter<EnhancedAppNotificationData>(context, resourceId){

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item : EnhancedAppNotificationData = getItem(position)!!
        val newView = TestViewGroup(context, null).apply{
            setEnhanceData(item)
            setVisualEffects(List<VisEffect>(item.notificationData.size){TestVisEffect()})
        }
        return newView
    }
}