package luyao.direct.util

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.utils.DisplayUtils
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.tencent.mmkv.MMKV
import de.hdodenhof.circleimageview.CircleImageView
import luyao.direct.DirectApp
import luyao.direct.R
import luyao.direct.model.MMKVConstants
import luyao.direct.model.entity.RecentEntity
import luyao.direct.ui.DirectActivity
import luyao.ktx.ext.*
import luyao.ktx.util.YLog
import kotlin.math.abs

const val LEFT_FLOAT_WINDOW_TAG = "left_float_window"
const val RIGHT_FLOAT_WINDOW_TAG = "right_float_window"
const val LEFT_CIRCLE_FLOAT_WINDOW_TAG = "left_circle_float_window"
const val RIGHT_CIRCLE_FLOAT_WINDOW_TAG = "right_circle_float_window"
const val GESTURE_TAG = "gesture"

@SuppressLint("ClickableViewAccessibility")
fun showFloatView(tag: String, resetLocation: Boolean = false) {
    var startX = 0f
    var startY = 0f
    var startMove = false
    EasyFloat.with(DirectApp.App).apply {
        setTag(tag)
        setImmersionStatusBar(false)
        setDragEnable(true)
        setShowPattern(ShowPattern.ALL_TIME)
        setSidePattern(SidePattern.DEFAULT)
//        setSidePattern(if (tag == LEFT_FLOAT_WINDOW_TAG || tag == LEFT_CIRCLE_FLOAT_WINDOW_TAG) SidePattern.LEFT else SidePattern.RIGHT)
        val layoutRes =
            when (tag) {
                LEFT_CIRCLE_FLOAT_WINDOW_TAG, RIGHT_CIRCLE_FLOAT_WINDOW_TAG -> R.layout.float_window_circle
                LEFT_FLOAT_WINDOW_TAG -> R.layout.float_window
                else -> R.layout.float_window_right
            }
        setLayout(layoutRes) {
            if (MMKVConstants.defaultSideBarShape == 0) {
                it.findViewById<View>(R.id.barView)?.setBackgroundColor(MMKVConstants.sideBarColor)
            }

            if (tag == LEFT_CIRCLE_FLOAT_WINDOW_TAG || tag == RIGHT_CIRCLE_FLOAT_WINDOW_TAG) {
                val root = it.findViewById<FrameLayout>(R.id.floatRoot)
                root.updateLayoutParams<FrameLayout.LayoutParams> {
                    width = dp2px(MMKVConstants.sideBarSize).toInt()
                    height = dp2px(MMKVConstants.sideBarSize).toInt()
                }
                it.findViewById<CircleImageView>(R.id.circleView).run {
                    setFloatIcon(this)
                }
            }

            it.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_DOWN -> {
                            setDragEnable(false)
                            startMove = true
                            startX = event.rawX
                            startY = event.rawY
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val currentX = event.rawX
                            val currentY = event.rawY
                            val direction = detectDirection(startX, startY, currentX, currentY)
                            if (direction.distance > ViewConfiguration.get(DirectApp.App).scaledTouchSlop && startMove) {
                                startMove = false
                                if (MMKVConstants.gestureVibrate) {
                                    DirectApp.App.vibrate()
                                }
                                when (direction.direction) {
                                    Direction.TOP -> {
                                        handleGesture(Direction.TOP)
                                    }
                                    Direction.BOTTOM -> {
                                        handleGesture(Direction.BOTTOM)
                                    }
                                    Direction.LEFT -> {
                                        handleGesture(Direction.LEFT)
                                    }
                                    Direction.RIGHT -> {
                                        handleGesture(Direction.RIGHT)
                                    }
                                }
                                YLog.e("moveDirection: $direction ${ViewConfiguration.get(DirectApp.App).scaledTouchSlop}")
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            if (startMove) {
                                DirectApp.App.openApp(DirectApp.App.packageName)
                            }
                            startMove = false
                        }

                        MotionEvent.ACTION_CANCEL -> {
                            startMove = false
                        }
                    }
                    return false
                }

                private fun handleGesture(gesture: Direction) {
                    val json = MMKV.defaultMMKV()
                        .getString("gesture_${gesture.name}", "")
                    if (json.isNullOrEmpty()) {
//                        DirectApp.App.toast(DirectApp.App.getString(R.string.no_gesture))
                    } else {
                        val moshi = Moshi.Builder().build()
                        val jsonAdapter: JsonAdapter<RecentEntity> =
                            moshi.adapter(RecentEntity::class.java)
                        jsonAdapter.fromJson(json)?.go()
                    }
                }
            })

            it.setOnLongClickListener {
                if (startMove) {
                    YLog.e("longClick")
                    startMove = false
                    if (MMKVConstants.gestureVibrate) {
                        DirectApp.App.vibrate(200)
                    }
                    EasyFloat.dragEnable(true, tag)
                }
                true
            }
        }
        registerCallback {
            dragEnd {
                val location = intArrayOf(0, 0)
                it.getLocationOnScreen(location)
                MMKVConstants.sideBarX = location[0]
                MMKVConstants.sideBarY = location[1] - DisplayUtils.statusBarHeight(it)
            }
        }
        setFilter(DirectActivity::class.java)

        if (!resetLocation) {
            setLocation(MMKVConstants.sideBarX, MMKVConstants.sideBarY)
        } else {
            setGravity(if (tag == LEFT_FLOAT_WINDOW_TAG || tag == LEFT_CIRCLE_FLOAT_WINDOW_TAG) Gravity.START else Gravity.END)
        }

        show()
    }


