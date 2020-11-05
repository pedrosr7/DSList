package thevoid.whichbinds.dslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arrow.core.extensions.list.foldable.exists

class DSListViewHolder(view: View): RecyclerView.ViewHolder(view)

class DSListAdapter<R,T> : RecyclerView.Adapter<DSListViewHolder>() {

    val rows: MutableList<Row<R,T>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DSListViewHolder =
        DSListViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)
        )

    override fun getItemViewType(position: Int): Int = this.rows[position].viewType

    override fun getItemCount(): Int = this.rows.size

    override fun onBindViewHolder(holder: DSListViewHolder, position: Int) {
        val row = this.rows[position]

        row.bindView?.let {
            it(row.content, holder.itemView)
        }
    }

    fun submitRow(row: Row<R,T>) {
        if(this.rows.exists { it.id != null && it.id == row.id }) return

        this.rows.add(row)
        DSLCache.saveRowToCache("rows", this.rows)
        notifyItemInserted(this.rows.lastIndex)
    }

    fun submitRows(rows: List<Row<R,T>>) {
        val nonRepeated = rows.filter { p -> this.rows.none { it.id == p.id } }.toMutableList()
        if(nonRepeated.isEmpty()) return
        val last = nonRepeated.lastIndex
        this.rows.prepend(nonRepeated)

        DSLCache.saveRowToCache("rows", this.rows)
        notifyItemRangeInserted(0, last)
    }

    fun retrieveFromCache() {
        if(rows.isEmpty()){
            DSLCache.retrieveRowsFromCache("rows")?.let {
                rows.addAll(it as MutableList<Row<R,T>>)
            }
        }
    }

}

