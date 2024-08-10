package luyao.direct.ui.settings

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat
import com.tencent.mmkv.MMKV
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.MMKVConstants
import luyao.direct.ui.gesture.GestureConfigActivity
import luyao.direct.util.*
import luyao.direct.view.IconPreference
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.scale
import luyao.ktx.ext.startActivity
import luyao.ktx.ext.toByteArray
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/3 06:46
 */
class SidebarSettingFragment : PreferenceFragmentCompat() {

    private var sideBarIconPreference: IconPreference? = null
    private var sideBarSizePreference: Preference? = null

    private val openDocument =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                DirectApp.App.contentResolver.openInputStream(it).use { input ->
                    input?.run {
                        val bitmap = BitmapFactory.decodeStream(this)
                        val scaleBitmap = bitmap.scale(dp2px(32).toInt(), dp2px(32).toInt())
                        sideBarIconPreference?.setIcon(bitmap)

                        val iconStr =
                            Base64.encodeToString(scaleBitmap.toByteArray(), Base64.DEFAULT)
                        MMKVConstants.floatWindowIconBase64 = iconStr
                        updateFloatIcon(getFloatTag())
                    }
                }
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_sidebar_preferences, rootKey)
//        setListener()
    }

    @SuppressLint("CheckResult")
    private fun setListener() {

        // 悬浮窗形状
        val shapeArrays = arrayListOf(
            getString(R.string.shape_line), getString(
                R.string.shape_cicle
            )
        )
        val sideBarShapePreference = findPreference<Preference>("sideBarShape")?.apply {
//            isVisible = MMKVConstants.showSideBar
            summary =
                if (MMKVConstants.defaultSideBarShape == 0) getString(R.string.shape_line) else getString(
                    R.string.shape_cicle
                )
            setOnPreferenceClickListener {
                MaterialDialog(requireActivity()).show {
                    cornerRadius(16f)
                    listItemsSingleChoice(
                        items = shapeArrays,
                        initialSelection = MMKVConstants.defaultSideBarShape
                    ) { _, index, text ->
                        it.summary = text
                        if (MMKVConstants.defaultSideBarShape == index) return@listItemsSingleChoice
                        hideFloatView(getFloatTag())
                        MMKVConstants.defaultSideBarShape = index
                        sideBarSizePreference?.isEnabled = MMKVConstants.defaultSideBarShape == 1
                        sideBarIconPreference?.isEnabled = MMKVConstants.defaultSideBarShape == 1
                        showFloatView(getFloatTag(), true)
                    }
                }
                true
            }
        }


        sideBarIconPreference = findPreference("sideBarIcon")
        sideBarIconPreference?.run {
            isEnabled = MMKVConstants.defaultSideBarShape == 1
            setOnPreferenceClickListener {
                openDocument.launch("image/*")
                true
            }
        }


        // 悬浮窗位置
//        val arrays = arrayListOf(getString(R.string.left), getString(R.string.right))
//        val sideBarLocationPreference = findPreference<Preference>("sideBarLocation")?.apply {
//            isVisible = MMKVConstants.showSideBar
//            summary =
//                if (MMKVConstants.defaultSideBarLocation == 0) getString(R.string.left) else getString(
//                    R.string.right
//                )
//            setOnPreferenceClickListener {
//                MaterialDialog(requireActivity()).show {
//                    cornerRadius(16f)
//                    listItemsSingleChoice(
//                        items = arrays,
//                        initialSelection = MMKVConstants.defaultSideBarLocation
//                    ) { _, index, text ->
//                        it.summary = text
//
//                        if (MMKVConstants.defaultSideBarLocation == index) return@listItemsSingleChoice
//
//                        hideFloatView(getFloatTag())
//                        MMKVConstants.defaultSideBarLocation = index
//                        showFloatView(getFloatTag(), true)
//                    }
//                }
//                true
//            }
//        }

        // 悬浮窗颜色
        val sideBarColorPreference = findPreference<ColorPreferenceCompat>("sideBarColor")?.apply {
            setDefaultValue(MMKVConstants.sideBarColor)
//            isVisible = MMKVConstants.showSideBar
        }
        sideBarColorPreference?.setOnPreferenceChangeListener { _, newValue ->
            if (MMKVConstants.sideBarColor == newValue) true
            else {
                MMKVConstants.sideBarColor = newValue as Int
                updateFloatView(getFloatTag())
                true
            }
        }

        // 悬浮窗开关
        findPreference<SwitchPreferenceCompat>("sideBar")?.apply {
            isChecked = MMKVConstants.showSideBar
        }?.setOnPreferenceChangeListener { _, newValue ->
//            sideBarLocationPreference?.isVisible = newValue as Boolean
//            sideBarColorPreference?.isVisible = newValue
//            sideBarShapePreference?.isVisible = newValue

            if (newValue as Boolean) {
                showFloatView(getFloatTag())
            } else {
                hideFloatView(getFloatTag())
            }
            MMKVConstants.showSideBar = newValue
            true
        }

        // 悬浮窗大小
        sideBarSizePreference = findPreference<Preference>("sideBarSize")?.apply {
            summary = MMKVConstants.sideBarSize.toString()
            isEnabled = MMKVConstants.defaultSideBarShape == 1
        }
        sideBarSizePreference?.setOnPreferenceClickListener {
            val dialog = MaterialDialog(requireContext()).customView(R.layout.dialog_seekbar)
            val view = dialog.getCustomView()
            val dialogSeekbar = view.findViewById<DiscreteSeekBar>(R.id.dialogSeekbar)
            dialogSeekbar.progress = MMKVConstants.sideBarSize
            dialogSeekbar.setOnProgressChangeListener(object :
                DiscreteSeekBar.OnProgressChangeListener {
                override fun onProgressChanged(
                    seekBar: DiscreteSeekBar?,
                    value: Int,
                    fromUser: Boolean
                ) {
                    it.summary = "$value"
                    MMKVConstants.sideBarSize = value
                    val size = dp2px(value).toInt()
                    updateFloatSize(getFloatTag(), size, size)
                }

                override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
                }

            })
            dialog.show()
            true
        }

        // 手势震动反馈
        findPreference<SwitchPreference>("gestureVibrate")?.apply {
            isChecked = MMKVConstants.gestureVibrate
        }?.setOnPreferenceChangeListener { _, newValue ->
            MMKVConstants.gestureVibrate = newValue as Boolean
            true
        }

        // 上滑
        findPreference<Preference>("slideUp")?.apply {
            val topJson = MMKV.defaultMMKV().getString("gesture_${Direction.TOP.name}", "")
            val recentEntity =
                if (topJson.isNullOrBlank()) null else
                    MoshiUtil.recentEntityAdapter.fromJson(topJson)
            summary = recentEntity?.name ?: ""
        }?.setOnPreferenceClickListener {
            startActivity<GestureConfigActivity>(GestureConfigActivity.GESTURE_CONFIG to Direction.TOP.name)
            true
        }

        // 下滑
        findPreference<Preference>("slideDown")?.apply {
            val bottomJson = MMKV.defaultMMKV().getString("gesture_${Direction.BOTTOM.name}", "")
            val recentEntity =
                if (bottomJson.isNullOrBlank()) null
                else MoshiUtil.recentEntityAdapter.fromJson(bottomJson)
            summary = recentEntity?.name ?: ""
        }?.setOnPreferenceClickListener {
            startActivity<GestureConfigActivity>(GestureConfigActivity.GESTURE_CONFIG to Direction.BOTTOM.name)
            true
        }

        // 左滑
        findPreference<Preference>("slideLeft")?.apply {
            val leftJson = MMKV.defaultMMKV().getString("gesture_${Direction.LEFT.name}", "")
            val recentEntity =
                if (leftJson.isNullOrBlank()) null else
                    MoshiUtil.recentEntityAdapter.fromJson(leftJson)
            summary = recentEntity?.name ?: ""
        }?.setOnPreferenceClickListener {
            startActivity<GestureConfigActivity>(GestureConfigActivity.GESTURE_CONFIG to Direction.LEFT.name)
            true
        }

        // 右滑
        findPreference<Preference>("slideRight")?.apply {
            val rightJson = MMKV.defaultMMKV().getString("gesture_${Direction.RIGHT.name}", "")
            val recentEntity = if (rightJson.isNullOrBlank()) null else
                MoshiUtil.recentEntityAdapter.fromJson(rightJson)
            summary = recentEntity?.name ?: ""
        }?.setOnPreferenceClickListener {
            startActivity<GestureConfigActivity>(GestureConfigActivity.GESTURE_CONFIG to Direction.RIGHT.name)
            true
        }

    }

    override fun onResume() {
        super.onResume()
        setListener()
    }
}