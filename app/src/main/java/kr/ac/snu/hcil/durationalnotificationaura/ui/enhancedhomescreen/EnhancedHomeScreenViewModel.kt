package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import android.util.Log
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedAppNotificationData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationDatum
import java.util.*

class EnhancedHomeScreenViewModel : ViewModel() {

    companion object {
        const val TAG = "VIEWMODEL"
    }
    private var appNotificationLiveData: MutableLiveData<MutableMap<String, EnhancedAppNotificationData>> = MutableLiveData()
    private val mHandler = Handler()
    private var lastUpdateInMillis = Calendar.getInstance().timeInMillis
    private fun updateNotiEnhancement(notiData: EnhancedNotificationDatum, updateInterval: Long): EnhancedNotificationDatum {
        when(notiData.lifeCycle){
            EnhancedNotificationLifeCycle.STATE_2 -> {
                if(notiData.timeElapsed >= notiData.naturalDecay)
                    notiData.lifeCycle = EnhancedNotificationLifeCycle.STATE_5
                else {
                    when (notiData.firstPattern) {
                        EnhancementPattern.DEC -> {
                            notiData.currEnhancement -= notiData.slope * updateInterval
                            if (notiData.currEnhancement < notiData.lowerBound)
                                notiData.currEnhancement = notiData.lowerBound
                        }
                        EnhancementPattern.INC -> {
                            notiData.currEnhancement += notiData.slope * updateInterval
                            if (notiData.currEnhancement > notiData.upperBound)
                                notiData.currEnhancement = notiData.upperBound
                        }
                        EnhancementPattern.EQ -> {}
                    }
                }
            }
            EnhancedNotificationLifeCycle.STATE_4 -> {
                if(notiData.timeElapsed >= notiData.naturalDecay)
                    notiData.lifeCycle = EnhancedNotificationLifeCycle.STATE_5
                else{
                    when(notiData.secondPattern){
                        EnhancementPattern.DEC -> {
                            notiData.currEnhancement -= notiData.slope * updateInterval
                            if(notiData.currEnhancement < notiData.lowerBound)
                                notiData.currEnhancement = notiData.lowerBound
                        }
                        EnhancementPattern.INC -> {
                            notiData.currEnhancement += notiData.slope * updateInterval
                            if(notiData.currEnhancement > notiData.upperBound)
                                notiData.currEnhancement = notiData.upperBound
                        }
                        EnhancementPattern.EQ -> {}
                    }
                }
            }
            EnhancedNotificationLifeCycle.STATE_1 -> {
                notiData.lifeCycle = EnhancedNotificationLifeCycle.STATE_2
                when(notiData.firstPattern){
                    EnhancementPattern.INC -> notiData.slope = (notiData.upperBound - notiData.enhanceOffset) / notiData.firstSaturationTime
                    EnhancementPattern.DEC -> notiData.slope = (notiData.enhanceOffset - notiData.lowerBound) / notiData.firstSaturationTime
                    EnhancementPattern.EQ -> notiData.slope = 0.0
                }
            }
            EnhancedNotificationLifeCycle.STATE_3 -> {
                notiData.lifeCycle = EnhancedNotificationLifeCycle.STATE_4
                when(notiData.secondPattern){
                    EnhancementPattern.INC -> notiData.slope = (notiData.upperBound - notiData.currEnhancement) / notiData.secondSaturationTime
                    EnhancementPattern.DEC -> notiData.slope = (notiData.currEnhancement - notiData.lowerBound) / notiData.secondSaturationTime
                    EnhancementPattern.EQ -> notiData.slope = 0.0
                }
            }
            EnhancedNotificationLifeCycle.STATE_5 -> {
                notiData.currEnhancement = notiData.lowerBound
            }
        }
        notiData.timeElapsed += updateInterval
        return notiData
    }
    private val autoUpdateRunnable = object: Runnable{
        override fun run() {
            val nowInMillis = Calendar.getInstance().timeInMillis
            val prevData = appNotificationLiveData.value
            val newData = prevData?.mapValues{
                it.value.apply{
                    notificationData.apply{
                        map { datum -> updateNotiEnhancement(datum, nowInMillis - lastUpdateInMillis) }
                            .apply{ filter{ datum -> datum.lifeCycle != EnhancedNotificationLifeCycle.STATE_5 || datum.currEnhancement > 0.0} }
                    }
                }
            }?.toMutableMap()
            lastUpdateInMillis = nowInMillis
            appNotificationLiveData.value = newData
            Log.d(TAG, "ViewModel Updated at $nowInMillis")
            mHandler.postDelayed(this, 1000L * 15)
        }
    }
    init{
        //데이터 하드코드는 여기서 하도록 합시다.
        val mutableMap : MutableMap<String, EnhancedAppNotificationData> = mutableMapOf()
        val currTime = Calendar.getInstance().timeInMillis
        for(pn:String in arrayOf(
            "com.google.android.gm",
            "com.google.android.apps.",
            "com.google.android.youtube",
            "com.google.android.calendar",
            "com.kakao.talk"
        )
        ) {
            val newOne = EnhancedAppNotificationData(pn)
                .apply{
                notificationData = mutableListOf()
                var count = Random().nextInt(5)
                while(count >= 0){
                    notificationData.add(
                        EnhancedNotificationDatum(
                            "default", currTime - 1000L * 10 * count, 1000L * 60 * 10
                        ).apply{
                            firstPattern = EnhancementPattern.INC
                        }
                    )
                    count--
                }
            }
            mutableMap[pn] = newOne
        }
        setNotificationByApps(mutableMap)
    }

    fun getNotificationsByApps(): LiveData<MutableMap<String, EnhancedAppNotificationData>>{
        return appNotificationLiveData
    }
    fun setNotificationByApps(data: MutableMap<String, EnhancedAppNotificationData>){
        appNotificationLiveData.value = data
        mHandler.removeCallbacks(autoUpdateRunnable)
        mHandler.post(autoUpdateRunnable)
    }
    override fun onCleared() {
        super.onCleared()
        mHandler.removeCallbacks(autoUpdateRunnable)
    }

}



