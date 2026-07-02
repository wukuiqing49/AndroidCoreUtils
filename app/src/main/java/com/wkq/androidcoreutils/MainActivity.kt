package com.wkq.androidcoreutils

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil3.size.Scale
import com.wkq.util.CoreUtils
import com.wkq.util.CoreUtilsConfig
import com.wkq.util.PhotoPickerHelper
import com.wkq.util.PickMediaType
import com.wkq.util.ShareUtil
import com.wkq.util.SpUtils
import com.wkq.util.coil.ImageLoaderUtil
import com.wkq.util.log.ALog
import com.wkq.util.showToast

class MainActivity : AppCompatActivity() {

    private lateinit var pickerHelper: PhotoPickerHelper
    private lateinit var logOutput: TextView
    private lateinit var imageStatus: TextView
    private lateinit var selectedImageStatus: TextView
    private lateinit var previewImage: ImageView
    private lateinit var selectedImage: ImageView
    private lateinit var spInput: EditText
    private lateinit var spOutput: TextView

    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoreUtils.init(
            applicationContext,
            CoreUtilsConfig(
                initLog = true,
                debug = true,
                logToFile = true,
                showLogStackInfo = true
            )
        )

        pickerHelper = PhotoPickerHelper.with(this).register(maxItems = 1) { uris ->
            handlePickedImage(uris.firstOrNull())
        }

