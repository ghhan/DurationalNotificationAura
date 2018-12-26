package kr.ac.snu.hcil.durationalnotificationaura

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import kr.ac.snu.hcil.durationalnotificationaura.ui.enhancedhomescreen.EnhancedHomeScreenFragment

class ScreenSlidePagerActivity : FragmentActivity() {

    companion object {
        const val NUM_PAGES = 5
    }

    private lateinit var mPager: ViewPager
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm){
        private  var fragments: MutableList<Fragment> = mutableListOf()
        init{
            //return fragments with specified Applications
        }

        override fun getCount(): Int = NUM_PAGES
        override fun getItem(position: Int): Fragment{
            return fragments[position]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_slide)

        mPager = findViewById(R.id.myPager)

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