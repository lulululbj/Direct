package luyao.ktx.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import luyao.ktx.util.YLog

/**
 *  @author: luyao
 * @date: 2021/6/19 下午5:33
 */
abstract class BaseFragment(@LayoutRes layoutId: Int) : Fragment(layoutId) {

    private var isLoaded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        YLog.e("${javaClass.name}: onViewCreated")
        initView()
        super.onViewCreated(view, savedInstanceState)
        if (!lazyLoad()) {
            startObserve()
            initData()
        }
    }

    override fun onResume() {
        super.onResume()
        YLog.e("${javaClass.name}: onResume")
        if (!isLoaded) {
            if (lazyLoad()) {
                startObserve()
                initData()
            }
            isLoaded = true
        }
    }

    override fun onDestroyView() {
        YLog.e("${javaClass.name}: onDestroyView")
        super.onDestroyView()
        isLoaded = false
    }

    abstract fun initView()
    abstract fun initData()
    open fun startObserve() {}

    /**
     * 由于 ViewPager2 的懒加载，导致 Fragment 的生命周期不再准确，所以需要手动控制懒加载
     */
    open fun lazyLoad() = false

}