package luyao.direct.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.ext.getFocusString
import luyao.direct.model.dao.saveSearchHistory
import luyao.direct.model.entity.ContactEntity
import luyao.direct.model.entity.SearchHistoryEntity
import luyao.direct.view.getProperDirectDrawable
import luyao.ktx.ext.hideKeyboard

/**
 *  @author: luyao
 * @date: 2021/7/13 下午11:15
 */
class ContactViewDelegate : ItemViewDelegate<ContactEntity, ContactViewDelegate.ViewHolder>() {

    private var keyWord = ""
    private var callPhoneAction: ((String) -> Unit)? = null
    private var sendSmsAction: ((String) -> Unit)? = null

    fun setKeyWord(toString: String) {
        keyWord = toString
    }

    fun setAction(phoneAction: ((String) -> Unit)? = null, smsAction: ((String) -> Unit)? = null) {
        callPhoneAction = phoneAction
        sendSmsAction = smsAction
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: ContactEntity) {
        holder.contactRoot.setBackgroundResource(getProperDirectDrawable(item.isFirst, item.isLast))
//        holder.contactName.setTextColor(DirectApp.App.getProperTextColor())
        holder.contactName.text = getFocusString(item.displayName, keyWord)
        holder.contactNumber.text = getFocusString(item.phoneNumber, keyWord)
        holder.contactRoot.setOnClickListener {
            callPhoneAction?.invoke(item.phoneNumber)
            saveSearchHistory(keyWord, SearchHistoryEntity.CONTACT)
        }
        holder.callPhone.setOnClickListener {
            DirectApp.App.hideKeyboard(it)
            callPhoneAction?.invoke(item.phoneNumber)
            saveSearchHistory(keyWord, SearchHistoryEntity.CONTACT)
        }
        holder.sendSms.setOnClickListener {
            DirectApp.App.hideKeyboard(it)
            sendSmsAction?.invoke(item.phoneNumber)
            saveSearchHistory(keyWord, SearchHistoryEntity.CONTACT)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contactName)
        val contactNumber: TextView = itemView.findViewById(R.id.contactNumber)
        val contactRoot: ConstraintLayout = itemView.findViewById(R.id.contactRoot)
        val callPhone: ImageView = itemView.findViewById(R.id.callPhone)
        val sendSms: ImageView = itemView.findViewById(R.id.sendSms)
//        val firstTag : TextView = itemView.findViewById(R.id.firstTag)
    }


}