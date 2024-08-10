package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.R
import luyao.direct.databinding.ItemUrlBinding
import luyao.direct.model.entity.PasteEntity
import luyao.ktx.ext.dp2px

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/21 11:12
 */
class PasteViewDelegate(
    private val clickPaste: (String) -> Unit,
    private val deletePaste: (Int) -> Unit
) :
    ItemViewDelegate<PasteEntity, PasteViewDelegate.ViewHolder>() {


    class ViewHolder(val binding: ItemUrlBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, item: PasteEntity) {
        holder.binding.run {
            root.setBackgroundResource(R.drawable.bg_single_direct)
            urlTv.text = item.content
            browserIcon.setPadding(dp2px(4).toInt())
            browserIcon.setImageResource(R.drawable.ic_copy)
            root.setOnClickListener {
                clickPaste.invoke(item.content)
            }
            clearIcon.isVisible = true
            clearIcon.setOnClickListener {
                deletePaste.invoke(holder.layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) =
        ViewHolder(ItemUrlBinding.inflate(LayoutInflater.from(context), parent, false))
}