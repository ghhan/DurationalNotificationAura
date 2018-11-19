package kr.ac.snu.hcil.durationalnotificationaura

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenFragment

class EnhancedHomeScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enhanced_home_screen_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, EnhancedHomeScreenFragment.newInstance())
                .commitNow()
        }

    }
}
