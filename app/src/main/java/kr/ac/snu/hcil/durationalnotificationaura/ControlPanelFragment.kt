package kr.ac.snu.hcil.durationalnotificationaura

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
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.EntryXComparator
import kotlinx.android.synthetic.main.control_panel_fragment.*
import kr.ac.snu.hcil.durationalnotificationaura.data.AppNotificationsEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationLifeCycle
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancementPattern
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenFragment
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenViewModel
import kr.ac.snu.hcil.durationalnotificationaura.utils.MyNotificationListenerService
import kr.ac.snu.hcil.durationalnotificationaura.utils.MyXAxisValueFormatter
import kr.ac.snu.hcil.durationalnotificationaura.utils.NotificationRandomGenerator
import java.util.*

class ControlPanelFragment: Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        const val ACTION = "kr.ac.snu.hcil.durationalnotificationaura.NOTIFICATION_LISTENER"
        const val NOTIFICATION_CHANNEL_ID = "MY_CHANNEL_ID"
        const val CHANNEL_ID = "AURA_TESTER"
    }

    private lateinit var viewModel: EnhancedHomeScreenViewModel
    private lateinit var packageNameAdapter: ArrayAdapter<String>
    private lateinit var chartAdapter: ArrayAdapter<String>
    private var currScreenNumber = 0
    private val notificationReceiver = NotificationReceiver()
    private val intentFilter = IntentFilter().also{ it.addAction(ACTION)}
    private var generatedNotificationID = 0

    private lateinit var currentSelectedNotifications : MutableList<NotificationEnhancedData>


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        packageNameAdapter = ArrayAdapter(context!!, R.layout.simple_spinner_dropdown_item)
        packageNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        packageNameSpinner.onItemSelectedListener = this

        chartAdapter = ArrayAdapter(context!!, R.layout.simple_spinner_dropdown_item)
        chartAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        chartSpinner.adapter = chartAdapter

        activity?.startService(Intent(activity, MyNotificationListenerService::class.java))

        viewModel = activity?.run{
            ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(application))
                .get(EnhancedHomeScreenViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        viewModel.getNotificationByApps().observe(this,
            Observer{
                dataMap ->
                packageNameAdapter.clear()
                dataMap?.filter{
                    it.value.screenNumber == this.currScreenNumber
                }?.forEach{
                    filteredEntry -> packageNameAdapter.add(filteredEntry.key)
                }
                packageNameSpinner.adapter = packageNameAdapter
            }
        )

        viewModel.getCurrentScreenNumber().observe(this,
            Observer{
                    data -> data?.let{
                    screenNum ->
                    packageNameAdapter.clear()
                    this.currScreenNumber = screenNum
                    viewModel.getNotificationByApps().value?.filter{
                        it.value.screenNumber == screenNum
                    }?.forEach{
                            filteredEntry -> packageNameAdapter.add(filteredEntry.key)
                    }
                }
                packageNameSpinner.adapter = packageNameAdapter
            }
        )

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

            notificationManager.notify(generatedNotificationID, mBuilder.build())
            if (generatedNotificationID < Int.MAX_VALUE)
                generatedNotificationID++
            else
                generatedNotificationID = 0
        }

        interactButton.setOnClickListener {
            Log.i("interaction", currentSelectedNotifications.toString())
            viewModel.getEnhancementDataInCurrentScreen(currScreenNumber).value?.let{
                    currData ->
                Log.i("interaction", currData.toString())
                val newData = currData.mapValues {
                        entry ->
                    if(entry.key == packageNameSpinner.selectedItem){
                        entry.value.apply{
                            notificationData.forEach{
                                    data ->
                                data.lifeCycle = EnhancedNotificationLifeCycle.STATE_3
                                data.interactionTime = System.currentTimeMillis()
                                data.interactionEnhancement = data.currEnhancement
                            }
                            drawChart(currentSelectedNotifications, 0)
                        }
                    }
                    else{
                        entry.value
                    }
                }.toMutableMap()
                viewModel.setNotificationByApps(newData)
            }
        }

        resetButton.setOnClickListener{
            viewModel.getEnhancementDataInCurrentScreen(currScreenNumber).value?.let{
                    currData ->
                val newData = currData.mapValues {
                        entry ->
                    if(entry.key == packageNameSpinner.selectedItem){
                        entry.value.apply{
                            notificationData.forEach{
                                    data -> data.currEnhancement = data.enhanceOffset
                                data.timeElapsed = 0
                                data.lifeCycle = EnhancedNotificationLifeCycle.STATE_1
                            }
                        }
                    }
                    else{
                        entry.value
                    }
                }.toMutableMap()
                viewModel.setNotificationByApps(newData)
            }
        }

        resetAllButton.setOnClickListener{
            viewModel.getEnhancementDataInCurrentScreen(currScreenNumber).value?.let{
                    currData ->
                val newData = currData.mapValues {
                        entry: Map.Entry<String, AppNotificationsEnhancedData>  ->
                    if(entry.value.screenNumber == currScreenNumber){
                        entry.value.apply{
                            notificationData.forEach{
                                    data ->
                                data.currEnhancement = data.enhanceOffset
                                data.timeElapsed = 0
                                data.lifeCycle = EnhancedNotificationLifeCycle.STATE_1
                            }
                        }
                    }
                    else{
                        entry.value
                    }
                }.toMutableMap()
                viewModel.setNotificationByApps(newData)
            }
        }

        chartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                view?.let {
                    val index = (it as TextView).text.toString().toInt()
                    drawChart(currentSelectedNotifications, index - 1)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (currentSelectedNotifications.size > 0) {
                    drawChart(currentSelectedNotifications, 0)
                }
                else {
                    lineChart.clear()
                    lineChart.notifyDataSetChanged()
                    lineChart.invalidate()
                }
            }

        }

    }

    private fun printShortcuts(){
        val shortcutIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
        val shortcuts = activity!!.packageManager.queryIntentActivities(shortcutIntent, 0)
        shortcuts.forEach{
            Log.d("Pager", "name = ${it.activityInfo.name}, package name = ${it.activityInfo.packageName}")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.control_panel_fragment, container, false)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //해당 packageName으로부터 정보 받아와야 함
        view?.let{

            val packageName = (it as TextView).text
            viewModel.getNotificationByApps().let{
                    livedata -> livedata.value?.let{
                    data ->
                data[packageName]!!.notificationData.let{
                    notifications ->

                    currentSelectedNotifications = notifications
                    if(notifications.size == 0){
                        //LineChart Spinner Logic
                        chartAdapter.clear()
                        chartAdapter.notifyDataSetChanged()
                        chartSpinner.visibility = View.INVISIBLE

                        statusView.text = "${(it).text}\n" +
                                "Number of notifications: ${notifications.size}\n"+
                                "Position: ${data[packageName]!!.positionInScreen}"
                        lineChart.clear()
                        lineChart.notifyDataSetChanged()
                        lineChart.invalidate()
                    }
                    else{
                        statusView.text = "${(it).text}\n" +
                                "Number of notifications: ${notifications.size}\n" +
                                "Position: ${data[packageName]!!.positionInScreen}"+
                                "Before Interaction: ${notifications[0].firstPattern}, After Interaction: ${notifications[0].secondPattern}\n" +
                                "Current State: ${notifications[0].lifeCycle}\n" +
                                "Current Enhancement: ${notifications[0].currEnhancement}"

                        // LineChart Spinner Logic
                        chartAdapter.clear()
                        for (i in 1 .. notifications.size) {
                            chartAdapter.add(i.toString())
                        }
                        chartAdapter.notifyDataSetChanged()
                        chartSpinner.visibility = View.VISIBLE

                        // LineChart Logic
                        drawChart(notifications, 0)
                    }
                }
                }
            }
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun drawChart(notifications: MutableList<NotificationEnhancedData>, index: Int) {
        val dataArray = if (notifications.get(0).lifeCycle >= EnhancedNotificationLifeCycle.STATE_3)
            chartDataInteractionHelper(notifications[index]) else chartDataHelper(notifications[index])

        val dataSet = LineDataSet(dataArray, "Model").apply {
            setDrawFilled(true)
            isHighlightEnabled = true
            highLightColor = Color.BLACK
            setDrawHighlightIndicators(true)
        }

        lineChart.run {
            clear()
            invalidate()
            data = LineData(dataSet)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = MyXAxisValueFormatter()
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisLeft.setLabelCount(dataArray.size, true)
            axisRight.isEnabled = false
            description = null
            highlightValue(Highlight(dataSet.getEntryForIndex(dataArray.size - 1).x,
                dataSet.getEntryForIndex(dataArray.size - 1).y, 3))
            notifyDataSetChanged()
        }
    }

    private fun chartDataHelper(enhancedData: NotificationEnhancedData): ArrayList<Entry> {

        val entries = ArrayList<Entry>()

        entries.run{
            // Initial Offset
            add(Entry(enhancedData.initTime.toFloat(), enhancedData.enhanceOffset.toFloat()))

            // Before Interaction
            val firstPeriod = enhancedData.initTime + enhancedData.firstSaturationTime
            when (enhancedData.firstPattern) {
                EnhancementPattern.EQ -> {
                    add(Entry(firstPeriod.toFloat(), enhancedData.enhanceOffset.toFloat()))
                }
                EnhancementPattern.INC -> {
                    add(Entry(firstPeriod.toFloat(), enhancedData.upperBound.toFloat()))
                }
                EnhancementPattern.DEC -> {
                    add(Entry(firstPeriod.toFloat(), enhancedData.lowerBound.toFloat()))
                }
            }

            // After Interaction
            if (enhancedData.interactionTime > 0) {
                val secondPeriod = enhancedData.initTime + enhancedData.firstSaturationTime + enhancedData.secondSaturationTime
                when (enhancedData.secondPattern) {
                    EnhancementPattern.EQ -> {
                        add(Entry(secondPeriod.toFloat(), enhancedData.enhanceOffset.toFloat()))
                    }
                    EnhancementPattern.INC -> {
                        add(Entry(secondPeriod.toFloat(), enhancedData.upperBound.toFloat()))
                    }
                    EnhancementPattern.DEC -> {
                        add(Entry(secondPeriod.toFloat(), enhancedData.lowerBound.toFloat()))
                    }
                }
            }

            // After Decay
            if (enhancedData.timeElapsed >= enhancedData.naturalDecay) {
                val decayPeriod = enhancedData.initTime + enhancedData.naturalDecay
                entries.add(Entry(decayPeriod.toFloat(), enhancedData.lowerBound.toFloat()))
            }

            // Current Enhancement
            add(Entry(System.currentTimeMillis().toFloat(), enhancedData.currEnhancement.toFloat()))
        }
        Collections.sort(entries, EntryXComparator())

        return entries
    }

    private fun chartDataInteractionHelper(enhancedData: NotificationEnhancedData): ArrayList<Entry> {

        if (enhancedData.timeElapsed >= enhancedData.naturalDecay) {
            return chartDataHelper(enhancedData)
        }

        val entries = ArrayList<Entry>()

        entries.run{
            // initial offset
            add(Entry(enhancedData.initTime.toFloat(), enhancedData.enhanceOffset.toFloat()))

            // until interaction: first pattern
            add(Entry(enhancedData.interactionTime.toFloat(), enhancedData.interactionEnhancement.toFloat()))

            // after interaction: second pattern
            val secondPeriod = enhancedData.interactionTime + enhancedData.secondSaturationTime
            add(Entry(secondPeriod.toFloat(), enhancedData.lowerBound.toFloat()))

            // Current Enhancement
            add(Entry(System.currentTimeMillis().toFloat(), enhancedData.currEnhancement.toFloat()))

        }
        Collections.sort(entries, EntryXComparator())

        return entries
    }

    private fun initializeEnhancedAppNotiMap(ids: IntArray, packageNames: Array<String>, postTimes: LongArray):MutableMap<String,AppNotificationsEnhancedData>{

        val dataMap: MutableMap<String, AppNotificationsEnhancedData>? = viewModel.getNotificationByApps().value

        if(dataMap != null){
            val distinctPackageNames = packageNames.distinct()
            distinctPackageNames.forEach{
                    distinctPN ->
                val list = mutableListOf<NotificationEnhancedData>()
                packageNames.forEachIndexed{
                        index, str ->
                    if(distinctPN == str) list.add(
                        NotificationRandomGenerator.newRandomNotification(ids[index], postTimes[index],
                            EnhancedHomeScreenFragment.DEFAULT_START_DECAY_AFTER
                        )
                    )
                }

                dataMap[distinctPN]?.let{
                        currData -> currData.notificationData = list
                }
            }
            return dataMap
        }
        else{
            return packageNames.distinct().map{
                    distinctStr ->
                val list = mutableListOf<NotificationEnhancedData>()
                packageNames.forEachIndexed{
                        index, str ->
                    if(distinctStr == str) list.add(
                        NotificationRandomGenerator.newRandomNotification(ids[index], postTimes[index],
                            EnhancedHomeScreenFragment.DEFAULT_START_DECAY_AFTER
                        )
                    )
                }
                distinctStr to list
            }.toMap().let{
                it.map{
                        entry -> entry.key to AppNotificationsEnhancedData(entry.key, 0).also{
                        datum -> datum.notificationData = entry.value }
                }
            }.toMap().toMutableMap()
        }
    }

    private fun addNewEnhancedNotification(id: Int, packageName: String, postTime: Long): MutableMap<String, AppNotificationsEnhancedData>{
        var currentData: MutableMap<String,AppNotificationsEnhancedData>? = viewModel.getNotificationByApps().value
        if(currentData != null){
            if(packageName in currentData.keys){
                currentData[packageName]!!.notificationData.add(
                    NotificationRandomGenerator.newRandomNotification(id, postTime, EnhancedHomeScreenFragment.DEFAULT_START_DECAY_AFTER)
                )
            }
            else{
                currentData[packageName] = AppNotificationsEnhancedData(packageName, currScreenNumber).also{
                    it.notificationData = mutableListOf(
                        NotificationRandomGenerator.newRandomNotification(id, postTime, EnhancedHomeScreenFragment.DEFAULT_START_DECAY_AFTER)
                    )
                }
            }
        }
        else{
            currentData = mutableMapOf(
                packageName to AppNotificationsEnhancedData(packageName, currScreenNumber).also{
                    it.notificationData = mutableListOf(
                        NotificationRandomGenerator.newRandomNotification(id, postTime, EnhancedHomeScreenFragment.DEFAULT_START_DECAY_AFTER)
                    )
                }
            )
        }
        return currentData
    }

    private fun dismissNotification(id: Int, packageName: String, postTime: Long):MutableMap<String, AppNotificationsEnhancedData>{
        val currentData: MutableMap<String,AppNotificationsEnhancedData>? = viewModel.getNotificationByApps().value

        return currentData!!.mapValues{
            if(packageName == it.key)
                it.value.apply{
                    notificationData = notificationData.map{
                            datum -> datum.apply{
                        lifeCycle = EnhancedNotificationLifeCycle.STATE_3
                        interactionTime = System.currentTimeMillis()
                        interactionEnhancement = this.currEnhancement
                        }
                    }.toMutableList()
                }
            else
                it.value
        }.toMutableMap()
    }

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