package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import java.util.*

class EnhancedHomeScreenViewModel : ViewModel() {
    private var appNotificationLiveData: MutableLiveData<MutableMap<String, EnhancedAppNotificationData>> = MutableLiveData()

    init{
        //데이터 하드코드는 여기서 하도록 합시다.
        val mutableMap : MutableMap<String, EnhancedAppNotificationData> = mutableMapOf()
        val currTime = Calendar.getInstance().timeInMillis
        for(pn:String in arrayOf(
            "com.google.android.gm",
            "com.google.android.apps.",
            "com.google.android.youtube",
            "com.google.android.calendar",
            "com.kakao.talk")
        ) {
            val newOne = EnhancedAppNotificationData(pn)
                .apply{
                notificationData = mutableListOf()
                var count = Random().nextInt(5)
                while(count >= 0){
                    notificationData.add(
                        EnhancedNotificationDatum(
                            "default", currTime, currTime + 1000L * 60 * 30
                        ).apply{
                            firstPattern = EnhancementPattern.INC
                        }
                    )
                    count--
                }
            }
            mutableMap[pn] = newOne
        }
        appNotificationLiveData.value = mutableMap
    }

    fun getNotificationsByApps(): LiveData<MutableMap<String, EnhancedAppNotificationData>>{
        return appNotificationLiveData
    }
    fun setNotificationByApps(data: MutableMap<String, EnhancedAppNotificationData>){
        appNotificationLiveData.value = data
    }
}



