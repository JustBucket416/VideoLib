package justbucket.videolib.adapter

import android.support.v7.widget.RecyclerView

/**
 * A not so simple [RecyclerView.LayoutManager] subclass which is used for portrait orientation
 */
class PortraitLayoutManager : RecyclerView.LayoutManager() {

    override fun generateDefaultLayoutParams() = RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State?) {
        if (itemCount > 0) {
            removeAndRecycleAllViews(recycler)
            val view = recycler.getViewForPosition(0)
            //view.layoutParams.width = view.layoutParams.width/2
            addView(view)
            measureChildWithMargins(view, 4, 4)
            val decoratedWidth = getDecoratedMeasuredWidth(view)
            val decoratedHeight = getDecoratedMeasuredHeight(view)
            detachAndScrapView(view, recycler)
            val count = height / view.measuredHeight
            var lastX = 0
            var lastY = 0
            for (i in 1 until itemCount + 1) {
                val child = recycler.getViewForPosition(i - 1)
                addView(child)
                measureChildWithMargins(child, 4, 4)
                layoutDecoratedWithMargins(child, lastX,
                        lastY,
                        lastX + decoratedWidth,
                        lastY + decoratedHeight)
                lastX = (i / count) * decoratedWidth
                lastY = (i % count) * decoratedHeight
            }
        }
    }

    override fun canScrollHorizontally() = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        val delta = getDelta(dx)
        offsetChildrenHorizontal(-delta)
        return delta
    }

    private fun getDelta(dx: Int): Int {
        if (childCount == 0) {
            return 0
        }

        val firstView = getChildAt(0) ?: throw NullPointerException("Recycler is acting weird")
        val lastView = getChildAt(childCount - 1)
                ?: throw NullPointerException("Recycler is acting weird")

        val viewSpan = getDecoratedRight(lastView) - getDecoratedLeft(firstView)
        if (viewSpan <= width) {
            return 0
        }

        var delta = 0

        if (dx < 0) {
            val firstViewAdapterPos = getPosition(firstView)
            delta = if (firstViewAdapterPos > 0) {
                dx
            } else {
                val viewTop = getDecoratedLeft(firstView)
                Math.max(viewTop, dx)
            }
        } else if (dx > 0) {
            val lastViewAdapterPos = getPosition(lastView)
            delta = if (lastViewAdapterPos < itemCount - 1) {
                dx
            } else {
                val viewBottom = getDecoratedRight(lastView)
                Math.min(viewBottom - width, dx)
            }
        }
        return delta
    }
}