package thevoid.whichbinds.dslist

interface DSLSelection<R, T> {
    fun onTap(index: Int)
    fun onLongTap(index: Int)
    fun selectAllIds()
    fun cleanSelectedIds()
    fun deleteSelectedIds(): MutableList<T>?
    fun addIdIntoSelectedIds(index: Int)
    fun findSelected(id: R?): Boolean
    fun getSelectIds(): MutableList<R?>
}