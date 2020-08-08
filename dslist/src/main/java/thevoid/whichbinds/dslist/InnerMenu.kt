package thevoid.whichbinds.dslist

import android.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView

data class MenuItem(val title: String, val onClick: (value: String) -> Unit)

class MenuItemBuilder {
    lateinit var title: String
    lateinit var onClick: (value: String) -> Unit

    fun build(): MenuItem = MenuItem(title, onClick)
}

class ListOfItems : ArrayList<MenuItem>() {
    fun item(menuItemBuilder: MenuItemBuilder.() -> Unit) {
        add(MenuItemBuilder().apply(menuItemBuilder).build())
    }
}

interface MenuDLS {
    val context: Context
    val layout: ViewGroup
    val listOfItems: ListOfItems.() -> Unit
    val items: MutableList<MenuItem>
}

class MenuDLSImpl (
    override val context: Context,
    override val layout: ViewGroup,
    override val listOfItems: ListOfItems.() -> Unit
) : MenuDLS {

    override val items: MutableList<MenuItem> =  mutableListOf()

    init {
        items.addAll(ListOfItems().apply(listOfItems))
        if(layout.visibility == View.GONE) {

            val listView = ListView(context)
            listView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val itemsTitle = items.map { it.title }

            listView.adapter = ArrayAdapter<String>(context, R.layout.simple_list_item_1, itemsTitle)
            listView.divider = null
            listView.justifyListViewHeightBasedOnChildren()

            layout.setPadding(80, 0,0,0)
            layout.addView(listView)

            listView.setOnItemClickListener { adapterView: AdapterView<*>, _: View, position: Int, _: Long ->
                items[position].onClick(adapterView.getItemAtPosition(position) as String)
            }

            layout.animate()
                .alpha(1.0f)
                .translationY(5f)
                .withStartAction { layout?.let { view -> view.visibility = View.VISIBLE } }
                .duration = 500
        } else {
            layout.removeAllViews()
            layout.animate()
                .alpha(0.0f)
                .translationY(0f)
                .withEndAction { layout?.let { view -> view.visibility = View.GONE } }
                .duration = 500
        }
    }
}

fun innerMenu(
    context: Context,
    layout: ViewGroup,
    listOfItems: ListOfItems.() -> Unit
) = MenuDLSImpl(context, layout, listOfItems)

private fun ListView.justifyListViewHeightBasedOnChildren() {
    val adapter = adapter ?: return
    val vg: ViewGroup = this
    var totalHeight = 0
    for (i in 0 until adapter.count) {
        val listItem = adapter.getView(i, null, vg)
        listItem.measure(0, 0)
        totalHeight += listItem.measuredHeight
    }
    val par = layoutParams
    par.height = totalHeight + dividerHeight * (adapter.count - 1)
    layoutParams = par
    requestLayout()
}