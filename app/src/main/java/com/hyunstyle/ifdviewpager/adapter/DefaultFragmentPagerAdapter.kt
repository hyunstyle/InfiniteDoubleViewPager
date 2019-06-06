package com.hyunstyle.ifdviewpager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class DefaultFragmentPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragmentList = ArrayList<Fragment>()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment) = fragmentList.add(fragment)
}