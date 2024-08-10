package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemKeywordBinding

class KeywordViewDelegate(private val keywordClick: (String) -> Unit) :
    ItemViewDelegate<String, KeywordViewDelegate.ViewHolder>() {

    class ViewHolder(val binding: ItemKeywordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, item: String) {
        holder.binding.run {
            root.text = item
            root.setOnClickListener {
                keywordClick.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) = ViewHolder(
        ItemKeywordBinding.inflate(LayoutInflater.from(context), parent, false)
    )
}