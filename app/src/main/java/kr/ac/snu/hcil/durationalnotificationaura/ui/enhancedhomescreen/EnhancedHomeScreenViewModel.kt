package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.BroadcastReceiver
import kr.ac.snu.hcil.durationalnotificationaura.EnhancedAppNotificationData

class EnhancedHomeScreenViewModel : ViewModel() {
    private var appNotificationLiveData: MutableLiveData<MutableMap<String, EnhancedAppNotificationData>> = MutableLiveData()

    fun getNotificationsByApps(): LiveData<MutableMap<String, EnhancedAppNotificationData>>{
        return appNotificationLiveData
    }
    fun setNotificationByApps(data: MutableMap<String, EnhancedAppNotificationData>){
        appNotificationLiveData.value = data
    }
}



