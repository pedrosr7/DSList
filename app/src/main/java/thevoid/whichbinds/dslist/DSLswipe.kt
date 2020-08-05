package thevoid.whichbinds.dslist

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import thevoid.whichbinds.dslist.Direction.*
import thevoid.whichbinds.dslist.Gravity.*

data class PaintText(val text: String, val textColor: Int = Color.BLACK, val size: Float = 60f, val margin: Float = 0f)

data class Draw(
    var backgroundColor: Int? = null,
    var icon: Drawable? = null,
    var text: PaintText? = null,
    var paint: Paint? = null,
    var swiped: ((viewHolder: RecyclerView.ViewHolder) -> Unit)?,
    var customDraw: ((
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean) -> Unit)?)

class DrawBuilder {
    var backgroundColor: Int? = null
    var icon: Drawable? = null
    var text: PaintText? = null
    var paint: Paint? = null
    var swiped: ((viewHolder: RecyclerView.ViewHolder) -> Unit)? = null
    var customDraw: ((
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean) -> Unit)? = null

    fun build(): Draw = Draw(
        backgroundColor,
        icon, text,
        paint, swiped, customDraw
    )
}

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
            ItemTouchHelper.LEFT -> drawLeft?.swiped?.let { it(viewHolder) }
            ItemTouchHelper.RIGHT-> drawRight?.swiped?.let { it(viewHolder) }
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

fun ColorDrawable.drawBackground(view: View, canvas: Canvas, dX: Int, colorR: Int, direction: Direction) {
    val (left, right) = when (direction) {
        LEFT -> view.left + dX to view.left
        RIGHT -> view.right + dX to view.right
    }
    setBounds(left, view.top, right, view.bottom)
    color = colorR
    draw(canvas)
}

fun Drawable.drawIcon(view: View, canvas: Canvas, gravity: Gravity) {
    val margin = (view.height - intrinsicHeight) / 2
    val top = view.top + (view.height - intrinsicHeight) / 2
    val bottom = top + intrinsicHeight

    val (left, right) = when(gravity) {
        START -> (view.left + margin) to (view.left + margin + intrinsicWidth)
        CENTER -> (view.width / 2) to (view.width / 2) + intrinsicWidth
        END -> (view.right - margin - intrinsicWidth) to (view.right - margin)
    }

    setBounds(left, top, right, bottom)
    draw(canvas)
}

fun PaintText.paintText(view: View, canvas: Canvas, gravity: Gravity) {
    with(Paint()) {
        isAntiAlias = true
        color = textColor
        textSize = size

        val xPos = when(gravity) {
            START -> (margin)
            CENTER -> (view.width / 2).toFloat()
            END -> (view.right - margin - measureText(text))
        }
        val yPos = (view.top.toFloat() + view.height/2 - (descent() + ascent()) / 2)
        canvas.drawText(text, xPos, yPos, this)
    }
}

enum class Gravity {
    START, CENTER, END,
}

enum class Direction {
    LEFT, RIGHT,
}