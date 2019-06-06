package com.hyunstyle.ifdviewpager.util

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hyunstyle.ifdviewpager.adapter.InfinitePagerAdapter

import java.lang.ref.WeakReference

class InfiniteDoubleViewPager : ViewPager {

    var interval = DEFAULT_INTERVAL.toLong()

    /** auto scroll direction, default is [.RIGHT]  */
    private var direction = RIGHT

    /** whether automatic cycle when auto scroll reaching the last or first item, default is true  */
    private val isCycle = true

    /** whether stop auto scroll when touching, default is true  */
    private val stopScrollWhenTouch = true

    /** scroll factor for auto scroll animation, default is 1.0  */
    private val autoScrollFactor = 1.0

    /** scroll factor for swipe scroll animation, default is 1.0  */
    private val swipeScrollFactor = 1.0

    private var handler: ScrollHandler? = null
    private var isAutoScroll = false
    private var scroller: DurationScroller? = null
    private var isPagingEnabled = true
    private val virtualCurrentItem: Int
        get() = super.getCurrentItem()

    var realCount = 0

    constructor(paramContext: Context) : super(paramContext) {
        setViewPagerScroller()
    }

    constructor(paramContext: Context, paramAttributeSet: AttributeSet) : super(paramContext, paramAttributeSet) {
        setViewPagerScroller()
    }

    /**
     * start auto scroll, first scroll delay time is [.getInterval]
     */
    @Synchronized
    fun startAutoScroll() {
        if (handler == null) {
            handler = ScrollHandler(WeakReference(this))
        }
        isAutoScroll = true

        if (isPagingEnabled) {
            sendScrollMessage((interval + scroller!!.duration / autoScrollFactor * swipeScrollFactor).toLong())
        }
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)

        if (visibility == View.GONE) {
            stopAutoScroll()
        } else {
            if (isAutoScroll) {
                startAutoScroll()
            }
        }
    }

    /**
     * start auto scroll
     *
     * @param delayTimeInMills first scroll delay time
     */
    fun startAutoScroll(delayTimeInMills: Int) {

        if (handler == null) {
            handler = ScrollHandler(WeakReference(this))
        }

        if (isPagingEnabled) {
            sendScrollMessage(delayTimeInMills.toLong())
        }
    }

    /**
     * stop auto scroll
     */
    fun stopAutoScroll() {
        if (handler != null) {
            handler!!.removeMessages(SCROLL_WHAT)
            handler = null
        }
    }

    override fun setCurrentItem(item: Int) {
        setCurrentItem(item, false)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {

        val adapter = adapter ?: return

        val pos = item % adapter.count

        Log.d("item?", "" + item)
        Log.d("what", "" + pos)

        super.setCurrentItem(pos, smoothScroll)
    }

    override fun getCurrentItem(): Int {
        //Log.d("current", "" + super.getCurrentItem() % realCount)
        //        if(getAdapter() instanceof InfiniteDoubleViewPager) {
        //            return super.getCurrentItem() % realCount;
        //        }

        return super.getCurrentItem()
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        if(adapter is InfinitePagerAdapter) {
            realCount = (adapter.realCount / 2)
            if(realCount > 1) {
                setCurrentItem(realCount * 1000, false)
            }
        }
    }

    private fun sendScrollMessage(delayTimeInMills: Long) {
        if (handler != null) {
            handler!!.removeMessages(SCROLL_WHAT)
            handler!!.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills)
        }
    }

    /**
     * set ViewPager scroller to change animation duration when sliding
     */
    private fun setViewPagerScroller() {
        try {
            val scrollerField = ViewPager::class.java.getDeclaredField("mScroller")
            scrollerField.isAccessible = true
            val interpolatorField = ViewPager::class.java.getDeclaredField("sInterpolator")
            interpolatorField.isAccessible = true

            scroller = DurationScroller(context, interpolatorField.get(null) as Interpolator)
            scrollerField.set(this, scroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * scroll only once
     */
    fun scrollOnce() {
        var currentItem = virtualCurrentItem
        val totalCount = adapter?.count ?: return
        val nextItem = if (direction == LEFT) --currentItem else ++currentItem

        if (nextItem < 0) {
            if (isCycle) {
                setCurrentItem(totalCount - 1, false)
            }
        } else if (nextItem == totalCount) {
            if (isCycle) {
                setCurrentItem(0, false)
            }
        } else {
            setCurrentItem(nextItem, true)
        }
    }

    /**
     *
     * if stopScrollWhenTouch is true
     *  * if event is down, stop auto scroll.
     *  * if event is up, start auto scroll again.
     *
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        /**
         * deprecated function modified by SangHyeon 2018-07-06
         */
        val action = ev.actionMasked

        if (stopScrollWhenTouch) {
            if (action == MotionEvent.ACTION_DOWN && isAutoScroll) {
                isInTouchState = true
            } else if (ev.action == MotionEvent.ACTION_UP) {
                isInTouchState = false
                sendScrollMessage((interval + scroller!!.duration / autoScrollFactor * swipeScrollFactor).toLong())
            }
        }

        parent.requestDisallowInterceptTouchEvent(true)

        return super.dispatchTouchEvent(ev)
    }

    private class ScrollHandler(private val infiniteDoubleViewPager: WeakReference<InfiniteDoubleViewPager>) : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                SCROLL_WHAT -> {

                    if (!InfiniteDoubleViewPager.isInTouchState) {
                        val vp = infiniteDoubleViewPager.get()
                        if(vp != null) {
                            vp.scroller!!.setScrollDurationFactor(vp.autoScrollFactor)
                            vp.scrollOnce()
                            vp.scroller!!.setScrollDurationFactor(vp.swipeScrollFactor)
                            vp.sendScrollMessage(vp.interval + vp.scroller!!.duration)
                        }
                    }
                }
            }
        }
    }

    /**
     * get auto scroll direction
     *
     * @return [.LEFT] or [.RIGHT], default is [.RIGHT]
     */
    fun getDirection(): Int {
        return if (direction == LEFT) LEFT else RIGHT
    }

    /**
     * set auto scroll direction
     *
     * @param direction [.LEFT] or [.RIGHT], default is [.RIGHT]
     */
    fun setDirection(direction: Int) {
        this.direction = direction
    }

    fun setPagingEnabled(isEnabled: Boolean) {
        this.isPagingEnabled = isEnabled
    }

    companion object {

        const val DEFAULT_INTERVAL = 4000
        const val LEFT = 0
        const val RIGHT = 1
        protected var isInTouchState = false

        const val SCROLL_WHAT = 0
    }
}
