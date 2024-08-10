package luyao.ktx.view.label

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.hi.dhl.binding.viewbind
import luyao.ktx.databinding.ViewLabelBinding
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.itemPadding
import luyao.ktx.util.MultiDiffCallback

/**
 * Description:
 * Author: luyao
 * Date: 2023/2/9 15:17
 */
class LabelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewLabelBinding by viewbind()
    private val labelAdapter = MultiTypeAdapter()
    private val labelViewDelegate = LabelViewDelegate()
    private val labelList = arrayListOf<Label>()
    var statusChanged: ((Label) -> Unit)? = null

    init {
        initView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {

        labelViewDelegate.onSelect = { selectedLab ->
            labelList.forEach {
                it.checked = selectedLab.name == it.name
            }
            labelAdapter.notifyDataSetChanged()
            statusChanged?.invoke(selectedLab)
        }

        binding.labelRv.run {
            itemPadding(
                top = 0,
                bottom = dp2px(6).toInt(),
                left = 0,
                right = dp2px(8).toInt()
            )
            layoutManager = FlexboxLayoutManager(context)
            labelAdapter.register(labelViewDelegate)
            labelAdapter.items = labelList
            adapter = labelAdapter
        }
    }

    fun setLabelList(labelList: List<Label>) {
        submitDiffList(labelList)
    }

    fun getSelectedLab() = labelList.filter { it.checked }[0]

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedLab(label:String) {
        labelList.forEach {
            it.checked = it.name == label
        }
        labelAdapter.notifyDataSetChanged()
    }

    private fun submitDiffList(newList: List<Label>) {
        val callback = MultiDiffCallback(labelList, newList)
        val result = DiffUtil.calculateDiff(callback)
        labelList.clear()
        labelList.addAll(newList)
        result.dispatchUpdatesTo(labelAdapter)
    }
}