//
//        val params = WindowManager.LayoutParams().apply {
//            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else {
//                WindowManager.LayoutParams.TYPE_PHONE
//            }
//            gravity = Gravity.START or Gravity.TOP
//            width = 8.px.toInt()
//            height = 70.px.toInt()
//            x = 0
//            y = 50.px.toInt()
//            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//            format = PixelFormat.RGBA_8888
//        }
//
//        val wm = requireActivity().getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        wm.addView(button, params)


}

fun hideFloatView(tag: String) {
    EasyFloat.dismiss(tag)
}

fun updateFloatView(tag: String) {
    EasyFloat.getFloatView(tag)?.run {
        findViewById<View>(R.id.barView)?.setBackgroundColor(MMKVConstants.sideBarColor)
    }
}

fun updateFloatSize(tag: String, width: Int, height: Int) {
    EasyFloat.getFloatView(tag)?.run {
        val root = findViewById<FrameLayout>(R.id.floatRoot)
        root.updateLayoutParams<FrameLayout.LayoutParams> {
            this.width = width
            this.height = height
        }
    }
    EasyFloat.updateFloat(tag, MMKVConstants.sideBarX, MMKVConstants.sideBarY, width, height)
}

fun getFloatTag(): String {
    return if (MMKVConstants.defaultSideBarShape == 1) {
        if (MMKVConstants.defaultSideBarLocation == 0) LEFT_CIRCLE_FLOAT_WINDOW_TAG else RIGHT_CIRCLE_FLOAT_WINDOW_TAG
    } else {
        if (MMKVConstants.defaultSideBarLocation == 0) LEFT_FLOAT_WINDOW_TAG else RIGHT_FLOAT_WINDOW_TAG
    }
}

// 1,2,3,4对应上下左右
private fun detectDirection(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float
): SlideDirection {
    val xDistance = abs(startX - endX)
    val yDistance = abs(startY - endY)
    val isLeftOrRight = xDistance > yDistance
    return if (isLeftOrRight) {
        SlideDirection(if (startX - endX >= 0) Direction.LEFT else Direction.RIGHT, xDistance)
    } else {
        SlideDirection(if (startY - endY >= 0) Direction.TOP else Direction.BOTTOM, yDistance)
    }
}

enum class Direction {
    TOP, BOTTOM, LEFT, RIGHT
}

data class SlideDirection(val direction: Direction, val distance: Float)

fun updateFloatIcon(tag: String) {
    if (tag == LEFT_FLOAT_WINDOW_TAG || tag == RIGHT_FLOAT_WINDOW_TAG) return
    EasyFloat.getFloatView(tag)?.run {
        findViewById<CircleImageView>(R.id.circleView)?.run {
            setFloatIcon(this)
        }
    }
}

private fun setFloatIcon(view: CircleImageView) {
    if (MMKVConstants.floatWindowIconBase64.isNotBlank()) {
        val iconBytes = Base64.decode(MMKVConstants.floatWindowIconBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size)
        view.setImageBitmap(bitmap)
    }
}
