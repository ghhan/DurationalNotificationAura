package kr.ac.snu.hcil.durationalnotificationaura

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.control_panel_fragment.*

class ControlPanelFragment: Fragment(), AdapterView.OnItemSelectedListener {


    private lateinit var packagesInPage: MutableMap<Int, List<String>>
    private lateinit var packageNameAdapter: ArrayAdapter<String>

    init{

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        packageNameAdapter = ArrayAdapter(context!!, R.layout.simple_spinner_dropdown_item)
        packageNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        //TODO: Need to be Initialized with corresponding applications
        packagesInPage = mutableMapOf(
            0 to listOf("packageNames blahblabh"),
            1 to listOf("packageNames blahblah"),
            2 to listOf("packageNames blahblabh"),
            3 to listOf("packageNames blahblah"),
            4 to listOf("packageNames blahblah")
        )

        packageNameAdapter.addAll(*(packagesInPage[0]!!.toTypedArray()))

        //해당 페이지에서 packageName들 쭉 받아와야 함

        packageNameSpinner.adapter = packageNameAdapter

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.control_panel_fragment, container, false)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //해당 packageName으로부터 정보 받아와야 함

        /*
        view?.let{
            val packageName = (it as TextView).text
            viewModel.getNotificationsByApps().let{
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
        */
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}