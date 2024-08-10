package luyao.direct.view

import android.app.Dialog
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.adapter.SelectEngineViewDelegate
import luyao.direct.databinding.DialogSelectEngineBinding
import luyao.direct.model.AppDatabase
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.NewDirectEntity

/**
 *  @author: luyao
 * @date: 2021/11/1 上午12:27
 */
class SelectEngineDialog(private val activity: AppCompatActivity) : Dialog(activity) {

    private var clickListener: ((NewDirectEntity) -> Unit)? = null
    private val binding = DialogSelectEngineBinding.inflate(activity.layoutInflater)
    private val engineList = arrayListOf<NewDirectEntity>()
    private val engineAdapter by lazy { MultiTypeAdapter() }
    private val engineViewDelegate by lazy { SelectEngineViewDelegate() }

    fun setClickListener(action: (NewDirectEntity) -> Unit) {
        clickListener = action
    }


    init {
        initView()
        setContentView(binding.root)
        window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT

        initData()
    }

    private fun initView() {
        binding.root.setBackgroundColor(MMKVConstants.panelBgColor)
        binding.engineRecyclerView.layoutManager = LinearLayoutManager(activity)
//        engineViewDelegate.setClickListener {
//            clickListener?.invoke(it)
//            dismiss()
//        }
        engineAdapter.run {
            register(engineViewDelegate)
            items = engineList
            binding.engineRecyclerView.adapter = this
        }

        binding.searchEngineEt.doOnTextChanged { text, _, _, _ ->
            loadData(text.toString())
        }
    }

    private fun initData() {
        loadData()
    }

    private fun loadData(keyWord: String = "") {
        activity.lifecycleScope.launch {
            val deferred = async(Dispatchers.IO) {
                if (keyWord.isEmpty())
                    AppDatabase.getInstance(activity).newDirectDao().getAllSearchEngine()
                else AppDatabase.getInstance(activity).newDirectDao().getSearchEngineByKeyword(keyWord)
            }
            deferred.await().run {
                submitList(this.filter { !it.iconUrl.isNullOrEmpty() })
            }
        }
    }

    private fun submitList(list: List<NewDirectEntity>) {
        val callback = MultiDiffCallback(engineList, list)
        val result = DiffUtil.calculateDiff(callback)
        engineList.clear()
        engineList.addAll(list)
        result.dispatchUpdatesTo(engineAdapter)
    }
}
