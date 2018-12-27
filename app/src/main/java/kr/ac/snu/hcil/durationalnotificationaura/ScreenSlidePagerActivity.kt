package kr.ac.snu.hcil.durationalnotificationaura

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenFragment
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenViewModel

class ScreenSlidePagerActivity : FragmentActivity() {

    companion object {
        const val NUM_PAGES = 5
    }

    private lateinit var mPager: ViewPager
    private lateinit var viewModel: EnhancedHomeScreenViewModel
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm){
        private  var fragments: MutableList<Fragment> = mutableListOf()
        init{
            //return fragments with specified Applications
            for(position:Int in 0..(NUM_PAGES - 1)){
                fragments.add (EnhancedHomeScreenFragment().also{
                    it.arguments = Bundle().apply{
                        putInt("screenNumber", position)
                    }
                })
            }
        }

        override fun getCount(): Int = NUM_PAGES
        override fun getItem(position: Int): Fragment{
            return fragments[position]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_slide)

        viewModel = ViewModelProviders.of(this, ViewModelProvider.AndroidViewModelFactory(application))
            .get(EnhancedHomeScreenViewModel::class.java)

        mPager = findViewById(R.id.myPager)
        mPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener
            {
                override fun onPageScrollStateChanged(state: Int) {
                    /*
                    SCROLL_STATE_IDLE
                    SCROLL_STATE_DRAGGING
                    SCROLL_STATE_SETTLING
                    */
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    /*
                    position: position index of the first page currently being displayed
                    positionOffset: [0, 1) indicating the offset from the page at position
                    positionOffsetPixels: value in pixels indicating offset from position
                    *
                    * new package name을 보내주고,
                    * */

                }

                override fun onPageSelected(position: Int) {
                    // position index of the new selected page
                    viewModel.setCurrentScreenNumber(position)
                }
            })

        //adapter가 바뀐다 -> 현재 보이는 application 구성이 바뀐다 ->
        mPager.addOnAdapterChangeListener{
            viewPager:ViewPager, pagerAdapter1: PagerAdapter?, pagerAdapter2: PagerAdapter? ->

        }
        
        

        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        mPager.adapter = pagerAdapter
    }

    override fun onBackPressed() {
        if(mPager.currentItem == 0)
            super.onBackPressed()
        else
            mPager.currentItem = mPager.currentItem - 1
    }


}