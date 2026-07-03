package com.wkq.androidcoreutils

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.wkq.util.FormatUtil
import com.wkq.util.log.ALog

class LogDemoActivity : BaseDemoActivity() {

    private lateinit var logOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createMarkdownPage(
            iconText = getString(R.string.badge_log),
            title = getString(R.string.section_log_title),
            description = getString(R.string.log_description),
            body = createBody()
        ))
        ALog.d(TAG, "Log demo opened")
        appendLog(getString(R.string.log_result_demo_ready, ALog.getCurrentLogFile()?.name.orEmpty()))
    }

    private fun createBody(): View {
        val box = verticalBox()
        box.addView(buttonGrid(
            action(getString(R.string.log_action_levels)) {
                ALog.d(TAG, "debug message")
                ALog.i(TAG, "info message")
                ALog.w(TAG, "warn message")
                ALog.e(TAG, "error message")
                ALog.f(TAG, "assert message")
                appendLog(getString(R.string.log_result_levels))
            },
            action(getString(R.string.log_action_exception)) {
                val error = IllegalStateException("Demo exception")
                ALog.e(TAG, "Captured exception sample", error)
                appendLog(getString(R.string.log_result_exception))
            },
            action(getString(R.string.log_action_tag_filter)) {
                ALog.enableTagFilter(true)
                ALog.addTag(TAG)
                ALog.d("HiddenTag", "This line is filtered")
                ALog.d(TAG, "This line is allowed")
                ALog.enableTagFilter(false)
                ALog.clearTags()
                appendLog(getString(R.string.log_result_tag_filter))
            },
            action(getString(R.string.log_action_flush_sync)) {
                ALog.flushSync()
                appendLog(getString(R.string.log_result_flush_sync))
            },
            action(getString(R.string.log_action_files)) {
                val files = ALog.getLogFiles()
                appendLog(getString(
                    R.string.log_result_files,
                    files.size,
                    files.joinToString("\n") { it.name + " / " + FormatUtil.formatFileSize(it.length()) }
                ))
            },
            action(getString(R.string.log_action_dir)) {
                appendLog(getString(
                    R.string.log_result_dir,
                    ALog.getLogDirOrNull()?.absolutePath.orEmpty(),
                    ALog.getCurrentLogFile()?.absolutePath.orEmpty()
                ))
            }
        ))
        logOutput = outputBox(getString(R.string.log_output_waiting))
        box.addView(logOutput, topMargin())
        box.addView(codeBlock(getString(R.string.code_log)))
        return box
    }

    private fun appendLog(line: String) {
        val old = logOutput.text?.toString().orEmpty()
        logOutput.text = if (old == getString(R.string.log_output_waiting)) line else "$old\n$line"
    }
}
