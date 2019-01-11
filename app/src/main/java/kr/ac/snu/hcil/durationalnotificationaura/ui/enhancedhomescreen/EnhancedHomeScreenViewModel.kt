package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.app.Application
import android.arch.lifecycle.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.graphics.Palette
import android.util.Log
import kr.ac.snu.hcil.durationalnotificationaura.data.*
import java.util.*

class EnhancedHomeScreenViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "AURA_VIEW_MODEL"
        const val UPDATE_INTERVAL = 1000L * 15
    }

    //LiveData
    private var appNotificationLiveData: MutableLiveData<MutableMap<String, AppNotificationsEnhancedData>> = MutableLiveData()
    private val mHandler = Handler()
    private var lastUpdateInMillis = Calendar.getInstance().timeInMillis
    private var currentScreenNumber: MutableLiveData<Int> = MutableLiveData()

    private fun updateNotiEnhancement(notiData: NotificationEnhancedData, updateInterval: Long): NotificationEnhancedData {
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
            mHandler.postDelayed(this, UPDATE_INTERVAL)
        }
    }

    var paletteMap: MutableMap<String, Palette> = mutableMapOf()
    var drawableMap: MutableMap<String, Drawable> = mutableMapOf()

    //
    private val bWidth = 80
    private val bHeight = 80

    private fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bmp = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return Bitmap.createScaledBitmap(bmp, bWidth, bHeight, false)
    }

    init{
        //데이터 하드코드 테스트는 여기서 하도록 합시다.
        val pm = application.packageManager
        val installedPackages = pm.getInstalledPackages(PackageManager.GET_ACTIVITIES)
        val installedApplications = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        installedApplications.map{
            applicationInfo ->
            val appName = applicationInfo.packageName
            val iconDrawable = pm.getApplicationIcon(appName)
            val bitmap = getBitmapFromDrawable(iconDrawable)
            drawableMap[appName] = iconDrawable
            Palette.Builder(bitmap).also{
                    builder -> builder.generate{
                    palette ->
                palette?.let{ paletteMap[appName] = it }
            }
            }
        }

        val random = Random()
        val mutableMap = mutableMapOf<String, AppNotificationsEnhancedData>()

        var currScreenNum = 0
        var currPosNum = 0

        val mustAppList = listOf(
            "kr.ac.snu.hcil.durationalnotificationaura",
            "com.kakao.talk",
            "com.android.phone",
            "com.google.android.gm",
            "com.android.mms",
            "com.facebook.mlite",
            "com.android.providers.calendar",
            "com.google.android.apps.maps",
            "android"
        )

        for(ai in installedApplications){
            val appName = ai.packageName
            if(appName in mustAppList){
                mutableMap[appName] = AppNotificationsEnhancedData(
                    appName,
                    currScreenNum,
                    Pair( currPosNum % 4, currPosNum / 4)
                )
                currPosNum++
            }
        }

        /*
        for(ai in installedApplications.shuffled()){
            val appName = ai.packageName
            val randInt = random.nextInt()

            if(currScreenNum == 5){
                if(appName in mustAppList)
                    continue
                else
                    mutableMap[appName] = AppNotificationsEnhancedData(appName)
            }

            else{
                if(appName in mustAppList)
                    continue

                if(randInt % 2 == 0){
                    mutableMap[appName] = AppNotificationsEnhancedData(
                        appName,
                        currScreenNum,
                        Pair( currPosNum % 4, currPosNum / 4)
                    )
                }
                else{
                    mutableMap[appName] = AppNotificationsEnhancedData(
                        appName
                    )
                }

                currPosNum++
                if(currPosNum == 20){
                    currScreenNum++
                    currPosNum = 0
                }
            }
        }
        */
        setNotificationByApps(mutableMap)

        /*
        installedPackages.map{
            pi ->
            val packageName = pi.packageName
            val iconDrawable = pm.getApplicationIcon(packageName)
            val bitmap = getBitmapFromDrawable(iconDrawable)
            drawableMap[packageName] = iconDrawable
            Palette.Builder(bitmap).also{
                builder -> builder.generate{
                palette ->
                palette?.let{ paletteMap[packageName] = it }
                }
            }
        }

        val random = Random()
        val mutableMap = mutableMapOf<String, AppNotificationsEnhancedData>()

        var currScreenNum = 0
        var currPosNum = 0

        for(pi in installedPackages.shuffled()){
            val packageName = pi.packageName
            val randInt = random.nextInt()

            if(currScreenNum == 5){
                mutableMap[packageName] = AppNotificationsEnhancedData(
                    packageName
                )
            }
            else{
                if(randInt % 2 == 0){
                    mutableMap[packageName] = AppNotificationsEnhancedData(
                        packageName,
                        currScreenNum,
                        Pair( currPosNum % 4, currPosNum / 4)
                    )
                }
                else{
                    mutableMap[packageName] = AppNotificationsEnhancedData(
                        packageName
                    )
                }

                currPosNum++
                if(currPosNum == 20){
                    currScreenNum++
                    currPosNum = 0
                }
            }
        }
        setNotificationByApps(mutableMap)
        */
    }

    fun getCurrentScreenNumber():LiveData<Int>{
        return currentScreenNumber
    }

    fun setCurrentScreenNumber(position: Int){
        currentScreenNumber.value = position
    }

    fun getNotificationByApps(): LiveData<MutableMap<String, AppNotificationsEnhancedData>>{
        return appNotificationLiveData
    }

    fun setNotificationByApps(data: MutableMap<String, AppNotificationsEnhancedData>){
        appNotificationLiveData.value = data
        mHandler.removeCallbacks(autoUpdateRunnable)
        mHandler.post(autoUpdateRunnable)
    }

    fun getEnhancementDataInCurrentScreen(position: Int): LiveData<MutableMap<String, AppNotificationsEnhancedData>> =
        Transformations.map(appNotificationLiveData){
            enhancementMap -> enhancementMap.filter{
                it.value.screenNumber == position
            }.toMutableMap()
        }

    override fun onCleared() {
        super.onCleared()
        mHandler.removeCallbacks(autoUpdateRunnable)
    }

}



