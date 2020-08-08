package thevoid.whichbinds.dslist

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

interface DragTo {
    var recyclerView: RecyclerView
    var callBack: (viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) -> Unit
}

class DragToImpl(
    override var recyclerView: RecyclerView,
    override var callBack: (viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) -> Unit
) : DragTo, ItemTouchHelper.Callback() {

    init {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // Set movement flags based on the layout manager
        return if (recyclerView.layoutManager is GridLayoutManager) {
            val dragFlags =
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = 0
            makeMovementFlags(
                dragFlags,
                swipeFlags
            )
        } else {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            makeMovementFlags(dragFlags, 0)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        callBack(viewHolder, target)
        return true
    }

    override fun isLongPressDragEnabled(): Boolean = true

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {}

}

fun dragTo(
    recyclerView: RecyclerView,
    callBack: (viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) -> Unit
) = DragToImpl(recyclerView, callBack)
