package net.euse.calendar.adapters

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.SparseArray
import net.euse.calendar.fragments.EventListFragment
import net.euse.calendar.helpers.DAY_CODE
import net.euse.calendar.interfaces.NavigationListener

class MyEventListPagerAdapter(fm: FragmentManager, private val mCodes: List<String>, private val mListener: NavigationListener) :
        FragmentStatePagerAdapter(fm) {
    private val mFragments = SparseArray<EventListFragment>()

    override fun getCount() = mCodes.size

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        val code = mCodes[position]
        bundle.putString(DAY_CODE, code)

        val fragment = EventListFragment()
        fragment.arguments = bundle

        mFragments.put(position, fragment)

        return fragment
    }

    fun updateMonthlyEventLists(pos: Int) {
        for (i in -1..1) {
            mFragments[pos + i]?.updateCalendar()
        }
    }
}
