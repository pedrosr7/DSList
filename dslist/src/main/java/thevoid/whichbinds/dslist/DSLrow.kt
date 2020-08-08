package thevoid.whichbinds.dslist

import android.view.View

typealias BindView<T> = (T, view: View) -> Unit

data class Row<R,T> (
    val id: R?,
    val content: T?,
    val viewType: Int,
    val bindView: BindView<T>
)

class RowBuilder<R,T> {
    var id: R? = null
    var content: T? = null
    var viewType: Int = 0
    private lateinit var bindView: BindView<T>

    fun viewBind(fn: BindView<T>) {
        bindView = fn
    }

    fun build(): Row<R,T> = Row(id, content, viewType, bindView)
}