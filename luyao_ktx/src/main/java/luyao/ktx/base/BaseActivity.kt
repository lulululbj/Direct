package luyao.ktx.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hjq.language.MultiLanguages
import com.hjq.toast.Toaster
import luyao.ktx.core.event.EventObserver
import luyao.ktx.ext.handleInset
import luyao.ktx.ext.isBeforeR
import luyao.ktx.model.UiState


/**
 * Description:
 * Author: luyao
 * Date: 2022/4/21 20:39
 */
abstract class BaseActivity : AppCompatActivity() {

    private var vmList: MutableList<BaseVM> = arrayListOf()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(MultiLanguages.attach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
        observe()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBack()
            }
        })
    }

    abstract fun initView()
    abstract fun initData()
    open fun observe() {}
    open fun handleBack() {
        finish()
    }

    open fun configToolbar(toolbar: Toolbar, title: String) {
        toolbar.run {
            this.title = title
            setSupportActionBar(toolbar)
            setNavigationOnClickListener { handleBack() }
        }
    }

    open fun configToolbarInset(v: View) {
        if (isBeforeR()) return
        v.handleInset { view, insets ->
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                0
            )
        }
    }

    open fun configRootInset(v: View) {
        if (isBeforeR()) return
        v.handleInset { view, insets ->
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )
        }
    }

    fun bindVM(vararg vm: BaseVM) {
        vm.forEach { vmList.add(it) }
        observeVmList()
    }

    private fun observeVmList() {
        vmList.forEach {
            it.uiStateEvent.observe(this, EventObserver { uiState ->
                when (uiState) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {

                    }

                    is UiState.Error -> {
                        showErrorMsg(uiState.errMsg)
                    }
                }
            })

            it.backEvent.observe(this, EventObserver {
                handleBack()
            })
        }
    }

    open fun showErrorMsg(errMsg: String) {
        Toaster.show(errMsg)
    }
}