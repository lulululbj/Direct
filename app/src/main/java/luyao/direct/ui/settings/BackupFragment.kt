package luyao.direct.ui.settings

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import dagger.hilt.android.AndroidEntryPoint
import luyao.direct.R
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.NewBackupEntity
import luyao.direct.util.WebDavUtil
import luyao.direct.view.WebDavDialog
import luyao.direct.vm.BackupViewModel
import luyao.ktx.ext.showConfirmDialog
import luyao.ktx.util.toFormatString
import java.util.*

/**
 * author: luyao
 * date:   2021/10/14 10:56
 */
@AndroidEntryPoint
class BackupFragment : PreferenceFragmentCompat() {

    private lateinit var snackBar: SnackProgressBar
    private lateinit var snackBarManager: SnackProgressBarManager
    private val vm by viewModels<BackupViewModel>()
    private lateinit var connectPreference: Preference
    private lateinit var backupCloudPreference: Preference
    private lateinit var backupLocalPreference: Preference

    private val openDocument =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                vm.restoreLocal(it)
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.backup_preferences, rootKey)
        setListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackBarManager = SnackProgressBarManager(requireView(), this)
        snackBar = SnackProgressBar(
            SnackProgressBar.TYPE_HORIZONTAL,
            getString(R.string.backingup_to_cloud)
        )
            .setIsIndeterminate(true).setAllowUserInput(true)
        startObserve()
        vm.checkWebDavConnect()
    }

    private fun setListener() {
        connectPreference = findPreference("connectStatus")!!
        connectPreference.setOnPreferenceClickListener {
            showWebDevDialog()
            true
        }

        backupCloudPreference = findPreference<Preference>("backupCloud")!!.apply {
            val time = MMKVConstants.lastBackupCloud
            if (time > 0) {
                summary = String.format(
                    Locale.getDefault(), getString(R.string.last_backup_time),
                    time.toFormatString()
                )
            }
        }
        backupCloudPreference.setOnPreferenceClickListener {
            if (WebDavUtil.checkConfig()) {
                vm.backupCloud()
            } else {
                Snackbar.make(requireView(), R.string.please_config_webdav, Snackbar.LENGTH_LONG)
                    .setAction(R.string.go_to_config) {
                        showWebDevDialog()
                    }.show()
            }
            true
        }

        findPreference<Preference>("restoreCloud")?.setOnPreferenceClickListener {
            if (WebDavUtil.checkConfig()) {
                vm.checkCloudData()
            } else {
                Snackbar.make(requireView(), R.string.please_config_webdav, Snackbar.LENGTH_LONG)
                    .setAction(R.string.go_to_config) {
                        showWebDevDialog()
                    }.show()
            }
            true
        }

        backupLocalPreference = findPreference<Preference>("backupLocal")!!.apply {
            val time = MMKVConstants.lastBackupLocal
            if (time > 0) {
                summary = String.format(
                    Locale.getDefault(), getString(R.string.last_backup_time),
                    time.toFormatString()
                )
            }
        }
        backupLocalPreference.setOnPreferenceClickListener {
            showConfirmDialog(
                title = getString(R.string.note),
                message = getString(R.string.backup_save_note),
                onConfirm = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vm.backupLocal()
                    } else {
                        PermissionX.init(this@BackupFragment)
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .onExplainRequestReason { scope, deniedList ->
                                scope.showRequestReasonDialog(
                                    deniedList,
                                    getString(R.string.write_backup_note),
                                    getString(R.string.permission_again),
                                    getString(R.string.permission_no_need)
                                )
                            }
                            .onForwardToSettings { scope, deniedList ->
                                scope.showForwardToSettingsDialog(
                                    deniedList,
                                    getString(R.string.never_ask_note),
                                    getString(R.string.go_to_setting),
                                    getString(R.string.suan_le)
                                )
                            }
                            .request { allGranted, _, _ ->
                                if (allGranted) {
                                    vm.backupLocal()
                                }
                            }
                    }
                }
            )
//            MaterialDialog(requireActivity()).show {
//                title(R.string.note)
//                message(R.string.backup_save_note)
//                positiveButton(R.string.Confirm) {
//
//                }
//            }

            true
        }

        findPreference<Preference>("restoreLocal")?.setOnPreferenceClickListener {
            showConfirmDialog(
                title = getString(R.string.note),
                message = getString(R.string.please_select_local_backup_file),
                onConfirm = { openDocument.launch(arrayOf("*/*")) }
            )
//            MaterialDialog(requireActivity()).show {
//                title(R.string.note)
//                message(text = getString(R.string.please_select_local_backup_file))
//                positiveButton(R.string.Confirm) {
//                    openDocument.launch(arrayOf("*/*"))
//                }
//            }
            true
        }


    }

    private fun startObserve() {
        vm.connectStatus.observe(viewLifecycleOwner) {
            connectPreference.title =
                if (it) getString(R.string.connected) else getString(R.string.connect_status)
        }

        vm.backupResult.observe(viewLifecycleOwner) {
            snackBarManager.dismiss()
            Snackbar.make(
                requireView(),
                if (it.isSuccess) it.getOrNull() ?: "" else it.exceptionOrNull()?.message ?: "",
                Snackbar.LENGTH_SHORT
            ).show()
            refreshSummary()
        }

        vm.restoreResult.observe(viewLifecycleOwner) {
            snackBarManager.dismiss()
            if (it.isSuccess) {
                it.getOrNull()?.let { entity -> showRestoreDialog(entity) }
            } else if (it.isFailure) {
                Snackbar.make(
                    requireView(),
                    it.exceptionOrNull()?.message ?: "",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

        vm.progressLabel.observe(viewLifecycleOwner) {
            snackBar.setMessage(it)
            snackBarManager.show(
                snackBar,
                SnackProgressBarManager.LENGTH_INDEFINITE
            )
        }
    }

    private fun refreshSummary() {
        val time = MMKVConstants.lastBackupCloud
        if (time > 0) {
            backupCloudPreference.summary = String.format(
                Locale.getDefault(), getString(R.string.last_backup_time),
                time.toFormatString()
            )
        }
        val backupLocaltime = MMKVConstants.lastBackupLocal
        if (time > 0) {
            backupLocalPreference.summary = String.format(
                Locale.getDefault(), getString(R.string.last_backup_time),
                backupLocaltime.toFormatString()
            )
        }
    }

    private fun showRestoreDialog(entity: NewBackupEntity) {
        showConfirmDialog(
            title = getString(R.string.note),
            message = String.format(
                Locale.getDefault(), getString(R.string.restore_note),
                entity.time.toFormatString(), entity.appVersion
            ),
            onConfirm = { vm.restoreData(entity) }
        )
//        MaterialDialog(requireActivity()).show {
//            title(R.string.note)
//            message(
//                text = String.format(
//                    Locale.getDefault(), getString(R.string.restore_note),
//                    entity.time.toFormatString(), entity.appVersion
//                )
//            )
//            positiveButton(R.string.Confirm) {
//                vm.restoreData(entity)
//            }
//            negativeButton(R.string.Cancel)
//        }
    }

    private fun showWebDevDialog() {
        WebDavDialog(requireActivity()).apply {
            setCheckStatusCallback {
                if (it) {
                    connectPreference.setTitle(R.string.connected)
                } else {
                    connectPreference.setTitle(R.string.connect_status)
                }
            }
        }.show()
    }
}