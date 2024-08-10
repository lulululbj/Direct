package luyao.direct.view

import android.app.Activity
import android.app.Dialog
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import luyao.direct.databinding.DialogSpeicalTagBinding
import luyao.direct.model.MMKVConstants

/**
 * author: luyao
 * date:   2021/11/1 22:42
 */
class SpecialTagDialog(activity: Activity) : Dialog(activity) {

    private val binding = DialogSpeicalTagBinding.inflate(activity.layoutInflater)

    init {
        initView()
        setContentView(binding.root)
        window?.run {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun initView() {
        binding.run {
            starTagEt.setText(MMKVConstants.starTag)
            recentTagEt.setText(MMKVConstants.recentTag)
            historyTagEt.setText(MMKVConstants.historyTag)
            defaultInputTagEt.setText(MMKVConstants.defaultInputTag)
            queryEngineTagEt.setText(MMKVConstants.engineTag)
            starTagEt.doOnTextChanged { text, _, _, _ -> MMKVConstants.starTag = text.toString() }
            recentTagEt.doOnTextChanged { text, _, _, _ ->
                MMKVConstants.recentTag = text.toString()
            }
            historyTagEt.doOnTextChanged { text, _, _, _ ->
                MMKVConstants.historyTag = text.toString()
            }
            defaultInputTagEt.doOnTextChanged { text, _, _, _ ->
                MMKVConstants.defaultInputTag = text.toString()
            }
            queryEngineTagEt.doOnTextChanged { text, _, _, _ ->
                MMKVConstants.engineTag = text.toString()
            }
        }
    }

}