package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.databinding.ItemUrlBinding
import luyao.direct.model.entity.EmailEntity
import luyao.ktx.ext.sendEmail
import luyao.ktx.ext.toast
import java.util.Locale

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/21 11:12
 */
class EmailViewDelegate : ItemViewDelegate<EmailEntity, EmailViewDelegate.ViewHolder>() {


    class ViewHolder(val binding: ItemUrlBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ViewHolder, item: EmailEntity) {
        holder.binding.run {
            root.setBackgroundResource(R.drawable.bg_single_direct)
            urlTv.text = String.format(
                Locale.getDefault(),
                DirectApp.App.getString(R.string.send_email),
                item.email
            )
            browserIcon.setImageResource(R.drawable.ic_gmail)
            root.setOnClickListener {
                DirectApp.App.sendEmail(item.email) {
                    toast(R.string.not_found_email_client)
                }
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) =
        ViewHolder(ItemUrlBinding.inflate(LayoutInflater.from(context), parent, false))
}
