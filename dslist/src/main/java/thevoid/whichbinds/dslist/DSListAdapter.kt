package thevoid.whichbinds.dslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DSListViewHolder(view: View): RecyclerView.ViewHolder(view)

class DSListAdapter<R,T : Comparable<T>> : RecyclerView.Adapter<DSListViewHolder>() {

    var rows: MutableList<Row<R,T>> = mutableListOf()
    var cacheName: String? = null
    var shimmerViewId: Int? = null
    var shimmersToAdd: Int = 3

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
        removeShimmers()
        saveToCache()
    }

    fun saveToCache() {
        cacheName?.let { cache ->
            shimmerViewId?.let { target ->
                val withOutShimmer = this.rows.filterNot {
                    it.viewType == target
                }
                DSLcache.saveRowToCache(cache, withOutShimmer)
            }
        }
    }

    fun retrieveFromCache() {
        if(rows.isEmpty()){
            cacheName?.let { name ->
                DSLcache.retrieveRowsFromCache(name)?.let {
                    rows.addAll(it as MutableList<Row<R,T>>)
                }
            }
        }
    }

    fun removeAll() {
        val last = rows.lastIndex
        rows.clear()
        notifyItemRangeRemoved(0, last)
    }

    fun removeShimmers() {
        shimmerViewId?.let { target ->
            val oldList = this.rows
            rows.removeAll {
                it.viewType == target
            }
            notifyChanges(oldList, this.rows)
        }
    }

    fun addShimmers() {
        shimmerViewId?.let { viewType ->
            val oldList = this.rows
            if (rows.isEmpty()) {
                val row = Row<R, T>(null, null, viewType, null)
                repeat(shimmersToAdd) {
                    rows.add(row)
                }
                notifyChanges(oldList, this.rows)
            }
        }
    }

}

