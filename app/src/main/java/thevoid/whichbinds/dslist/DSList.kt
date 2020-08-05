package thevoid.whichbinds.dslist

import android.annotation.SuppressLint
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

    private val _listState: MutableStateFlow<ListState> = MutableStateFlow(ListState.APPEND)
    private val listState: StateFlow<ListState> = _listState

    private val adapter: DSListAdapter<R,T> = DSListAdapter()
    val rows: ArrayList<Row<R, T>> = arrayListOf()

    val after: R?
        get() { return rows.lastOrNull()?.id }
    val before: R?
        get() { return rows.firstOrNull()?.id }

    var recyclerView: RecyclerView? = null
        set(value) {
            value?.adapter = adapter
            field = value
            recyclerView?.addOnScrollListener( object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if(recyclerView.reachesTopScrolling(newState)) {
                        _listState.value = ListState.PREPEND
                    }
                    if(recyclerView.reachesBottomScrolling(newState)) {
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

    @SuppressLint("CheckResult")
    fun row(rowBuilderk: RowBuilder<R,T>.() -> Unit) {
        rows.add(RowBuilder<R,T>().apply(rowBuilderk).build())
        adapter.submitRows(RowBuilder<R,T>().apply(rowBuilderk).build())
        _listState.value = ListState.REFRESH
    }

}

@ExperimentalCoroutinesApi
fun <R,T> listPaged(init: DSList<R,T>.() -> Unit) = DSList<R,T>().apply(init)