package thevoid.whichbinds.dslist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

enum class ListState {
    REFRESH,
    PREPEND,
    APPEND
}

typealias Status = (ListState) -> Unit

@ExperimentalCoroutinesApi
class DSList<R,T> {

    val _listState: MutableStateFlow<ListState> = MutableStateFlow(ListState.REFRESH)
    val listState: StateFlow<ListState> = _listState

    val adapter: DSListAdapter<R,T> = DSListAdapter()

    val after: R?
        get() { return adapter.rows.lastOrNull()?.id }
    val before: R?
        get() { return adapter.rows.firstOrNull()?.id }

    var recyclerView: RecyclerView? = null
        set(value) {
            value?.adapter = adapter
            field = value
            adapter.retrieveFromCache()
            recyclerView?.addOnScrollListener( object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if(recyclerView.reachesTopScrolling(newState)) {
                        if(_listState.value != ListState.REFRESH) _listState.value = ListState.REFRESH
                        _listState.value = ListState.PREPEND
                    }
                    if(recyclerView.reachesBottomScrolling(newState)) {
                        if(_listState.value != ListState.REFRESH) _listState.value = ListState.REFRESH
                        _listState.value = ListState.APPEND
                    }
                }
            })
        }


    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun load(fn: Status) = scope.launch {
        listState.collect { fn(it) }
    }

    fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
        liveData.observe(this, Observer(body))

    fun row(rowBuilder: RowBuilder<R,T>.() -> Unit) {
        adapter.submitRow(RowBuilder<R,T>().apply(rowBuilder).build())
        _listState.value = ListState.REFRESH
    }

    fun ul(aRow: Ul<R,T>.() -> Unit) {
        adapter.submitRows(Ul<R,T>().apply(aRow))
        _listState.value = ListState.REFRESH
    }

}

@ExperimentalCoroutinesApi
fun <R,T> listDSL(init: DSList<R,T>.() -> Unit) = DSList<R,T>().apply(init)