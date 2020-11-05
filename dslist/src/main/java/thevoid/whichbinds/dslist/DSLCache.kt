package thevoid.whichbinds.dslist

import androidx.collection.LruCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object DSLCache {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val lru: LruCache<Any, Any> = LruCache(100000)

    fun saveRowToCache(key: String, row: Any) {

        try {
            scope.launch {
                lru.put(key, row)
            }
        } catch (e: Exception) { }

    }

    fun retrieveRowsFromCache(key: String): Any? {

        try {
            return lru.get(key)
        } catch (e: Exception) { }

        return null
    }

}