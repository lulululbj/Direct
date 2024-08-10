package luyao.direct.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hjq.toast.Toaster
import luyao.direct.R
import luyao.direct.databinding.DialogItemAppBinding
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.SimpleAppEntity
import luyao.direct.util.loadIcon
import luyao.ktx.ext.toast

/**
 * ================================================
 * Copyright (c) 2020 All rights reserved
 * Description：
 * Author: luyao
 * Date： Date: 2021/12/6
 * ================================================
 */
class AppAdapter(private val appList: MutableList<SimpleAppEntity>) :
    RecyclerView.Adapter<AppAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            DialogItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.run {
            val appEntity = appList[position]
            appEntity.loadIcon(appIcon)
            appName.text = appEntity.appName
            appCheckBox.isChecked = MMKVConstants.iconPack == appEntity.packageName
            appCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    MMKVConstants.iconPack = appEntity.packageName
                    root.post {
                        notifyDataSetChanged()
                    }
                    toast(R.string.effect_next_reboot)
                }
            }
            appRoot.setOnClickListener {
                appCheckBox.isChecked = !appCheckBox.isChecked
            }
        }
    }

    override fun getItemCount() = appList.size

    class ViewHolder(val binding: DialogItemAppBinding) : RecyclerView.ViewHolder(binding.root)
}