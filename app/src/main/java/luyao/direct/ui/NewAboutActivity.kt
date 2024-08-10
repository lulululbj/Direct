package luyao.direct.ui

import com.hi.dhl.binding.viewbind
import de.psdev.licensesdialog.LicensesDialog
import luyao.direct.R
import luyao.direct.base.DirectBaseActivity
import luyao.direct.databinding.ActivityAboutBinding
import luyao.direct.ext.joinQQGroup
import luyao.direct.model.DirectConstants
import luyao.ktx.ext.copyToClipboard
import luyao.ktx.ext.sendEmail
import luyao.ktx.ext.toast
import luyao.ktx.ext.versionName
import luyao.ktx.web.WebLauncher

/**
 * Description:
 * Author: luyao
 * Date: 2023/6/20 10:08
 */
class NewAboutActivity : DirectBaseActivity() {

    private val binding by viewbind<ActivityAboutBinding>()

    override fun initView() {
        configRootInset(binding.root)
        configToolbar(binding.titleLayout.toolBar, getString(R.string.app_name))

        binding.run {
            versionTv.text = String.format("v%s", versionName)
        }

        initListener()
    }

    override fun initData() {

    }

    private fun initListener() {
        binding.run {
            aboutDeveloper.aboutClick = {
                copyToClipboard("bingxinshuo_")
                toast("已复制微信号")
            }

            aboutMail.aboutClick = {
                sendEmail("sunluyao1993x@gmail.com")
            }

            aboutQQ.aboutClick = {
                joinQQGroup()
            }

            userLicense.settingClick = {
                WebLauncher().start(this@NewAboutActivity) {
                    title(R.string.user_license)
                    url(DirectConstants.USER_LICENSE_URL)
                    titleTextColor(R.color.title_text_color)
                }
            }

            secretLicense.settingClick = {
                WebLauncher().start(this@NewAboutActivity) {
                    title(R.string.secret_license)
                    url(DirectConstants.SECRET_LICENSE_URL)
                    titleTextColor(R.color.title_text_color)
                }
            }

            openLicense.settingClick = {
                LicensesDialog.Builder(this@NewAboutActivity)
                    .setNotices(R.raw.licenses)
                    .build()
                    .show()
            }
        }
    }
}