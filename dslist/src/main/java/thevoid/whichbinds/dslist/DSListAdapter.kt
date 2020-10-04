package thevoid.whichbinds.dslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arrow.core.extensions.list.foldable.exists

class FlowViewHolder(view: View): RecyclerView.ViewHolder(view)

class DSListAdapter<R,T> : RecyclerView.Adapter<FlowViewHolder>() {

    val rows: MutableList<Row<R,T>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowViewHolder =
        FlowViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(viewType, parent, false)
        )

    override fun getItemViewType(position: Int): Int = this.rows[position].viewType

    override fun getItemCount(): Int = this.rows.size

    override fun onBindViewHolder(holder: FlowViewHolder, position: Int) {
        val row = this.rows[position]
        row.content?.let { row.bindView(it, holder.itemView) }
    }

    fun submitRows(rows: Row<R,T>){
        if(this.rows.exists { it.id != null && it.id == rows.id  }) return

        this.rows.add(rows)
        notifyDataSetChanged()
    }
}