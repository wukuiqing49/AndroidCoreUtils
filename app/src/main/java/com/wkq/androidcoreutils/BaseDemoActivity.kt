package com.wkq.androidcoreutils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wkq.util.CoreUtils
import com.wkq.util.CoreUtilsConfig

abstract class BaseDemoActivity : AppCompatActivity() {

    protected data class DemoAction(val title: String, val onClick: () -> Unit)

    private val pageBg = Color.rgb(244, 247, 251)
    private val ink = Color.rgb(28, 36, 50)
    private val muted = Color.rgb(96, 110, 130)
    private val border = Color.rgb(213, 222, 234)
    private val brand = Color.rgb(31, 99, 190)
    private val brandDark = Color.rgb(21, 52, 96)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ensureCoreUtils()
    }

    private fun ensureCoreUtils() {
        CoreUtils.init(
            applicationContext,
            CoreUtilsConfig(
                initLog = true,
                debug = true,
                logToFile = true,
                showLogStackInfo = true,
                fileProviderAuthority = "${packageName}.fileprovider"
            )
        )
    }

    protected fun createMarkdownPage(
        iconText: String,
        title: String,
        description: String,
        body: View
    ): View {
        val root = ScrollView(this).apply {
            setBackgroundColor(pageBg)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            clipToPadding = false
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(28))
        }
        root.addView(content)

        content.addView(pageHeader(iconText, title, description))
        content.addView(contentPanel(body))
        return root
    }

    protected fun action(title: String, onClick: () -> Unit): DemoAction = DemoAction(title, onClick)

    protected fun buttonGrid(vararg actions: DemoAction): View {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        actions.toList().chunked(2).forEach { rowActions ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            rowActions.forEach { item ->
                row.addView(actionButton(item.title, item.onClick))
            }
            if (rowActions.size == 1) {
                row.addView(FrameLayout(this), LinearLayout.LayoutParams(0, dp(46), 1f))
            }
            container.addView(row, matchWrap().apply { topMargin = dp(10) })
        }
        return container
    }

    protected fun outputBox(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13f
            typeface = Typeface.MONOSPACE
            setLineSpacing(dp(2).toFloat(), 1f)
            setTextColor(Color.rgb(39, 50, 65))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = roundedStroke(Color.rgb(248, 251, 255), Color.rgb(196, 215, 238), dp(8))
        }
    }

    protected fun codeBlock(code: String): TextView {
        return TextView(this).apply {
            text = code
            textSize = 12.5f
            typeface = Typeface.MONOSPACE
            setLineSpacing(dp(2).toFloat(), 1f)
            setTextColor(Color.rgb(223, 231, 241))
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = rounded(Color.rgb(28, 36, 50), dp(8))
        }.also {
            it.layoutParams = matchWrap().apply { topMargin = dp(14) }
        }
    }

    protected fun paragraph(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setLineSpacing(dp(3).toFloat(), 1f)
            setTextColor(muted)
            setPadding(0, dp(4), 0, dp(10))
        }
    }

    protected fun markdownLine(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13.5f
            setLineSpacing(dp(2).toFloat(), 1f)
            setTextColor(Color.rgb(74, 88, 108))
            setPadding(dp(12), dp(8), dp(12), dp(8))
            background = roundedStroke(Color.rgb(250, 252, 255), Color.rgb(229, 235, 244), dp(8))
        }.also {
            it.layoutParams = matchWrap().apply { topMargin = dp(8) }
        }
    }

    protected fun verticalBox(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
    }

    protected fun topMargin(): LinearLayout.LayoutParams {
        return matchWrap().apply { topMargin = dp(12) }
    }

    protected fun matchWrap(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    protected fun matchFixedHeight(height: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        ).apply {
            topMargin = dp(6)
            bottomMargin = dp(4)
        }
    }

    protected fun rounded(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }
    }

    protected fun roundedStroke(color: Int, strokeColor: Int, radius: Int): GradientDrawable {
        return rounded(color, radius).apply {
            setStroke(dp(1), strokeColor)
        }
    }

    protected fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density + 0.5f).toInt()
    }

    private fun contentPanel(body: View): View {
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(16))
            background = roundedStroke(Color.WHITE, border, dp(8))
        }
        wrapper.addView(body)
        return wrapper
    }

    private fun pageHeader(iconText: String, title: String, description: String): View {
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(18))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.rgb(255, 255, 255), Color.rgb(232, 241, 255))
            ).apply {
                cornerRadius = dp(8).toFloat()
                setStroke(dp(1), Color.rgb(203, 218, 238))
            }
        }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        top.addView(iconTile(iconText))
        top.addView(TextView(this).apply {
            text = title
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(ink)
            setPadding(dp(12), 0, 0, 0)
        }, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        header.addView(top)
        header.addView(TextView(this).apply {
            text = description
            textSize = 14.5f
            setLineSpacing(dp(3).toFloat(), 1f)
            setTextColor(muted)
            setPadding(0, dp(10), 0, 0)
        })
        return header.also {
            it.layoutParams = matchWrap().apply { bottomMargin = dp(14) }
        }
    }

    private fun actionButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            textSize = 13f
            isAllCaps = false
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(brandDark)
            background = roundedStroke(Color.rgb(241, 247, 255), Color.rgb(174, 203, 238), dp(8))
            setOnClickListener { onClick() }
            minHeight = 0
            minimumHeight = 0
            minWidth = 0
            minimumWidth = 0
            setPadding(dp(8), 0, dp(8), 0)
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(0, dp(46), 1f).apply {
                rightMargin = dp(8)
            }
        }
    }

    private fun iconTile(text: String): View {
        val container = FrameLayout(this).apply {
            background = rounded(accentColor(text), dp(8))
            layoutParams = LinearLayout.LayoutParams(dp(44), dp(44))
        }
        container.addView(ImageView(this).apply {
            setImageResource(iconResource(text))
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            layoutParams = FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER)
        })
        return container
    }

    private fun iconResource(text: String): Int {
        return when (text) {
            "HOME" -> android.R.drawable.ic_menu_manage
            "LOG" -> android.R.drawable.ic_menu_info_details
            "IMG" -> android.R.drawable.ic_menu_gallery
            "PICK" -> android.R.drawable.ic_menu_upload
            "SHARE" -> android.R.drawable.ic_menu_share
            "SP" -> android.R.drawable.ic_menu_save
            else -> android.R.drawable.ic_menu_help
        }
    }

    private fun accentColor(text: String): Int {
        return when (text) {
            "HOME" -> Color.rgb(46, 58, 89)
            "LOG" -> Color.rgb(24, 119, 132)
            "IMG" -> Color.rgb(31, 99, 190)
            "PICK" -> Color.rgb(112, 80, 159)
            "SHARE" -> Color.rgb(39, 128, 86)
            "SP" -> Color.rgb(173, 95, 31)
            else -> brand
        }
    }

    private fun divider(): View {
        return View(this).apply {
            setBackgroundColor(Color.rgb(224, 231, 241))
        }
    }

    companion object {
        const val TAG = "Demo"
        const val KEY_DEMO_TEXT = "demo_text"
        const val DEMO_IMAGE_URL = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200&q=80"
        const val DEMO_GIF_URL = "https://media.giphy.com/media/ICOgUNjpvO0PC/giphy.gif"
    }
}
