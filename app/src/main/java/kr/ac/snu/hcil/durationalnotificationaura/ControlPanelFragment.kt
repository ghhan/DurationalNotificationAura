package kr.ac.snu.hcil.durationalnotificationaura

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.control_panel_fragment.*
import kr.ac.snu.hcil.durationalnotificationaura.data.AppNotificationsEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.data.EnhancedNotificationLifeCycle
import kr.ac.snu.hcil.durationalnotificationaura.data.NotificationEnhancedData
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenFragment
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenViewModel
import kr.ac.snu.hcil.durationalnotificationaura.utils.MyNotificationListenerService
import kr.ac.snu.hcil.durationalnotificationaura.utils.NotificationRandomGenerator

class ControlPanelFragment: Fragment(), AdapterView.OnItemSelectedListener {


    private lateinit var viewModel: EnhancedHomeScreenViewModel
    private lateinit var packagesInPage: MutableMap<Int, List<String>>
    private lateinit var packageNameAdapter: ArrayAdapter<String>
    var screenNumber = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        packageNameAdapter = ArrayAdapter(context!!, R.layout.simple_spinner_dropdown_item)
        packageNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        packageNameSpinner.onItemSelectedListener = this

        activity?.startService(Intent(activity, MyNotificationListenerService::class.java))

        viewModel = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application))
            .get(EnhancedHomeScreenViewModel::class.java)
        viewModel.getNotificationByApps().observe(this,
            Observer{
                dataMap ->
                packageNameAdapter.clear()
                dataMap?.filter{
                    it.value.screenNumber == this.screenNumber
                }?.forEach{
                    filteredEntry -> packageNameAdapter.add(filteredEntry.key)
                }
                packageNameAdapter.addAll(*(packagesInPage[0]!!.toTypedArray()))
                packageNameSpinner.adapter = packageNameAdapter
            }
        )

        viewModel.getCurrentScreenNumber().observe(this,
            Observer{
                    data -> data?.let{
                    screenNum ->
                    packageNameAdapter.clear()
                    this.screenNumber = screenNum
                    viewModel.getNotificationByApps().value?.filter{
                        it.value.screenNumber == screenNum
                    }?.forEach{
                            filteredEntry -> packageNameAdapter.add(filteredEntry.key)
                    }
                }
                packageNameAdapter.addAll(*(packagesInPage[0]!!.toTypedArray()))
                packageNameSpinner.adapter = packageNameAdapter
            }
        )
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
                val notifications = data[packageName]!!.notificationData
                statusView.text = "${(it).text}\n" +
                        "Number of notifications: ${notifications.size}\n" +
                        "Before Interaction: ${notifications[0].firstPattern}, After Interaction: ${notifications[0].secondPattern}\n" +
                        "Current State: ${notifications[0].lifeCycle}\n" +
                        "Current Enhancement: ${notifications[0].currEnhancement}"
            }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initializeEnhancedAppNotiMap(ids: IntArray, packageNames: Array<String>, postTimes: LongArray) =
        packageNames.distinct().map{
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
                    datum -> datum.notificationData = entry.value
            }
            }.toMap()
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
                currentData[packageName] = AppNotificationsEnhancedData(packageName, screenNumber).also{
                    it.notificationData = mutableListOf(
                        NotificationRandomGenerator.newRandomNotification(id, postTime, EnhancedHomeScreenFragment.DEFAULT_START_DECAY_AFTER)
                    )
                }
            }
        }
        else{
            currentData = mutableMapOf(
                packageName to AppNotificationsEnhancedData(packageName, screenNumber).also{
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
                            datum -> datum.apply{ lifeCycle = EnhancedNotificationLifeCycle.STATE_3 }
                    }.toMutableList()
                }
            else
                it.value
        }.toMutableMap()
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