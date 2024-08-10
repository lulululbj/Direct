package luyao.direct.view

import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import luyao.direct.R
import luyao.direct.adapter.EngineViewDelegate
import luyao.direct.adapter.MultiDiffCallback
import luyao.direct.databinding.DialogKeyboradUtilBinding
import luyao.direct.ext.go
import luyao.direct.model.dao.saveSearchHistory
import luyao.direct.model.entity.NewDirectEntity
import luyao.direct.model.entity.SearchHistoryEntity
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.getScreenWidth
import luyao.ktx.ext.hideKeyboard
import luyao.view.keyboard.KeyboardChangeListener

/**
 * 输入法工具栏
 *  @author: luyao
 * @date: 2021/7/8 上午6:16
 */
class ToolPopupWindowManager(
    private val activity: ComponentActivity,
    private val searchEt: EditText
) :
    LifecycleObserver {

    private val mKeyboardChangeListener by lazy { KeyboardChangeListener(activity) }
    private var isKeyboardShowing = false

    private val binding by lazy { DialogKeyboradUtilBinding.inflate(activity.layoutInflater) }

    private var mKeyboardPopupWindow: PopupWindow? = null
    private val mToolAdapter by lazy { MultiTypeAdapter() }
    private val mToolViewDelegate by lazy {
        EngineViewDelegate(engineClick = { it, _, _ ->
            it.go(activity, searchEt.text.toString())
            saveSearchHistory(searchEt.text.toString(), SearchHistoryEntity.ENGINE)
        })
    }
    private val searchEngineList = arrayListOf<NewDirectEntity>()
    private var heightListener: ((Int) -> Unit)? = null

    private var needChange = true // 输入法显示或隐藏时是否需要同步工具栏状态
    private var keyboardHeight = 0

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun init() {
        activity.lifecycle.addObserver(mKeyboardChangeListener)
        initToolPopupWindow()
        mKeyboardChangeListener.addListener { b, i ->
            handleKeyboardChange(b, i)
        }
    }

    public fun getWindowHeight(): Int {
        return mKeyboardPopupWindow?.height ?: 0
    }

    private fun initToolPopupWindow() {
        if (mKeyboardPopupWindow == null) {
            initRecyclerView()
            mKeyboardPopupWindow = PopupWindow(
                binding.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp2px(40).toInt(),
                true
            )

            mKeyboardPopupWindow?.isTouchable = true
            mKeyboardPopupWindow?.isOutsideTouchable = false
            mKeyboardPopupWindow?.isFocusable = false
            mKeyboardPopupWindow?.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            mKeyboardPopupWindow?.animationStyle = R.style.ToolPopupStyle
        }
    }

    private fun initRecyclerView() {
        binding.toolRecyclerView.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        mToolAdapter.run {
            register(mToolViewDelegate)
            items = searchEngineList
            binding.toolRecyclerView.adapter = this
        }
    }

    fun bindData(it: List<NewDirectEntity>) {
        val callback = MultiDiffCallback(searchEngineList, it)
        val result = DiffUtil.calculateDiff(callback)
        searchEngineList.clear()
        searchEngineList.addAll(it)
        result.dispatchUpdatesTo(mToolAdapter)
    }

    fun setHeightListener(listener: (Int) -> Unit) {
        this.heightListener = listener
    }

    private fun handleKeyboardChange(isShowing: Boolean, keyboardHeight: Int) {
        this.keyboardHeight = keyboardHeight
        if (!needChange) {
            return
        }

        heightListener?.invoke(keyboardHeight)
        val lastShowing = isKeyboardShowing
        isKeyboardShowing = isShowing
        if (isKeyboardShowing) {
            showKeyboardTool(keyboardHeight)
        } else if (lastShowing) {
            closeKeyboardTool()
        }
    }

    private fun showKeyboardTool(height: Int) {
        mKeyboardPopupWindow?.let {
            if (!it.isShowing)
                it.showAtLocation(
                    activity.findViewById(android.R.id.content),
                    Gravity.BOTTOM,
                    activity.getScreenWidth() / 2,
                    height
                )
        }
        updatePopupWindowLocation(height)

    }

    private fun updatePopupWindowLocation(height: Int) {
        mKeyboardPopupWindow?.let {
            it.update(activity.getScreenWidth() / 2, height, it.width, it.height)
        }
    }

    private fun closeKeyboardTool() {
        mKeyboardPopupWindow?.dismiss()
    }

    fun closeInputMethod() {
        mKeyboardPopupWindow?.height = keyboardHeight
        needChange = false
        activity.hideKeyboard(binding.root)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        mKeyboardPopupWindow?.let {
            if (it.isShowing) it.dismiss()
        }
        mKeyboardPopupWindow = null
    }

}