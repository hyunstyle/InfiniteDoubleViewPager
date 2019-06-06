package com.hyunstyle.ifdviewpager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hyunstyle.ifdviewpager.R
import com.hyunstyle.ifdviewpager.adapter.DefaultFragmentPagerAdapter
import com.hyunstyle.ifdviewpager.fragment.child.DashboardChild2Fragment
import com.hyunstyle.ifdviewpager.fragment.child.DashboardChildFragment

class DashboardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        initView(view)
        return view
    }

    private fun initView(view: View) {
        val tabLayout: TabLayout = view.findViewById(R.id.dashboard_tab_layout)
        val viewPager: ViewPager = view.findViewById(R.id.dashboard_view_pager)
        val adapter = DefaultFragmentPagerAdapter(childFragmentManager)

        adapter.addFragment(DashboardChildFragment())
        adapter.addFragment(DashboardChild2Fragment())

        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = adapter


    }
}