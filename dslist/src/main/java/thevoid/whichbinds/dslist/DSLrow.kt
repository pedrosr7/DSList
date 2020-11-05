package thevoid.whichbinds.dslist

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.Serializable

typealias BindView<T> = (T, view: View) -> Unit

data class Row<R,T> (
    val id: R?,
    val content: T?,
    val viewType: Int,
    val bindView: BindView<T?>?
)

class RowBuilder<R,T> {
    var id: R? = null
    var content: T? = null
    var viewType: Int = 0
    var bindView: BindView<T?>? = null

    fun viewBind(fn: BindView<T?>) {
        bindView = fn
    }

    fun build(): Row<R,T> = Row(id, content, viewType, bindView)
}

class Ul <R,T>: ArrayList<Row<R, T>>() {
    fun li(rowBuilder: RowBuilder<R,T>.() -> Unit) {
        add(RowBuilder<R,T>().apply(rowBuilder).build())
    }
}