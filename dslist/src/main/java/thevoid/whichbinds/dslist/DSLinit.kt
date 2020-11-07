package thevoid.whichbinds.dslist

import androidx.recyclerview.widget.RecyclerView

data class Init (
    val recyclerView: RecyclerView?,
    val cacheName: String?,
    val shimmerViewId: Int?,
    val shimmersToAdd: Int
)
class InitBuilder {
    var recyclerView: RecyclerView? = null
    var cacheName: String? = null
    var shimmerViewId: Int? = null
    var shimmersToAdd: Int = 3

    fun build(): Init = Init(recyclerView, cacheName, shimmerViewId, shimmersToAdd)
}
