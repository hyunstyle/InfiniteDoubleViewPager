package com.hyunstyle.ifdviewpager

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.hyunstyle.ifdviewpager.adapter.InfinitePagerAdapter
import com.hyunstyle.ifdviewpager.adapter.DefaultFragmentPagerAdapter
import com.hyunstyle.ifdviewpager.fragment.HomeFragment
import com.hyunstyle.ifdviewpager.fragment.DashboardFragment
import com.hyunstyle.ifdviewpager.fragment.NotificationFragment
import com.hyunstyle.ifdviewpager.fragment.SettingsFragment
import com.hyunstyle.ifdviewpager.util.InfiniteDoubleViewPager

class MainActivity : AppCompatActivity() {

    private lateinit var navView: BottomNavigationView
    private lateinit var viewPager: InfiniteDoubleViewPager
    private var menuSize: Int = 0
    private var isNavViewClicked = false

    private enum class BottomNav(val position: Int, val menuId: Int) {
        HOME(0, R.id.navigation_home),
        DASHBOARD(1, R.id.navigation_dashboard),
        NOTIFICATION(2, R.id.navigation_notifications),
        SETTINGS(3, R.id.navigation_settings);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        menuSize = navView.menu.size()

        initViewPager()
        viewPager.startAutoScroll()
    }

    private fun initViewPager() {
        viewPager = findViewById(R.id.view_pager)
        val adapter = DefaultFragmentPagerAdapter(supportFragmentManager)
        createMainFragments(adapter)

        viewPager.adapter = InfinitePagerAdapter(adapter)
        viewPager.addOnPageChangeListener(onPageChangedListener)
    }

    private fun createMainFragments(adapter: DefaultFragmentPagerAdapter) {
        for (i in 1..2) {
            adapter.addFragment(HomeFragment())
            adapter.addFragment(DashboardFragment())
            adapter.addFragment(NotificationFragment())
            adapter.addFragment(SettingsFragment())
        }
    }

    @Synchronized
    private fun navigateToPosition(position: Int) {
        val currentItemPos =
            if(viewPager.realCount != 0) viewPager.currentItem % viewPager.realCount
            else viewPager.currentItem

        when {
            (currentItemPos == position) -> return
            (currentItemPos > position) -> {
                viewPager.currentItem += (viewPager.realCount - (currentItemPos - position))
                return
            }
            (currentItemPos < position) -> {
                viewPager.currentItem += (position - currentItemPos)
                return
            }
        }

    }

    private fun activateNavViewByPosition(position: Int) {
        navView.selectedItemId = BottomNav.values()[position].menuId
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if(isNavViewClicked) {
            isNavViewClicked = false
            return@OnNavigationItemSelectedListener true
        }

        isNavViewClicked = true
        when (item.itemId) {
            BottomNav.HOME.menuId -> {
                navigateToPosition(BottomNav.HOME.position)
                return@OnNavigationItemSelectedListener true
            }
            BottomNav.DASHBOARD.menuId -> {
                navigateToPosition(BottomNav.DASHBOARD.position)
                return@OnNavigationItemSelectedListener true
            }
            BottomNav.NOTIFICATION.menuId -> {
                navigateToPosition(BottomNav.NOTIFICATION.position)
                return@OnNavigationItemSelectedListener true
            }
            BottomNav.SETTINGS.menuId -> {
                navigateToPosition(BottomNav.SETTINGS.position)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val onPageChangedListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            activateNavViewByPosition(position % viewPager.realCount)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}
