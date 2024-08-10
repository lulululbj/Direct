package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.databinding.ItemUrlBinding
import luyao.direct.model.AppIconCache
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.UrlEntity
import luyao.ktx.ext.openBrowser
import java.util.*

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/21 11:12
 */
class UrlViewDelegate : ItemViewDelegate<UrlEntity, UrlViewDelegate.ViewHolder>() {


    class ViewHolder(val binding: ItemUrlBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, item: UrlEntity) {
        holder.binding.run {
            root.setBackgroundResource(R.drawable.bg_single_direct)
            urlTv.text = String.format(
                Locale.getDefault(),
                DirectApp.App.getString(R.string.visit_url),
                item.url
            )
            if (MMKVConstants.defaultBrowser.isEmpty()) {
                browserIcon.setImageResource(R.drawable.ic_browser)
            } else {
                AppIconCache.setImageViewBitmap(MMKVConstants.defaultBrowser, "", browserIcon)
            }
            root.setOnClickListener {
                val url = if (item.url.startsWith("http") || item.url.startsWith("https")
                    || item.url.startsWith("ftp") || item.url.startsWith("file")
                ) item.url else "http://${item.url}"
                DirectApp.App.openBrowser(url, MMKVConstants.defaultBrowser)
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) =
        ViewHolder(ItemUrlBinding.inflate(LayoutInflater.from(context), parent, false))
}