package com.example.a14_recyclerviewdragandswipekotlin.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

data class SpacesItemDecoration(var mSpace: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = mSpace
        outRect.right = mSpace
        outRect.bottom = mSpace
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mSpace
        }
    }

}