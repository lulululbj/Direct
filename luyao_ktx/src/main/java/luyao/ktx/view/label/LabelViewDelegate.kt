package luyao.ktx.view.label

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.ktx.R
import luyao.ktx.databinding.ItemLabelBinding

/**
 * Description:
 * Author: luyao
 * Date: 2023/2/9 15:27
 */
class LabelViewDelegate : ItemViewDelegate<Label, LabelViewDelegate.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(val binding: ItemLabelBinding) : RecyclerView.ViewHolder(binding.root)

    var onSelect: ((Label) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, item: Label) {
        holder.binding.labelTv.run {
            text = item.name
            setBackgroundResource(if (item.checked) R.drawable.label_selected else R.drawable.label_unselected)
            setTextColor(
                if (item.checked) ContextCompat.getColor(
                    context,
                    R.color.white
                ) else ContextCompat.getColor(context, R.color.black)
            )

            setOnClickListener {
                onSelect?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        this.context = context
        return ViewHolder(ItemLabelBinding.inflate(LayoutInflater.from(context), parent, false))
    }
}