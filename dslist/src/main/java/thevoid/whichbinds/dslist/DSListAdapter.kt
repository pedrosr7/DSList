package thevoid.whichbinds.dslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DSListViewHolder(view: View): RecyclerView.ViewHolder(view)

class DSListAdapter<R,T : Comparable<T>> : RecyclerView.Adapter<DSListViewHolder>() {

    var rows: MutableList<Row<R,T>> = mutableListOf()

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

    fun submitRows(rows: MutableList<Row<R,T>>) {
        val oldList = this.rows
        this.rows = rows

        notifyChanges(oldList, this.rows)
        DSLCache.saveRowToCache("rows", this.rows)
    }

    fun retrieveFromCache() {
        if(rows.isEmpty()){
            DSLCache.retrieveRowsFromCache("rows")?.let {
                rows.addAll(it as MutableList<Row<R,T>>)
            }
        }
    }

    fun removeAll() {
        val last = rows.lastIndex
        rows.clear()
        notifyItemRangeRemoved(0, last)
    }

    fun removeByViewType(target: Int) {
        val oldList = this.rows
        rows.removeAll {
            it.viewType == target
        }

        notifyChanges(oldList, this.rows)
    }

    fun configShimmer(number: Int, viewType: Int) {
        if (rows.isEmpty()) {
            val row = Row<R, T>(null, null, viewType) { _, _ -> }
            repeat(number) {
                rows.add(row)
            }

            notifyItemRangeInserted(0, rows.lastIndex)
        }
    }


}

