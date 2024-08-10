package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.databinding.ItemSelectEngineBinding
import luyao.direct.model.entity.NewDirectEntity

/**
 *  @author: luyao
 * @date: 2021/11/1 上午12:37
 */
class SelectEngineViewDelegate :
    ItemViewDelegate<NewDirectEntity, SelectEngineViewDelegate.ViewHolder>() {

    private var checkChangeListener: ((NewDirectEntity, Boolean) -> Unit)? = null

    fun setCheckChangeListener(listener: ((NewDirectEntity, Boolean) -> Unit)? = null) {
        checkChangeListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, item: NewDirectEntity) {
        holder.binding.run {
//            engineCheck.isChecked = item.enabled == 0 && item.showPanel == 1
            Glide.with(root).load(item.iconUrl).into(engineIcon)
            engineName.text = item.label
            root.setOnClickListener {
                checkChangeListener?.invoke(item, true)
//                engineCheck.isChecked = !engineCheck.isChecked
            }
//            engineCheck.setOnCheckedChangeListener { _, isChecked ->
//                checkChangeListener?.invoke(item, isChecked)
//            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(ItemSelectEngineBinding.inflate(LayoutInflater.from(context)))
    }

    class ViewHolder(val binding: ItemSelectEngineBinding) : RecyclerView.ViewHolder(binding.root)
}