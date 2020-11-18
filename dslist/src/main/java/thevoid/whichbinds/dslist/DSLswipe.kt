package thevoid.whichbinds.dslist

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import thevoid.whichbinds.dslist.Direction.*
import thevoid.whichbinds.dslist.Gravity.*

class BuilderSwipe {
    var drawLeft: Draw? = null
    var drawRight: Draw? = null

    fun left(drawBuilder: DrawBuilder.() -> Unit) {
        drawLeft = DrawBuilder().apply(drawBuilder).build()
    }

    fun right(drawBuilder: DrawBuilder.() -> Unit) {
        drawRight = DrawBuilder().apply(drawBuilder).build()
    }
}

interface SwipeDLS  {
    val recyclerView: RecyclerView
    val swipedTo: BuilderSwipe.() -> Unit
}

class SwipeTo (
    override val recyclerView: RecyclerView,
    override val swipedTo: BuilderSwipe.() -> Unit
) : SwipeDLS, ItemTouchHelper.Callback() {

    private val drawLeft = BuilderSwipe().apply(swipedTo).drawLeft
    private val drawRight = BuilderSwipe().apply(swipedTo).drawRight

    init {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val left = if(drawLeft !== null) ItemTouchHelper.LEFT else 0
        val right = if(drawRight !== null ) ItemTouchHelper.RIGHT else 0
        return makeMovementFlags(0, left or right)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when(direction) {
            ItemTouchHelper.LEFT -> drawLeft?.action?.let { it(viewHolder) }
            ItemTouchHelper.RIGHT-> drawRight?.action?.let { it(viewHolder) }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val isCanceled = dX == 0f && !isCurrentlyActive
        when {
            dX < 0 -> {
                drawLeft?.apply {
                    backgroundColor?.let {
                        ColorDrawable().drawBackground(itemView, c, dX.toInt(), it, RIGHT)
                    }
                    icon?.drawIcon(itemView, c, END)
                    text?.paintText(itemView, c, END)
                    customDraw?.let { it(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive) }
                }
            }
            dX > 0 -> {
                drawRight?.apply {
                    backgroundColor?.let {
                        ColorDrawable().drawBackground(itemView, c, dX.toInt(), it, LEFT)
                    }
                    icon?.drawIcon(itemView, c, START)
                    text?.paintText(itemView, c, START)
                    customDraw?.let { it(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive) }
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}

fun swipeTo(
    recyclerView: RecyclerView,
    swipedTo: BuilderSwipe.() -> Unit
) = SwipeTo(recyclerView, swipedTo)