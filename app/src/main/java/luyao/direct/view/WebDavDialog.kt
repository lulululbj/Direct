package luyao.direct.view

import android.app.Dialog
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import luyao.direct.R
import luyao.direct.databinding.DialogWebdavBinding
import luyao.direct.model.DirectConstants
import luyao.direct.model.MMKVConstants
import luyao.direct.util.WebDavUtil
import luyao.ktx.ext.openBrowser

/**
 * author: luyao
 * date:   2021/10/14 16:22
 */
class WebDavDialog(private val activity: ComponentActivity) :
    Dialog(activity) {

    private var checkStatusCallback: ((Boolean) -> Unit)? = null

    fun setCheckStatusCallback(callback: (Boolean) -> Unit) {
        checkStatusCallback = callback
    }


    init {
        val binding = DialogWebdavBinding.inflate(layoutInflater)
        initView(binding)
        setContentView(binding.root)

        window?.attributes?.width = MATCH_PARENT
    }

    private fun initView(binding: DialogWebdavBinding) {
        binding.run {
            webDavServer.setText(MMKVConstants.webDavServer)
            webDavUserName.setText(MMKVConstants.webDavUserName)
            webDavPassword.setText(MMKVConstants.webDavPassword)
            webDavHelp.setOnClickListener {
                activity.openBrowser(DirectConstants.WEBDAV_HELP_URL)
            }

            webDavConfirm.setOnClickListener {

                activity.lifecycleScope.launch {

                    if (webDavServer.text.isNullOrBlank()) {
                        Toast.makeText(context, R.string.input_webdav_server, Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    }

                    if (webDavUserName.text.isNullOrBlank()) {
                        Toast.makeText(context, R.string.input_webdav_username, Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    }

                    if (webDavPassword.text.isNullOrBlank()) {
                        Toast.makeText(context, R.string.input_webdav_password, Toast.LENGTH_SHORT)
                            .show()
                        return@launch
                    }

                    webDavConfirm.visibility = View.INVISIBLE
                    webDavProgress.visibility = View.VISIBLE
                    val deferred = activity.lifecycleScope.async(Dispatchers.IO) {
                        WebDavUtil.checkWebDavSetting(
                            webDavServer.text.toString(),
                            webDavUserName.text.toString(),
                            webDavPassword.text.toString()
                        )
                    }

                    if (deferred.await()) {
                        checkStatusCallback?.invoke(true)
                        MMKVConstants.webDavServer = webDavServer.text.toString()
                        MMKVConstants.webDavUserName = webDavUserName.text.toString()
                        MMKVConstants.webDavPassword = webDavPassword.text.toString()
                        Toast.makeText(context, R.string.connect_success, Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        checkStatusCallback?.invoke(false)
                        Toast.makeText(context, R.string.connect_faile, Toast.LENGTH_SHORT).show()
                    }

                    webDavConfirm.visibility = View.VISIBLE
                    webDavProgress.visibility = View.INVISIBLE
                }
            }


        }
    }
}