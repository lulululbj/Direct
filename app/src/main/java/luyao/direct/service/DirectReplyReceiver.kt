package luyao.direct.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.RemoteInput
import luyao.direct.ext.KEY_TEXT_REPLY
import luyao.direct.ui.DirectActivity

/**
 * author: luyao
 * date:   2021/9/18 16:42
 */
public class DirectReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val input = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY)
        context.startActivity(Intent(context, DirectActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("direct_reply", input)
        })
    }
}