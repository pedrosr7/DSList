package thevoid.whichbinds.dslist

interface SelectionDSL<R, T> {
    fun selectAllIds()
    fun cleanSelectedIds()
    fun onTapSelection(index: Int)
    fun findSelected(id: R?): Boolean
    fun onLongTapSelection(index: Int)
    fun getSelectIds(): MutableList<R?>
    fun deleteSelectedIds(): MutableList<T>?
    fun deleteSelectedIds(ids: MutableList<R>): MutableList<T>?
    fun addIdIntoSelectedIds(index: Int)
}