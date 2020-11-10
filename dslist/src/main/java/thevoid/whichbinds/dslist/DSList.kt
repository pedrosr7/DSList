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
class DSList<R,T : Comparable<T>>
    : DSLSelection<R,T> {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val _states: MutableStateFlow<States> = MutableStateFlow(States.REFRESH)
    private val states: StateFlow<States> = _states
    private val adapter: DSListAdapter<R,T> = DSListAdapter()
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

    val after: R?
        get() { return adapter.rows.lastOrNull()?.id }
    val before: R?
        get() { return adapter.rows.firstOrNull()?.id }

    val isMultiSelectOn: Boolean
        get() = adapter.isMultiSelectOn

    fun init(init: InitBuilder.() -> Unit) {
        val initial = InitBuilder().apply(init)
        this.recyclerView = initial.recyclerView
        initial.shimmerViewId?.let { addShimmers(it, initial.shimmersToAdd) }
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

    private fun addShimmers(shimmerViewId: Int, shimmersToAdd: Int = 3){
        if(adapter.rows.isEmpty()) {
            val rows: MutableList<Row<R,T>> = mutableListOf()
            val row = Row<R, T>(null, null, shimmerViewId, null)
            repeat(shimmersToAdd) {
                rows.add(row)
            }
            adapter.submitRows(rows)
        }
    }

    override fun onLongTap(index: Int) {
        adapter.onLongTap(index)
    }

    override fun onTap(index: Int) {
        adapter.onTap(index)
    }

    override fun cleanSelectedIds() {
        adapter.cleanSelectedIds()
    }

    override fun deleteSelectedIds(): MutableList<T>? =
        adapter.deleteSelectedIds()

    override fun addIdIntoSelectedIds(index: Int) {
        addIdIntoSelectedIds(index)
    }

    override fun findSelected(id: R?): Boolean =
        adapter.findSelected(id)

    override fun selectAllIds() {
        adapter.selectAllIds()
    }

    override fun getSelectIds(): MutableList<R?> =
        adapter.getSelectIds()

    fun getRows(): MutableList<Row<R,T>> = adapter.rows

    fun getContent(): List<T> = adapter.getContents()
}

@ExperimentalCoroutinesApi
fun <R,T : Comparable<T>> listDSL(init: DSList<R,T>.() -> Unit) = DSList<R,T>().apply(init)