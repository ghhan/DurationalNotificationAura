package kr.ac.snu.hcil.durationalnotificationaura

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenViewModel
import kr.ac.snu.hcil.visualeffecttest.UnityPlayerActivity

class UnityEffectActivity : AppCompatActivity() {

    private lateinit var viewModel: EnhancedHomeScreenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unity_effect)
        viewModel = ViewModelProviders.of(this).get(EnhancedHomeScreenViewModel::class.java)

        val intent = Intent(this, UnityPlayerActivity::class.java)
        startActivity(intent)
    }
}
