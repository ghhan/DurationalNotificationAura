package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.enhanced_home_screen_fragment.*
import kr.ac.snu.hcil.durationalnotificationaura.utils.MyNotificationListenerService
import kr.ac.snu.hcil.durationalnotificationaura.R
import kr.ac.snu.hcil.durationalnotificationaura.data.AppNotificationsEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationLifeCycle
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancementPattern
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenFragment.Companion.DEFAULT_START_DECAY_AFTER
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AnimationParams
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AnimationTypes
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.DerivedVisEffect
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.DerivedVisEffect2
import java.util.*

class EnhancedHomeScreenFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = EnhancedHomeScreenFragment()
        const val ACTION = "kr.ac.snu.hcil.durationalnotificationaura.NOTIFICATION_LISTENER"
        const val DEFAULT_START_DECAY_AFTER = 1000L * 60 * 10
        const val NOTIFICATION_CHANNEL_ID = "MY_CHANNEL_ID"
        const val TAG = "TESTING_FRAGMENT"
        const val CHANNEL_ID = "AURA_TESTER"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        view?.let{
            val packageName = (it as TextView).text
            viewModel.getNotificationsByApps().let{
                livedata -> livedata.value?.let{
                data ->
                val notifications = data[packageName]!!.notificationData
                statusView.text = "${(it as TextView).text}\n" +
                        "Number of notifications: ${notifications.size}\n" +
                        "Before Interaction: ${notifications[0]!!.firstPattern}, After Interaction: ${notifications[0]!!.secondPattern}\n" +
                        "Current State: ${notifications[0]!!.lifeCycle}\n" +
                        "Current Enhancement: ${notifications[0]!!.currEnhancement}"
                }
            }
        }
    }

    private fun printShortcuts(){
        val shortcutIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
        val shortcuts = activity!!.packageManager.queryIntentActivities(shortcutIntent, 0)
        shortcuts.forEach{
            Log.d(TAG, "name = ${it.activityInfo.name}, package name = ${it.activityInfo.packageName}")
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var pns = arrayOf(
        "com.google.android.gm",
        "com.android.providers.calendar",
        "com.google.android.youtube",
        "com.google.android.apps.maps"
    )

    lateinit var drawableMap:Map<String, Drawable>

    private lateinit var viewModel: EnhancedHomeScreenViewModel
    private lateinit var packageNameAdapter: ArrayAdapter<String>
    private val notificationReceiver = NotificationReceiver()
    private val intentFilter = IntentFilter().also{ it.addAction(ACTION)}

    var NOTIFICATION_ID = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.enhanced_home_screen_fragment, container, false).also{

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.startService(Intent(activity, MyNotificationListenerService::class.java))
        //service가 시작하면, 이미 viewmodel setting은 시작됨 (service의 onstart)

        packageNameAdapter = ArrayAdapter(context!!, R.layout.simple_spinner_dropdown_item)
        packageNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        gridLayout.let{
            it.clipChildren = false
            it.clipToPadding = false
            it.clipToOutline = false
        }

        triggerButton.setOnClickListener{
            //TODO: view 중 packagename이 같은 애의 data를 Stage 1 상태로 삽입

            val notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val mBuilder = NotificationCompat.Builder(activity!!, CHANNEL_ID).also{
                    builder ->
                builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                builder.setContentTitle("Test Notification")
                builder.setContentText("Much longer text that cannot fit one line...")
                builder.setStyle(
                    NotificationCompat.BigTextStyle().bigText("Much longer text that cannot fit one line...")
                )
                builder.priority = NotificationCompat.PRIORITY_DEFAULT
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Here is Readable Title",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            }

            notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
        }

        // Set selected View's Noti Data to STATE_2, "Triggered But Not Interacted"
        interactButton.setOnClickListener {
            viewModel.getNotificationsByApps().value?.let{
                    currData ->
                currData.mapValues {
                        entry ->
                    if(entry.key == packageNameSpinner.selectedItem){
                        entry.value.notificationData.forEach{
                                data -> data.lifeCycle = EnhancedNotificationLifeCycle.STATE_2 // TODO Interacted -> Not State_3?
                        }
                    }
                }
                viewModel.setNotificationByApps(currData)
            }
        }

        // Set selected View's Noti Data to STATE_1, "Just Triggered"
        resetButton.setOnClickListener{
            viewModel.getNotificationsByApps().value?.let{
                    currData ->
                currData.mapValues {
                        entry ->
                    if(entry.key == packageNameSpinner.selectedItem){
                        entry.value.notificationData.forEach{
                                data ->
                            data.currEnhancement = data.enhanceOffset;
                            data.timeElapsed = 0;
                            data.lifeCycle = EnhancedNotificationLifeCycle.STATE_1
                        }
                    }
                }
                viewModel.setNotificationByApps(currData)
            }
        }

        // Set all Views' Noti Data to STATE_1, "Just Triggered" and reinitialize all parameters
        resetAllButton.setOnClickListener{
            viewModel.getNotificationsByApps().value?.let{
                currData ->
                currData.mapValues {
                        entry ->
                    entry.value.notificationData.forEach{
                        data ->
                    data.currEnhancement = data.enhanceOffset
                    data.timeElapsed = 0
                    data.lifeCycle = EnhancedNotificationLifeCycle.STATE_1 } }
                viewModel.setNotificationByApps(currData)
            }
        }

        // Grid Layout ChildViews(EnhacedAppAuraView) Design Logic
        for(index in 0..19){
            gridLayout.addView(
                EnhancedAppAuraView(context!!, null).apply{
                    if((index % 8) / 4 == 0){
                        if(index % 2 == 0 )
                            background = ColorDrawable(Color.LTGRAY)
                        else
                            background = ColorDrawable(Color.GRAY)
                    }
                    else{
                        if(index % 2 == 0 )
                            background = ColorDrawable(Color.GRAY)
                        else
                            background = ColorDrawable(Color.LTGRAY)
                    }
                } //,
//                GridLayout.LayoutParams(
//                    GridLayout.spec(index / 4, 1, GridLayout.CENTER,1f),
//                    GridLayout.spec(index % 4, 1, GridLayout.CENTER,1f)
//                ).also{
//                    param -> param.width = 250; param.height=250
//                } // just for debug
            )
        }

        // Live Data View Model Observer Logic: Updates packageNameAdapter and Aura Views Animation according to EHSViewModel
        viewModel = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application))
            .get(EnhancedHomeScreenViewModel::class.java)
        viewModel.getNotificationsByApps().observe(this,
            Observer {

                packageNameAdapter.clear()
                var counter = 0
                val myIterator = it!!.iterator()
                while (myIterator.hasNext()) {
                    myIterator.next().let { entry ->
                        val packageName = entry.key
                        val data = entry.value
                        if (data.notificationData.size > 0 && counter < 20) { // Only show visual effects of those with notifications
                        packageNameAdapter.add(packageName)

//                        gridLayout.addView(EnhancedAppAuraView(context!!, null)) // just for debug
                        (gridLayout.getChildAt(counter) as EnhancedAppAuraView).let { view ->
                            view.tag = packageName
                            view.background = viewModel.drawableMap[packageName]
                            view.setEnhanceData(data)

                                view.setVisualEffects(List(data.notificationData.size) { index ->
                                    DerivedVisEffect(
                                            viewModel.paletteMap[packageName]!!,
                                            view.getChildAt(index),
                                            mapOf(),
                                            mapOf(
                                                    AnimationTypes.ALPHA to
                                                            AnimationParams(
                                                                    arrayOf(0f, 1f).toFloatArray(),
                                                                    3000,
                                                                    AccelerateDecelerateInterpolator()
                                                            ),
                                                    AnimationTypes.SCALE_X to
                                                            AnimationParams(
                                                                    arrayOf(0f, 1f).toFloatArray(),
                                                                    3000,
                                                                    LinearInterpolator()
                                                            ),
                                                    AnimationTypes.SCALE_Y to
                                                            AnimationParams(
                                                                    arrayOf(0f, 1f).toFloatArray(),
                                                                    3000,
                                                                    LinearInterpolator()
                                                            )
                                            )
                                    )
                                })
                            }
                            counter++
                        }
                    }
                }
                packageNameSpinner.adapter = packageNameAdapter
            }
        )
        packageNameSpinner.onItemSelectedListener = this
        printShortcuts()
    }

    //TODO: 생몰주기 때문에 문제 생길 수 있는지 체크 필요함
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.registerReceiver(notificationReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
        activity?.registerReceiver(notificationReceiver, intentFilter)
    }
    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(notificationReceiver)
    }

    private fun initializeEnhancedAppNotiMap(ids: IntArray, packageNames: Array<String>, postTimes: LongArray) =
        packageNames.distinct().map{
            distinctStr ->
            val list = mutableListOf<NotificationEnhancedData>()
            packageNames.forEachIndexed{
                index, str ->
                if(distinctStr == str) list.add(
                    createSingleNotification(postTimes[index], DEFAULT_START_DECAY_AFTER)
                )
            }
            distinctStr to list
        }.toMap().let{
            it.map{
                entry -> entry.key to AppNotificationsEnhancedData(entry.key).also{
                    datum -> datum.notificationData = entry.value
                }
            }.toMap()
        }

    // Creates and Adds newly generated notification to View Model
    private fun addNewEnhancedNotification(id: Int, packageName: String, postTime: Long): MutableMap<String, AppNotificationsEnhancedData>{
        var currentData: MutableMap<String,AppNotificationsEnhancedData>? = viewModel.getNotificationsByApps().value
        if(currentData != null){
            if(packageName in currentData.keys){
                currentData[packageName]!!.notificationData.add(
                    createSingleNotification(postTime, DEFAULT_START_DECAY_AFTER)
                )
            }
            else{
                currentData[packageName] = AppNotificationsEnhancedData(packageName).also{
                    it.notificationData = mutableListOf(
                        createSingleNotification(postTime, DEFAULT_START_DECAY_AFTER)
                    )
                }
            }
        }
        else{
            currentData = mutableMapOf(
                packageName to AppNotificationsEnhancedData(packageName).also{
                    it.notificationData = mutableListOf(
                        createSingleNotification(postTime, DEFAULT_START_DECAY_AFTER)
                    )
                }
            )
        }
        return currentData
    }

    // Changes notiData status of dismissed Notification to STATE_3, "Just Interacted"
    private fun dismissNotification(id: Int, packageName: String, postTime: Long):MutableMap<String, AppNotificationsEnhancedData>{
        val currentData: MutableMap<String,AppNotificationsEnhancedData>? = viewModel.getNotificationsByApps().value

        return currentData!!.mapValues{
            if(packageName == it.key)
                it.value.apply{
                    notificationData = notificationData.map{
                        datum -> datum.apply{ lifeCycle = EnhancedNotificationLifeCycle.STATE_3 }
                    }.toMutableList()
                }
            else
                it.value
        }.toMutableMap()
    }

    // Currently randomly generates a single notification
    private fun createSingleNotification(initTime: Long, naturalDecay: Long): NotificationEnhancedData{
        val firsttrend = Random().nextInt() % 3
        val secondtrend = Random().nextInt() % 3

        return NotificationEnhancedData(
            "default",
            initTime,
            naturalDecay
        ).apply{
            when(firsttrend){
                0 -> {
                    firstPattern = EnhancementPattern.INC
                }
                1 -> {
                    firstPattern = EnhancementPattern.DEC
                    enhanceOffset = 1.0
                    currEnhancement = enhanceOffset
                }
                2 -> {
                    firstPattern = EnhancementPattern.EQ
                    enhanceOffset = 0.5
                    currEnhancement = enhanceOffset
                }
            }

            when(secondtrend){
                0 -> {firstPattern = EnhancementPattern.INC}
                1 -> {firstPattern = EnhancementPattern.DEC}
                2 -> {firstPattern = EnhancementPattern.EQ}
            }
        }
    }

    inner class NotificationReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle: Bundle? = intent?.extras
            var newData: MutableMap<String, AppNotificationsEnhancedData> = mutableMapOf()
            when(bundle?.getString("event")){
                "Initialized" -> {
                    val idArray = bundle.getIntArray("IDs")
                    val pNArray = bundle.getStringArray("packageNames")
                    val postTimes = bundle.getLongArray("postTimes")
                    newData = initializeEnhancedAppNotiMap(idArray!!, pNArray!!, postTimes!!).toMutableMap()
                }
                "Posted" -> {
                    //TODO:
                    val id = bundle.getInt("ID")
                    val packageName = bundle.getString("packageName")
                    val postTime = bundle.getLong("postTime")
                    newData = addNewEnhancedNotification(id, packageName!!, postTime)
                }
                "Removed" -> {
                    //TODO:
                    val id = bundle.getInt("ID")
                    val packageName = bundle.getString("packageName")
                    val postTime = bundle.getLong("postTime")
                    newData = dismissNotification(id, packageName!!, postTime)
                }
            }
            viewModel.setNotificationByApps(newData)
        }
    }
}