        setContentView(createContentView())
        refreshSpValue()
        ALog.d(TAG, "Demo page created")
        appendLog("Demo page created")
    }

    private fun createContentView(): View {
        val root = ScrollView(this).apply {
            setBackgroundColor(Color.rgb(246, 247, 249))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(22), dp(18), dp(28))
        }
        root.addView(content)

        content.addView(TextView(this).apply {
            text = "AndroidCoreUtils Demo"
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(26, 30, 36))
        })

        content.addView(TextView(this).apply {
            text = "Log / Image / Picker / SP / Share"
            textSize = 14f
            setTextColor(Color.rgb(90, 98, 110))
            setPadding(0, dp(4), 0, dp(10))
        })

        content.addView(section("日志", createLogSection()))
        content.addView(section("图片加载", createImageLoadSection()))
        content.addView(section("选择图片", createPickImageSection()))
        content.addView(section("SP 存储", createSpSection()))
        content.addView(section("分享", createShareSection()))

        return root
    }

    private fun createLogSection(): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val row = buttonRow()
        row.addView(actionButton("Debug") {
            ALog.d(TAG, "Debug log from demo")
            appendLog("D/$TAG: Debug log from demo")
        })
        row.addView(actionButton("Info") {
            ALog.i(TAG, "Info log from demo")
            appendLog("I/$TAG: Info log from demo")
        })
        row.addView(actionButton("Error") {
            ALog.e(TAG, "Error log from demo")
            appendLog("E/$TAG: Error log from demo")
        })
        box.addView(row)

        logOutput = TextView(this).apply {
            text = "日志输出会显示在这里"
            textSize = 13f
            setTextColor(Color.rgb(42, 49, 59))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = rounded(Color.rgb(239, 242, 246), dp(8))
        }
        box.addView(logOutput, matchWrap().apply { topMargin = dp(10) })

        box.addView(TextView(this).apply {
            text = "文件: ${ALog.getCurrentLogFile()?.absolutePath.orEmpty()}"
            textSize = 12f
            setTextColor(Color.rgb(100, 108, 120))
            setPadding(0, dp(8), 0, 0)
        })
        return box
    }

    private fun createImageLoadSection(): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        previewImage = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(ColorDrawable(Color.rgb(225, 231, 238)))
            background = rounded(Color.rgb(225, 231, 238), dp(8))
        }
        box.addView(previewImage, matchFixedHeight(180))

        imageStatus = TextView(this).apply {
            text = "未加载"
            textSize = 13f
            setTextColor(Color.rgb(90, 98, 110))
            setPadding(0, dp(8), 0, 0)
        }
        box.addView(imageStatus)

        val row = buttonRow()
        row.addView(actionButton("加载网络图") {
            loadDemoImage()
        })
        row.addView(actionButton("圆角灰度") {
            loadDemoImage(grayscale = true)
        })
        box.addView(row, matchWrap().apply { topMargin = dp(10) })
        return box
    }

    private fun createPickImageSection(): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        selectedImage = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(ColorDrawable(Color.rgb(225, 231, 238)))
            background = rounded(Color.rgb(225, 231, 238), dp(8))
        }
        box.addView(selectedImage, matchFixedHeight(180))

        selectedImageStatus = TextView(this).apply {
            text = "未选择"
            textSize = 13f
            setTextColor(Color.rgb(90, 98, 110))
            setPadding(0, dp(8), 0, 0)
        }
        box.addView(selectedImageStatus)

        val row = buttonRow()
        row.addView(actionButton("打开相册") {
            pickerHelper.launch(PickMediaType.IMAGE_ONLY)
        })
        row.addView(actionButton("分享图片") {
            shareSelectedImage()
        })
        box.addView(row, matchWrap().apply { topMargin = dp(10) })
        return box
    }

    private fun createSpSection(): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        spInput = EditText(this).apply {
            hint = "输入要保存的内容"
            textSize = 15f
            setSingleLine()
            setPadding(dp(12), 0, dp(12), 0)
            background = roundedStroke(Color.WHITE, Color.rgb(210, 216, 224), dp(8))
        }
        box.addView(spInput, matchFixedHeight(48))

        val row = buttonRow()
        row.addView(actionButton("保存") {
            val value = spInput.text?.toString().orEmpty()
            SpUtils.put(KEY_DEMO_TEXT, value)
            ALog.i(TAG, "Saved SP value: $value")
            refreshSpValue()
            showToast("已保存")
        })
        row.addView(actionButton("读取") {
            refreshSpValue()
            showToast("已读取")
        })
        row.addView(actionButton("清除") {
            SpUtils.remove(KEY_DEMO_TEXT)
            spInput.setText("")
            refreshSpValue()
            showToast("已清除")
        })
        box.addView(row, matchWrap().apply { topMargin = dp(10) })

        spOutput = TextView(this).apply {
            textSize = 13f
            setTextColor(Color.rgb(42, 49, 59))
            setPadding(dp(12), dp(12), dp(12), dp(12))
            background = rounded(Color.rgb(239, 242, 246), dp(8))
        }
        box.addView(spOutput, matchWrap().apply { topMargin = dp(10) })
        return box
    }

    private fun createShareSection(): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val row = buttonRow()
        row.addView(actionButton("分享文本") {
            val text = SpUtils.getString(KEY_DEMO_TEXT, "AndroidCoreUtils demo")
            val ok = ShareUtil.shareText(this, text, "分享 Demo 文本")
            ALog.i(TAG, "Share text result: $ok")
        })
        row.addView(actionButton("分享日志路径") {
            val path = ALog.getCurrentLogFile()?.absolutePath ?: "No log file"
            val ok = ShareUtil.shareText(this, path, "分享日志路径")
            ALog.i(TAG, "Share log path result: $ok")
        })
        box.addView(row)

        box.addView(TextView(this).apply {
            text = "当前日志与 SP 内容可直接分享"
            textSize = 13f
            setTextColor(Color.rgb(90, 98, 110))
            setPadding(0, dp(8), 0, 0)
        })
        return box
    }

    private fun loadDemoImage(grayscale: Boolean = false) {
        imageStatus.text = "加载中..."
        ALog.d(TAG, "Start loading demo image, grayscale=$grayscale")
        ImageLoaderUtil.load(
            imageView = previewImage,
            data = DEMO_IMAGE_URL,
            placeholder = ColorDrawable(Color.rgb(225, 231, 238)),
            error = ColorDrawable(Color.rgb(250, 218, 218)),
            isGrayscale = grayscale,
            radius = dp(12).toFloat(),
            scale = Scale.FILL,
            onSuccess = {
                imageStatus.text = "加载成功: $DEMO_IMAGE_URL"
                ALog.i(TAG, "Demo image loaded")
            },
            onError = {
                imageStatus.text = "加载失败: ${it.message.orEmpty()}"
                ALog.e(TAG, "Demo image load failed: ${it.message}")
            }
        )
    }

    private fun handlePickedImage(uri: Uri?) {
        selectedImageUri = uri
        if (uri == null) {
            selectedImageStatus.text = "未选择"
            ALog.w(TAG, "Image pick cancelled")
            return
        }

        selectedImageStatus.text = uri.toString()
        ALog.i(TAG, "Picked image: $uri")
        ImageLoaderUtil.load(
            imageView = selectedImage,
            data = uri,
            placeholder = ColorDrawable(Color.rgb(225, 231, 238)),
            error = ColorDrawable(Color.rgb(250, 218, 218)),
            radius = dp(12).toFloat(),
            scale = Scale.FILL
        )
    }

    private fun shareSelectedImage() {
        val uri = selectedImageUri
        if (uri == null) {
            showToast("请先选择图片")
            return
        }
        val ok = ShareUtil.shareFile(this, uri, "image/*", "分享 Demo 图片")
        ALog.i(TAG, "Share selected image result: $ok")
    }

    private fun refreshSpValue() {
        val value = SpUtils.getString(KEY_DEMO_TEXT, "")
        spOutput.text = if (value.isBlank()) {
            "当前没有保存内容"
        } else {
            "已保存: $value"
        }
    }

    private fun appendLog(line: String) {
        val old = logOutput.text?.toString().orEmpty()
        logOutput.text = if (old == "日志输出会显示在这里") {
            line
        } else {
            "$old\n$line"
        }
    }

    private fun section(title: String, body: View): View {
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(Color.WHITE, dp(8))
            setPadding(dp(14), dp(14), dp(14), dp(14))
        }

        wrapper.addView(TextView(this).apply {
            text = title
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(26, 30, 36))
        })
        wrapper.addView(body, matchWrap().apply { topMargin = dp(12) })

        return FrameLayout(this).apply {
            setPadding(0, dp(12), 0, 0)
            addView(wrapper, matchWrap())
        }
    }

    private fun buttonRow(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    private fun actionButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background = rounded(Color.rgb(35, 99, 235), dp(8))
            setOnClickListener { onClick() }
            minHeight = 0
            minimumHeight = 0
            minWidth = 0
            minimumWidth = 0
            setPadding(dp(12), 0, dp(12), 0)
            layoutParams = LinearLayout.LayoutParams(0, dp(42), 1f).apply {
                rightMargin = dp(8)
            }
        }
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()
        }
    }

    private fun roundedStroke(color: Int, strokeColor: Int, radius: Int): GradientDrawable {
        return rounded(color, radius).apply {
            setStroke(dp(1), strokeColor)
        }
    }

    private fun matchWrap(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun matchFixedHeight(height: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        )
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density + 0.5f).toInt()
    }

    companion object {
        private const val TAG = "Demo"
        private const val KEY_DEMO_TEXT = "demo_text"
        private const val DEMO_IMAGE_URL = "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?w=1200&q=80"
    }
}
