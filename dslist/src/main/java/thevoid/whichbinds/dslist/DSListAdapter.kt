package thevoid.whichbinds.dslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DSListViewHolder(view: View): RecyclerView.ViewHolder(view)

class DSListAdapter<R,T : Comparable<T>>
    : RecyclerView.Adapter<DSListViewHolder>() {

    var rows: MutableList<Row<R,T>> = mutableListOf()
    var selectedIds: MutableList<R?> = ArrayList()

    var isMultiSelectOn: Boolean = false
        set(value) {
            notifyItemRangeChanged(0, rows.lastIndex, true)
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DSListViewHolder =
        DSListViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)
        )

    override fun getItemViewType(position: Int): Int = this.rows[position].viewType

    override fun getItemCount(): Int = this.rows.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onBindViewHolder(holder: DSListViewHolder, position: Int) {
        val row = this.rows[position]

        row.bindView?.let {
            it(row.id, row.content, holder.itemView, position)
        }
    }

    fun submitRows(rows: MutableList<Row<R,T>>) {
        val oldList = this.rows.toMutableList()
        this.rows = rows

        notifyChanges(oldList, this.rows)
    }

    fun removeAll() {
        val oldList = this.rows.toMutableList()
        rows.clear()
        notifyChanges(oldList, this.rows)
    }

    fun getContents(): List<T> =
        rows.mapNotNull { it.content }

    fun getItemByPosition(position: Int): T? =
        rows[position].content

}

