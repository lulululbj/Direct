package luyao.direct.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Base64
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import de.hdodenhof.circleimageview.CircleImageView
import luyao.direct.R
import luyao.direct.model.MMKVConstants
import luyao.ktx.util.YLog

/**
 * Description:
 * Author: luyao
 * Date: 2022/12/6 16:39
 */
class IconPreference : Preference {

    private var preferenceIcon: CircleImageView? = null


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?)
            : this(context, attrs, android.R.attr.preferenceStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : this(context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        widgetLayoutResource = R.layout.preference_icon
    }


    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        preferenceIcon = holder.itemView.findViewById(R.id.preferenceIcon) as CircleImageView
        if (MMKVConstants.floatWindowIconBase64.isNotBlank()) {
            val iconBytes = Base64.decode(MMKVConstants.floatWindowIconBase64, Base64.DEFAULT)
            getIconView()?.let {
                val bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size)
                it.setImageBitmap(bitmap)
            }
        }
    }

    fun setIcon(bitmap: Bitmap) {
        preferenceIcon?.setImageBitmap(bitmap)
    }

    fun setIconDrawable(drawable: Drawable) {
        preferenceIcon?.setImageDrawable(drawable)
    }

    fun getIconView() = preferenceIcon

}