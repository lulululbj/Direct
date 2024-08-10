package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.databinding.ItemAppDirectBinding
import luyao.direct.model.AppIconCache
import luyao.direct.model.entity.AppDirect
import luyao.direct.util.loadIcon
import luyao.ktx.ext.startScheme
import luyao.ktx.ext.toast

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/18 13:30
 */
class AppLocalDirectViewDelegate :
    ItemViewDelegate<AppDirect, AppLocalDirectViewDelegate.ViewHolder>() {

    private var checkChangeListener: ((AppDirect, Boolean) -> Unit)? = null

    fun setCheckChangeListener(listener: (AppDirect, Boolean) -> Unit) {
        checkChangeListener = listener
    }

    class ViewHolder(val binding: ItemAppDirectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, item: AppDirect) {
        holder.binding.run {
            item.loadIcon(directIcon)
            directName.text = item.label
            directClass.text = item.className
            directTest.setOnClickListener {
                DirectApp.App.startScheme(item.scheme) {
                    toast(R.string.scheme_unavaliable)
                }
            }
            directRoot.setOnClickListener {
                directCheck.isChecked = !directCheck.isChecked
            }

            directCheck.setOnCheckedChangeListener(null)
            directCheck.isChecked = item.enabled
            directCheck.setOnCheckedChangeListener { _, isChecked ->
                item.enabled = isChecked
                checkChangeListener?.invoke(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) =
        ViewHolder(ItemAppDirectBinding.inflate(LayoutInflater.from(context), parent, false))
}