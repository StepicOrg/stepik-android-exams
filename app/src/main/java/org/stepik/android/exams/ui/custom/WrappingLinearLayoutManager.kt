package org.stepik.android.exams.ui.custom

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

class WrappingLinearLayoutManager
@JvmOverloads
constructor(context: Context, orientation: Int = VERTICAL, reverseLayout: Boolean) : LinearLayoutManager(context, orientation, reverseLayout) {
    private val dimensions = intArrayOf(0, 0)

    init {
        isAutoMeasureEnabled = false
    }

    override fun onMeasure(recycler: RecyclerView.Recycler, state: RecyclerView.State, widthSpec: Int, heightSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthSpec)
        val heightMode = View.MeasureSpec.getMode(heightSpec)
        val widthSize = View.MeasureSpec.getSize(widthSpec)
        val heightSize = View.MeasureSpec.getSize(heightSpec)

        var width = 0
        var height = 0

        val verticalPadding = paddingTop + paddingBottom
        val horizontalPadding = paddingLeft + paddingEnd

        for(i in 0 until itemCount) {
            if (orientation == HORIZONTAL) {
                measureChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), heightSpec, dimensions)

                width += dimensions[0]
                height = maxOf(height, dimensions[1])
            } else {
                measureChild(recycler, i, widthSpec, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), dimensions)

                width = maxOf(width, dimensions[0])
                height += dimensions[1]
            }
        }

        width += horizontalPadding
        height += verticalPadding

        if (widthMode == View.MeasureSpec.EXACTLY) {
            width = widthSize
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize
        }

        setMeasuredDimension(width, height)
    }

    private fun measureChild(recycler: RecyclerView.Recycler, position: Int, widthSpec: Int, heightSpec: Int, dimensions: IntArray) {
        val view = recycler.getViewForPosition(position) ?: return
        recycler.bindViewToPosition(view, position)

        val params = view.layoutParams as RecyclerView.LayoutParams
        val childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, 0, params.width)
        val childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, 0, params.height)
        view.measure(childWidthSpec, childHeightSpec)

        dimensions[0] = view.measuredWidth + params.leftMargin + params.rightMargin
        dimensions[1] = view.measuredHeight + params.bottomMargin + params.topMargin
        recycler.recycleView(view)
    }
}