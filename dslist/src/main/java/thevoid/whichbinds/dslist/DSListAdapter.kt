package thevoid.whichbinds.dslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DSListViewHolder(view: View): RecyclerView.ViewHolder(view)

class DSListAdapter<R,T : Comparable<T>>
    : RecyclerView.Adapter<DSListViewHolder>(), DSLSelection<R,T> {

    var rows: MutableList<Row<R,T>> = mutableListOf()
    private var selectedIds: MutableList<R?> = ArrayList()

    var isMultiSelectOn: Boolean = false
        set(value) {
            notifyItemRangeChanged(0, rows.lastIndex)
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

    override fun onLongTap(index: Int) {
        if (!isMultiSelectOn) {
            isMultiSelectOn = true
        }
        addIdIntoSelectedIds(index)
    }

    override fun onTap(index: Int) {
        if (isMultiSelectOn) {
            addIdIntoSelectedIds(index)
        }
    }

    override fun cleanSelectedIds() {
        selectedIds.clear()
        isMultiSelectOn = false
    }

    override fun deleteSelectedIds(): MutableList<T>? {
        val contents: MutableList<T> = mutableListOf()
        if (selectedIds.size < 1) return null
        val selectedIdIteration = selectedIds.listIterator()

        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            var indexOfModelList = 0
            val modelListIteration: MutableListIterator<Row<R, T>> = rows.listIterator()
            while (modelListIteration.hasNext()) {
                val model = modelListIteration.next()
                if (selectedItemID?.equals(model.id)!!) {
                    model.content?.let { contents.add(it) }
                    modelListIteration.remove()
                    selectedIdIteration.remove()
                    notifyItemRemoved(indexOfModelList)
                }
                indexOfModelList++
            }

            isMultiSelectOn = false
        }

        return contents
    }

    override fun addIdIntoSelectedIds(index: Int) {
        val id = rows[index].id
        if (selectedIds.contains(id))
            selectedIds.remove(id)
        else
            id?.let { selectedIds.add(it) }

        notifyItemChanged(index)
        if (selectedIds.size < 1) isMultiSelectOn = false
    }

    override fun findSelected(id: R?): Boolean =
        selectedIds.contains(id)

    override fun selectAllIds() {
        selectedIds = rows.map { it.id }.toMutableList()
        notifyItemRangeChanged(0, rows.lastIndex)
    }

    override fun getSelectIds(): MutableList<R?> =
        selectedIds

    fun getContents(): List<T> {
        return rows.mapNotNull { it.content }
    }

}

