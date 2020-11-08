package thevoid.whichbinds.dslist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

enum class States {
    REFRESH,
    PREPEND,
    APPEND
}

typealias Status = (States) -> Unit

@ExperimentalCoroutinesApi
class DSList<R,T : Comparable<T>> {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val _states: MutableStateFlow<States> = MutableStateFlow(States.REFRESH)
    private val states: StateFlow<States> = _states

    private val adapter: DSListAdapter<R,T> = DSListAdapter()

    val after: R?
        get() { return adapter.rows.lastOrNull()?.id }
    val before: R?
        get() { return adapter.rows.firstOrNull()?.id }

    private var cacheName: String? = null

    private var recyclerView: RecyclerView? = null
        set(value) {
            value?.adapter = adapter
            field = value
            recyclerView?.addOnScrollListener( object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if(recyclerView.reachesTopScrolling(newState)) {
                        if(_states.value != States.REFRESH) _states.value = States.REFRESH
                        _states.value = States.PREPEND
                    }
                    if(recyclerView.reachesBottomScrolling(newState)) {
                        if(_states.value != States.REFRESH) _states.value = States.REFRESH
                        _states.value = States.APPEND
                    }
                }
            })
        }

    fun init(init: InitBuilder.() -> Unit) {
        val initial = InitBuilder().apply(init)
        this.cacheName = initial.cacheName
        this.recyclerView = initial.recyclerView
        this.adapter.cacheName = cacheName
        this.adapter.shimmerViewId = initial.shimmerViewId
        this.adapter.shimmersToAdd = initial.shimmersToAdd
        adapter.retrieveFromCache()
        adapter.addShimmers()
    }

    fun load(fn: Status) = scope.launch {
        states.collect { fn(it) }
    }

    fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, body: (T?) -> Unit) =
        liveData.observe(this, Observer(body))

    fun ul(uL: Ul<R,T>.() -> Unit) {
        adapter.submitRows(Ul<R,T>().apply(uL))
        _states.value = States.REFRESH
    }

    fun manualStateChange(newState: States) {
        _states.value = newState
    }

    fun removeCache() {
        adapter.removeCache()
    }

}

@ExperimentalCoroutinesApi
fun <R,T : Comparable<T>> listDSL(init: DSList<R,T>.() -> Unit) = DSList<R,T>().apply(init)