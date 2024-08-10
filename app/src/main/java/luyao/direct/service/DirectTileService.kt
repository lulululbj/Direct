package luyao.direct.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import luyao.direct.ui.DirectActivity

/**
 * author: luyao
 * date:   2021/9/13 15:18
 */
@RequiresApi(Build.VERSION_CODES.N)
class DirectTileService : TileService() {

    override fun onClick() {
        super.onClick()
        startActivityAndCollapse(Intent(this, DirectActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}