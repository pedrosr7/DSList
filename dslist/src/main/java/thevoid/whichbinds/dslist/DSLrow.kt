package thevoid.whichbinds.dslist

import android.view.View

typealias BindView<T> = (T, view: View) -> Unit

data class Row<out R, T : Comparable<T>> (
    val id: R?,
    val content: T?,
    val viewType: Int,
    val bindView: BindView<T?>?
): Comparable<T?> {
    override fun compareTo(other: T?): Int {
        return if(content == null && other == null) 0
        else if(content != null && other == null) -1
        else if(content == null && other != null) -1
        else content!!.compareTo(other!!)
    }


}
class RowBuilder<R,T : Comparable<T>> {
    var id: R? = null
    var content: T? = null
    var viewType: Int = 0
    var bindView: BindView<T?>? = null

    fun viewBind(fn: BindView<T?>) {
        bindView = fn
    }

    fun build(): Row<R,T> = Row(id, content, viewType, bindView)
}

class Ul <R,T : Comparable<T>>: ArrayList<Row<R, T>>() {
    fun li(rowBuilder: RowBuilder<R,T>.() -> Unit) {
        add(RowBuilder<R,T>().apply(rowBuilder).build())
    }
}