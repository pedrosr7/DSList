package thevoid.whichbinds.dslist

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView

data class PaintText(val text: String, val textColor: Int = Color.BLACK, val size: Float = 60f, val margin: Float = 0f)

data class Draw(
    var backgroundColor: Int? = null,
    var icon: Drawable? = null,
    var text: PaintText? = null,
    var paint: Paint? = null,
    var action: ((viewHolder: RecyclerView.ViewHolder) -> Unit)?,
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
    var action: ((viewHolder: RecyclerView.ViewHolder) -> Unit)? = null
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
        paint, action, customDraw
    )
}

fun ColorDrawable.drawBackground(view: View, canvas: Canvas, dX: Int, colorR: Int, direction: Direction) {
    val (left, right) = when (direction) {
        Direction.LEFT -> view.left + dX to view.left
        Direction.RIGHT -> view.right + dX to view.right
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
        Gravity.START -> (view.left + margin) to (view.left + margin + intrinsicWidth)
        Gravity.CENTER -> (view.width / 2) to (view.width / 2) + intrinsicWidth
        Gravity.END -> (view.right - margin - intrinsicWidth) to (view.right - margin)
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
            Gravity.START -> (margin)
            Gravity.CENTER -> (view.width / 2).toFloat()
            Gravity.END -> (view.right - margin - measureText(text))
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