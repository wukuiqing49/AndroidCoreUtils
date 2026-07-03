package com.wkq.androidcoreutils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil3.size.Scale
import com.wkq.util.FormatUtil
import com.wkq.util.PhotoPickerHelper
import com.wkq.util.UriUtil
import com.wkq.util.coil.ImageLoaderUtil
import com.wkq.util.log.ALog

class PickerDemoActivity : BaseDemoActivity() {

    private lateinit var pickerHelper: PhotoPickerHelper
    private lateinit var pickerPreview: ImageView
    private lateinit var pickerOutput: TextView
    private var selectedUris: List<Uri> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerHelper = PhotoPickerHelper.with(this).register(maxItems = 9) { uris ->
            handlePickedMedia(uris)
        }
        setContentView(createMarkdownPage(
            iconText = getString(R.string.badge_picker),
            title = getString(R.string.section_picker_title),
            description = getString(R.string.picker_description),
            body = createBody()
        ))
    }

    private fun createBody(): View {
        val box = verticalBox()
        pickerPreview = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageDrawable(ColorDrawable(Color.rgb(228, 233, 240)))
            background = rounded(Color.rgb(228, 233, 240), dp(8))
        }
        box.addView(pickerPreview, matchFixedHeight(188))
        box.addView(buttonGrid(
            action(getString(R.string.picker_action_single_image)) { pickerHelper.launchImage() },
            action(getString(R.string.picker_action_multi_image)) { pickerHelper.launchImages() },
            action(getString(R.string.picker_action_single_video)) { pickerHelper.launchVideo() },
            action(getString(R.string.picker_action_mixed)) { pickerHelper.launchImageAndVideo(isMultiple = true) },
            action(getString(R.string.picker_action_camera)) { pickerHelper.launchCamera() },
            action(getString(R.string.picker_action_video_camera)) { pickerHelper.launchVideoCamera() },
            action(getString(R.string.picker_action_uri_info)) { showSelectedUriInfo() },
            action(getString(R.string.picker_action_uri_to_file)) { convertSelectedUriToFile() }
        ))
        pickerOutput = outputBox(getString(R.string.picker_output_waiting))
        box.addView(pickerOutput, topMargin())
        box.addView(codeBlock(getString(R.string.code_picker)))
        return box
    }

    private fun handlePickedMedia(uris: List<Uri>) {
        selectedUris = uris
        val first = uris.firstOrNull()
        if (first == null) {
            pickerOutput.text = getString(R.string.picker_result_cancelled)
            ALog.w(TAG, "Picker cancelled")
            return
        }
        pickerOutput.text = buildString {
            append(getString(R.string.picker_result_selected_count, uris.size)).append('\n')
            uris.take(5).forEachIndexed { index, uri ->
                append(index + 1).append(". ").append(uri).append('\n')
            }
        }.trimEnd()
        ImageLoaderUtil.load(
            imageView = pickerPreview,
            data = first,
            placeholder = ColorDrawable(Color.rgb(228, 233, 240)),
            error = ColorDrawable(Color.rgb(250, 218, 218)),
            radius = dp(14).toFloat(),
            scale = Scale.FILL
        )
    }

    private fun showSelectedUriInfo() {
        val uri = selectedUris.firstOrNull()
        if (uri == null) {
            pickerOutput.text = getString(R.string.picker_result_select_first)
            return
        }
        pickerOutput.text = getString(
            R.string.picker_result_uri_info,
            UriUtil.getDisplayName(this, uri),
            FormatUtil.formatFileSize(UriUtil.getSize(this, uri)),
            UriUtil.getMimeType(this, uri),
            uri
        )
    }

    private fun convertSelectedUriToFile() {
        val uri = selectedUris.firstOrNull()
        if (uri == null) {
            pickerOutput.text = getString(R.string.picker_result_select_first)
            return
        }
        val file = pickerHelper.uriToFile(uri)
        pickerOutput.text = if (file == null) {
            getString(R.string.picker_result_uri_to_file_failed)
        } else {
            getString(R.string.picker_result_uri_to_file_success, file.absolutePath, FormatUtil.formatFileSize(file.length()))
        }
    }
}
