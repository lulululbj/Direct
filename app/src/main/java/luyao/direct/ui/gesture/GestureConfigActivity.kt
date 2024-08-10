package luyao.direct.ui.gesture

import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.hi.dhl.binding.viewbind
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.R
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityGestureConfigBinding
import luyao.direct.model.entity.RecentEntity
import luyao.direct.ui.panel.StarEditFragment
import luyao.direct.util.Direction
import luyao.direct.util.MoshiUtil
import luyao.direct.vm.DataViewModel
import luyao.ktx.ext.showConfirmDialog
import luyao.ktx.util.YLog

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/5 15:10
 */
@AndroidEntryPoint
class GestureConfigActivity : DirectBaseActivity() {

    companion object {
        const val GESTURE_CONFIG = "gesture_config"
    }

    private val binding: ActivityGestureConfigBinding by viewbind()
    private val dataVM by viewModels<DataViewModel>()
    private val fragmentList = arrayListOf<Fragment>()
    private val starEditFragment by lazy { StarEditFragment.newInstance(false) }
    private val chooseDirectFragment by lazy { ChooseDirectFragment() }
    private val chooseAppFragment by lazy { ChooseAppFragment() }
    private val direction by lazy { intent?.getStringExtra(GESTURE_CONFIG) ?: "" }

    override fun initView() {
        binding.titleLayout.run {
            toolBar.setNavigationOnClickListener { handleBack() }
            searchEt.doAfterTextChanged {
                when (binding.gestureViewPager.currentItem) {
                    0 -> {
                        starEditFragment.search(it.toString())
                    }
                    1 -> {
                        chooseDirectFragment.search(it.toString())
                    }
                    2 -> {
                        chooseAppFragment.searchApp(it.toString())
                    }
                }
            }
        }
        fragmentList.run {
            add(starEditFragment)
            add(chooseDirectFragment)
            add(chooseAppFragment)
        }
        starEditFragment.setGestureConfigListener { entity -> gestureConfig(entity) }
        chooseDirectFragment.setGestureConfigListener { entity -> gestureConfig(entity) }
        chooseAppFragment.setGestureConfigListener { entity -> gestureConfig(entity) }
        initTab()
    }

    private fun gestureConfig(entity: RecentEntity) {
        YLog.e("$direction ${entity.name}")
        showConfirmDialog(message = "是否将 ${entity.name} 配置到 ${getDirectDesc(direction)} 手势？") {
            val configJson = MoshiUtil.recentEntityAdapter.toJson(entity)
            MMKV.defaultMMKV().putString("gesture_$direction", configJson)
            handleBack()
        }
    }

    private fun getDirectDesc(direction: String): String =
        when (direction) {
            Direction.TOP.name -> getString(R.string.slide_up)
            Direction.BOTTOM.name -> getString(R.string.slide_down)
            Direction.LEFT.name -> getString(R.string.slide_left)
            Direction.RIGHT.name -> getString(R.string.slide_right)
            else -> getString(R.string.slide_up)
        }


    override fun initData() {

    }

    private fun initTab() {
        val tabs = arrayListOf(
            getString(R.string.star_use),
            getString(R.string.app_shortcut),
            getString(R.string.app)
        )
        binding.run {
            gestureViewPager.offscreenPageLimit = 3
            gestureViewPager.adapter = object : FragmentStateAdapter(this@GestureConfigActivity) {
                override fun getItemCount() = fragmentList.size

                override fun createFragment(position: Int) = fragmentList[position]
            }

            gestureViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    val keyword = binding.titleLayout.searchEt.text.toString()
                    when (binding.gestureViewPager.currentItem) {
                        0 -> {
                            if (!starEditFragment.isDetached)
                                starEditFragment.search(keyword)
                        }
                        1 -> {
                            if (!chooseDirectFragment.isDetached)
                                chooseDirectFragment.search(keyword)
                        }
                        2 -> {
                            if (!chooseAppFragment.isDetached)
                                chooseAppFragment.searchApp(keyword)
                        }
                    }
                }
            })

            TabLayoutMediator(gestureTabLayout, gestureViewPager) { tab, position ->
                tab.text = tabs[position]
            }.attach()
        }
    }
}