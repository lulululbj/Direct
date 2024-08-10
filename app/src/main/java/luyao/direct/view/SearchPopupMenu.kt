package luyao.direct.view

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.PopupMenu
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.NewDirectEntity

/**
 * author: luyao
 * date:   2021/9/1 15:54
 */
class SearchPopupMenu(context: Context, list: List<NewDirectEntity>, anchor: View, gravity: Int) :
    PopupMenu(context, anchor, gravity) {

    init {
        list.forEachIndexed { index, searchEntity ->
            val menuItem = menu.add(1, index, index, searchEntity.label)
//                .setIcon(entityResIds[searchEntity.id]!!)
            menuItem.isChecked = searchEntity.id == MMKVConstants.newSearchId
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setForceShowIcon(true)
            }
        }
        menu.setGroupCheckable(1, true, true)
    }
}