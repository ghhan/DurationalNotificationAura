package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.enhanced_home_screen_fragment.*
import kr.ac.snu.hcil.durationalnotificationaura.EnhancedNotificationDataAdapter
import kr.ac.snu.hcil.durationalnotificationaura.utils.MyNotificationListenerService
import kr.ac.snu.hcil.durationalnotificationaura.R

class EnhancedHomeScreenFragment : Fragment() {

    companion object {
        fun newInstance() = EnhancedHomeScreenFragment()
        const val ACTION = "kr.ac.snu.hcil.durationalnotificationaura.NOTIFICATION_LISTENER"
    }

    private lateinit var viewModel: EnhancedHomeScreenViewModel
    private lateinit var enhancedAppNotificationDataAdapter: EnhancedNotificationDataAdapter
    private val notificationReceiver = NotificationReceiver()
    private val intentFilter = IntentFilter().also{
        it.addAction(ACTION)}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.enhanced_home_screen_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        enhancedAppNotificationDataAdapter = EnhancedNotificationDataAdapter(context!!, R.layout.home_screen_gridview_item)

        activity?.startService(Intent(activity, MyNotificationListenerService::class.java))

        viewModel = ViewModelProviders.of(this).get(EnhancedHomeScreenViewModel::class.java)
        viewModel.getNotificationsByApps().observe(this,
            Observer {
                //Render Logic
                enhancedAppNotificationDataAdapter.clear()
                enhancedAppNotificationDataAdapter.addAll(it!!.values)
                appGrid.adapter = enhancedAppNotificationDataAdapter
            }
        )
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
            when(bundle?.getString("event")){
                "Initialized" -> {
                    //TODO:
                    val idArray = bundle.getIntArray("IDs")
                    val pNArray = bundle.getStringArray("packageNames")
                    val postTimes = bundle.getLongArray("postTimes")

                }
                "Posted" -> {
                    //TODO:
                    val id = bundle.getInt("ID")
                    val packageName = bundle.getString("packageName")
                    val postTime = bundle.getLong("postTime")
                }
                "Removed" -> {
                    //TODO:
                    val id = bundle.getInt("ID")
                    val packageName = bundle.getString("packageName")
                    val postTime = bundle.getLong("postTime")
                }
            }

            //TODO: 새로운 MutableMap<String, EnhancedAppNotificationData>로 viewModel update
            viewModel.setNotificationByApps(mutableMapOf())
        }
    }
}
