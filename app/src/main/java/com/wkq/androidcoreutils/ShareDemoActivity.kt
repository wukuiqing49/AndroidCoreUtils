package com.wkq.androidcoreutils

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.wkq.util.FileUtil
import com.wkq.util.PhotoPickerHelper
import com.wkq.util.ShareUtil
import com.wkq.util.log.ALog
import com.wkq.util.showToast
import java.io.File

class ShareDemoActivity : BaseDemoActivity() {

    private lateinit var pickerHelper: PhotoPickerHelper
    private lateinit var shareOutput: TextView
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickerHelper = PhotoPickerHelper.with(this).register(maxItems = 2) { uris ->
            selectedImageUri = uris.firstOrNull()
            shareOutput.text = selectedImageUri?.toString() ?: getString(R.string.share_result_no_image)
        }
        setContentView(createMarkdownPage(
            iconText = getString(R.string.badge_share),
            title = getString(R.string.section_share_title),
            description = getString(R.string.share_description),
            body = createBody()
        ))
    }

    private fun createBody(): View {
        val box = verticalBox()
        box.addView(buttonGrid(
            action(getString(R.string.share_action_pick_image)) { pickerHelper.launchImage() },
            action(getString(R.string.share_action_text)) {
                val ok = ShareUtil.shareText(this, getString(R.string.share_demo_text), getString(R.string.share_title_text))
                shareOutput.text = getString(R.string.share_result_text, ok)
            },
            action(getString(R.string.share_action_selected_image)) {
                val uri = selectedImageUri
                if (uri == null) {
                    showToast(getString(R.string.toast_select_image_first))
                    shareOutput.text = getString(R.string.share_result_no_image)
                } else {
                    val ok = ShareUtil.shareImage(this, uri, getString(R.string.share_title_image))
                    shareOutput.text = getString(R.string.share_result_image, ok)
                }
            },
            action(getString(R.string.share_action_file)) {
                val file = createDemoShareFile()
                val ok = ShareUtil.shareFile(this, file, "text/plain", getString(R.string.share_title_file))
                shareOutput.text = getString(R.string.share_result_file, ok, file.absolutePath)
            },
            action(getString(R.string.share_action_multi_file)) {
                val files = listOf(createDemoShareFile("share-a.txt"), createDemoShareFile("share-b.txt"))
                val ok = ShareUtil.shareMultipleFiles(this, files, "text/plain", getString(R.string.share_title_multi_file))
                shareOutput.text = getString(R.string.share_result_multi_file, ok, files.size)
            },
            action(getString(R.string.share_action_log_file)) {
                ALog.flushSync()
                val file = ALog.getCurrentLogFile()
                if (file == null || !file.exists()) {
                    shareOutput.text = getString(R.string.share_result_no_log)
                } else {
                    val ok = ShareUtil.shareFile(this, file, "text/plain", getString(R.string.share_title_log_file))
                    shareOutput.text = getString(R.string.share_result_log_file, ok, file.name)
                }
            }
        ))
        shareOutput = outputBox(getString(R.string.share_output_waiting))
        box.addView(shareOutput, topMargin())
        box.addView(codeBlock(getString(R.string.code_share)))
        return box
    }

    private fun createDemoShareFile(name: String = "android-core-utils-share.txt"): File {
        val file = File(cacheDir, name)
        FileUtil.saveStringToFile(
            getString(R.string.share_file_content, ALog.getCurrentLogFile()?.name.orEmpty()),
            file
        )
        return file
    }
}
