package thevoid.whichbinds.dslist

fun <R,T : Comparable<T>> DSListAdapter<R,T>.onLongTapSelection(index: Int) {
    if (!isMultiSelectOn) {
        isMultiSelectOn = true
    }
    addIdIntoSelectedIds(index)
}

fun <R,T : Comparable<T>> DSListAdapter<R,T>.onTapSelection(index: Int) {
    if (isMultiSelectOn) {
        addIdIntoSelectedIds(index)
    }
}

fun <R,T : Comparable<T>> DSListAdapter<R,T>.onRangSelection(range: IntRange, add: Boolean) {
    if (isMultiSelectOn) {
        range.forEach {
            addRangeIdIntoSelectedIds(it, add)
        }
    }
}

fun <R,T : Comparable<T>> DSListAdapter<R,T>.addRangeIdIntoSelectedIds(index: Int, add: Boolean) {
    val id = rows[index].id

    if(add) {
        if (!selectedIds.contains(id))
            id?.let { selectedIds.add(it) }
    } else {
        if (selectedIds.contains(id))
            selectedIds.remove(id)
    }

    notifyItemChanged(index, true)
    if (selectedIds.size < 1) isMultiSelectOn = false
}

fun <R,T : Comparable<T>> DSListAdapter<R,T>.cleanSelectedIds() {
    selectedIds.clear()
    isMultiSelectOn = false
}



fun <R,T : Comparable<T>> DSListAdapter<R,T>.addIdIntoSelectedIds(index: Int) {
    val id = rows[index].id
    if (selectedIds.contains(id))
        selectedIds.remove(id)
    else
        id?.let { selectedIds.add(it) }

    notifyItemChanged(index, true)
    if (selectedIds.size < 1) isMultiSelectOn = false
}

fun <R,T : Comparable<T>> DSListAdapter<R,T>.deleteSelectedIds(): MutableList<T>? {
    val contents: MutableList<T> = mutableListOf()
    if (selectedIds.size < 1) return null
    val selectedIdIteration = selectedIds.listIterator()

    while (selectedIdIteration.hasNext()) {
        val selectedItemID = selectedIdIteration.next()
        var indexOfModelList = 0
        val modelListIteration: MutableListIterator<Row<R, T>> = rows.listIterator()
        while (modelListIteration.hasNext()) {
            val model = modelListIteration.next()
            if (selectedItemID?.equals(model.id)!!) {
                model.content?.let { contents.add(it) }
                modelListIteration.remove()
                selectedIdIteration.remove()
                notifyItemRemoved(indexOfModelList)
            }
            indexOfModelList++
        }

        isMultiSelectOn = false
    }

    return contents
}

fun <R,T : Comparable<T>> DSListAdapter<R,T>.deleteSelectedIds(ids: MutableList<R>): MutableList<T>? {
    val contents: MutableList<T> = mutableListOf()
    if (ids.size < 1) return null
    val selectedIdIteration = ids.listIterator()

    while (selectedIdIteration.hasNext()) {
        val selectedItemID = selectedIdIteration.next()
        var indexOfModelList = 0
        val modelListIteration: MutableListIterator<Row<R, T>> = rows.listIterator()
        while (modelListIteration.hasNext()) {
            val model = modelListIteration.next()
            if (selectedItemID?.equals(model.id)!!) {
                model.content?.let { contents.add(it) }
                modelListIteration.remove()
                selectedIdIteration.remove()
                notifyItemRemoved(indexOfModelList)
            }
            indexOfModelList++
        }
        isMultiSelectOn = false
    }

    return contents
}

fun <R,T : Comparable<T>> DSListAdapter<R,T>.findSelected(id: R?): Boolean =
    selectedIds.contains(id)

fun <R,T : Comparable<T>> DSListAdapter<R,T>.selectAllIds() {
    selectedIds = rows.map { it.id }.toMutableList()
    notifyItemRangeChanged(0, rows.lastIndex, true)
}

fun <R,T : Comparable<T>> DSListAdapter<R,T>.getSelectIds(): MutableList<R?> =
    selectedIds
