package kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.enhanced_home_screen_fragment.*
import kr.ac.snu.hcil.durationalnotificationaura.R
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AnimationParams
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.AnimationTypes
import kr.ac.snu.hcil.durationalnotificationaura.visualEffects.DerivedVisEffect

class EnhancedHomeScreenFragment : Fragment() {

    companion object {
        fun newInstance() = EnhancedHomeScreenFragment()
        const val DEFAULT_START_DECAY_AFTER = 1000L * 60 * 10
        const val TAG = "TESTING_FRAGMENT"
    }

    private lateinit var viewModel: EnhancedHomeScreenViewModel
    private lateinit var packageNameAdapter: ArrayAdapter<String>
    private var screenNumber = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.enhanced_home_screen_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenNumber = arguments!!.getInt("screenNumber", -1)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        gridLayout.let{
            it.clipChildren = false
            //it.clipToPadding = false
            //it.clipToOutline = false
        }

        viewModel = activity?.run{
            ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(application))
                .get(EnhancedHomeScreenViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        viewModel.getEnhancementDataInCurrentScreen(screenNumber).observe(this,
            Observer {
                it?.let{
                    appNotiData ->
                    appNotiData.map{
                        entry ->
                        val packageName = entry.key
                        val data = entry.value
                        val targetView = findViewWithPackageName(gridLayout, packageName)
                        if(targetView != null) {
                            (targetView as EnhancedAppAuraView).let{
                                view ->
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
                        }
                        else {
                            (gridLayout.getChildAt(data.positionInScreen.first + data.positionInScreen.second * 4) as EnhancedAppAuraView).let{
                                view ->
                                view.tag = packageName
                                view.background = viewModel.drawableMap[packageName]
                                view.setEnhanceData(data)
                                view.setVisualEffects(List(data.notificationData.size) {index ->
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
                        }
                    }
                }

            }
        )

    }

    private fun findViewWithPackageName(parent:ViewGroup, packageName: String): View?{
        val currentSize = parent.childCount
        for(index:Int in 0..(currentSize - 1)){
            val child = parent.getChildAt(index)
            if(child.tag == packageName)
                return child
        }
        return null
    }
}
