package thevoid.whichbinds.dslist

import androidx.recyclerview.widget.RecyclerView

data class Init (
    val recyclerView: RecyclerView?,
    val cacheName: String?
)
class InitBuilder {
    var recyclerView: RecyclerView? = null
    var cacheName: String? = null

    fun build(): Init = Init(recyclerView, cacheName)
}
