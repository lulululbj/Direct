package luyao.direct.ui.panel

import android.graphics.Color
import androidx.fragment.app.activityViewModels
import com.hi.dhl.binding.viewbind
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import luyao.direct.R
import luyao.direct.base.DirectBaseFragment
import luyao.direct.databinding.FragmentPanelSettingBinding
import luyao.direct.model.MMKVConstants
import luyao.direct.vm.DataViewModel
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import java.util.*

/**
 * 面板基本设置
 * author: luyao
 * date:   2021/10/28 20:10
 */
class PanelSettingFragment : DirectBaseFragment(R.layout.fragment_panel_setting) {

    private val binding: FragmentPanelSettingBinding by viewbind()
    private val vm by activityViewModels<DataViewModel>()

    override fun initView() {
        initPanel()
    }

    override fun initData() {

    }

    override fun startObserve() {
        vm.mainBgColor.observe(viewLifecycleOwner) {
            binding.mainBgBt.setBackgroundColor(MMKVConstants.mainBgColor)
        }
        vm.panelBgColor.observe(viewLifecycleOwner) {
            binding.run {
                panelBgBt.setBackgroundColor(it)
//                arrayListOf(
//                    mainBgTv,
//                    panelBgTv,
//                    engineSpanCount,
//                    mainTransparencyTv
//                ).forEach { it.setProperTextColor() }
            }
        }
    }

    private fun initPanel() {
        binding.mainBgBt.run {
            setOnClickListener {
                val dialog = ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowCustom(true)
                    .setShowAlphaSlider(true)
                    .setColor(MMKVConstants.mainBgColor)
                    .setDialogId(0)
                    .create()
                dialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                    override fun onColorSelected(dialogId: Int, color: Int) {
                        MMKVConstants.mainBgColor = color
                        vm.mainBgColor.value = color

                    }

                    override fun onDialogDismissed(dialogId: Int) {

                    }
                })
                dialog.show(requireActivity().supportFragmentManager, "main")
            }
        }

        binding.panelBgBt.setBackgroundColor(MMKVConstants.panelBgColor)
        binding.panelBgBt.run {
            setOnClickListener {
                val dialog = ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowCustom(true)
                    .setShowAlphaSlider(true)
                    .setDialogId(1)
                    .setColor(MMKVConstants.panelBgColor)
                    .create()
                dialog.setColorPickerDialogListener(object : ColorPickerDialogListener {
                    override fun onColorSelected(dialogId: Int, color: Int) {
                        MMKVConstants.panelBgColor = color
                        vm.panelBgColor.value = color

                    }

                    override fun onDialogDismissed(dialogId: Int) {

                    }
                })
                dialog.show(requireActivity().supportFragmentManager, "main")
            }
        }

        binding.engineSpanSeekBar.run {
            binding.engineSpanCount.text =
                String.format(
                    Locale.getDefault(),
                    context.getString(R.string.current_span_count),
                    MMKVConstants.engineSpanCount
                )
            progress = MMKVConstants.engineSpanCount
            setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
                override fun onProgressChanged(
                    seekBar: DiscreteSeekBar,
                    value: Int,
                    fromUser: Boolean
                ) {
                    vm.engineSpanCount.value = value
                    MMKVConstants.engineSpanCount = progress
                    binding.engineSpanCount.text =
                        String.format(
                            Locale.getDefault(),
                            context.getString(R.string.current_span_count),
                            progress
                        )
                }

                override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
                }

            })
        }
        binding.mainTransparencySeekBar.run {
            progress = 100 - (Color.alpha(MMKVConstants.mainBgColor) / 255f * 100).toInt()
            binding.mainTransparencyTv.text =
                String.format(Locale.getDefault(), "主页背景透明度 (当前: %d%%)", progress)
            setOnProgressChangeListener(object : DiscreteSeekBar.OnProgressChangeListener {
                override fun onProgressChanged(
                    seekBar: DiscreteSeekBar?,
                    value: Int,
                    fromUser: Boolean
                ) {
                    binding.mainTransparencyTv.text =
                        String.format(Locale.getDefault(), "主页背景透明度 (当前: %d%%)", value)
                    val originColor = MMKVConstants.mainBgColor
                    val color = Color.argb(
                        ((100 - value) / 100f * 255).toInt(),
                        Color.red(originColor),
                        Color.green(originColor),
                        Color.blue(originColor)
                    )
                    MMKVConstants.mainBgColor = color
                    vm.mainBgColor.value = color
                }

                override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
                }
            })
        }
    }


}