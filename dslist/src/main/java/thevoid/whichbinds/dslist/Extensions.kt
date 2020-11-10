package thevoid.whichbinds.dslist

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.reachesTopScrolling(newState: Int) : Boolean =
    RecyclerView.SCROLL_STATE_IDLE == newState && !canScrollVertically(-1)

fun RecyclerView.reachesBottomScrolling(newState: Int): Boolean =
    RecyclerView.SCROLL_STATE_IDLE == newState && !canScrollVertically(1)

