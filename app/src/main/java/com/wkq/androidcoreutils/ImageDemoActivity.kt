package com.wkq.androidcoreutils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import coil3.size.Scale
import com.wkq.util.coil.CacheManager
import com.wkq.util.coil.ImageLoaderUtil
import com.wkq.util.log.ALog

class ImageDemoActivity : BaseDemoActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var imageOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createMarkdownPage(
            iconText = getString(R.string.badge_image),
            title = getString(R.string.section_image_title),
            description = getString(R.string.image_description),
            body = createBody()
        ))
    }

    private fun createBody(): View {
        val box = verticalBox()
        imagePreview = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(ColorDrawable(Color.rgb(228, 233, 240)))
            background = rounded(Color.rgb(228, 233, 240), dp(8))
            clipToOutline = true
        }
        box.addView(imagePreview, previewRectParams())
        box.addView(buttonGrid(
            action(getString(R.string.image_action_url)) { loadImage(getString(R.string.image_action_url)) },
            action(getString(R.string.image_action_rounded)) { loadImage(getString(R.string.image_action_rounded), radius = dp(18).toFloat()) },
            action(getString(R.string.image_action_circle)) { loadImage(getString(R.string.image_action_circle), circle = true) },
            action(getString(R.string.image_action_grayscale)) { loadImage(getString(R.string.image_action_grayscale), grayscale = true) },
            action(getString(R.string.image_action_gif)) { loadGif() },
            action(getString(R.string.image_action_res)) { loadResImage() },
            action(getString(R.string.image_action_pinned)) { loadPinnedImage() },
            action(getString(R.string.image_action_preload)) {
                ImageLoaderUtil.preload(this, DEMO_IMAGE_URL)
                imageOutput.text = getString(R.string.image_result_preload)
            },
            action(getString(R.string.image_action_clear_cache)) {
                CacheManager.clearAllCache()
                imageOutput.text = getString(R.string.image_result_clear_cache)
            }
        ))
        imageOutput = outputBox(getString(R.string.image_output_waiting))
        box.addView(imageOutput, topMargin())
        box.addView(codeBlock(getString(R.string.code_image)))
        return box
    }

    private fun loadImage(label: String, circle: Boolean = false, grayscale: Boolean = false, radius: Float = 0f) {
        preparePreview(circle = circle, rounded = radius > 0f)
        imageOutput.text = getString(R.string.image_result_loading, label)
        ImageLoaderUtil.load(
            imageView = imagePreview,
            data = DEMO_IMAGE_URL,
            placeholder = ColorDrawable(Color.rgb(228, 233, 240)),
            error = ColorDrawable(Color.rgb(250, 218, 218)),
            isCircle = circle,
            isGrayscale = grayscale,
            radius = radius,
            scale = Scale.FILL,
            onSuccess = {
                imageOutput.text = getString(R.string.image_result_success, label, DEMO_IMAGE_URL)
                ALog.i(TAG, "Image loaded: $label")
            },
            onError = {
                imageOutput.text = getString(R.string.image_result_error, label, it.message.orEmpty())
                ALog.e(TAG, "Image load failed", it)
            }
        )
    }

    private fun loadGif() {
        val label = getString(R.string.image_action_gif)
        preparePreview()
        imageOutput.text = getString(R.string.image_result_loading, label)
        ImageLoaderUtil.loadGif(
            imageView = imagePreview,
            data = DEMO_GIF_URL,
            scale = Scale.FILL,
            onSuccess = { imageOutput.text = getString(R.string.image_result_success, label, DEMO_GIF_URL) },
            onError = { imageOutput.text = getString(R.string.image_result_error, label, it.message.orEmpty()) }
        )
    }

    private fun loadResImage() {
        preparePreview(circle = true)
        ImageLoaderUtil.load(
            imageView = imagePreview,
            data = android.R.drawable.ic_menu_gallery,
            isCircle = true,
            scale = Scale.FIT,
            onSuccess = { imageOutput.text = getString(R.string.image_result_res_success) },
            onError = { imageOutput.text = getString(R.string.image_result_res_error, it.message.orEmpty()) }
        )
    }

    private fun loadPinnedImage() {
        val label = getString(R.string.image_action_pinned)
        preparePreview(rounded = true)
        imageOutput.text = getString(R.string.image_result_loading, label)
        ImageLoaderUtil.loadPinned(imagePreview, DEMO_IMAGE_URL, radius = dp(18).toFloat())
        imageOutput.text = getString(R.string.image_result_pinned, CacheManager.getPinnedDiskDir()?.absolutePath.orEmpty())
    }

    private fun preparePreview(circle: Boolean = false, rounded: Boolean = false) {
        val color = Color.rgb(228, 233, 240)
        if (circle) {
            val size = dp(188)
            imagePreview.layoutParams = LinearLayout.LayoutParams(size, size).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dp(6)
                bottomMargin = dp(4)
            }
            imagePreview.background = rounded(color, size / 2)
        } else {
            imagePreview.layoutParams = previewRectParams()
            imagePreview.background = rounded(color, if (rounded) dp(18) else dp(8))
        }
        imagePreview.clipToOutline = true
        imagePreview.scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private fun previewRectParams(): LinearLayout.LayoutParams {
        return matchFixedHeight(188)
    }
}
