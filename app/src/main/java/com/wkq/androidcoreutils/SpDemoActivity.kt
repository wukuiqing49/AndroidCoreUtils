package com.wkq.androidcoreutils

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.wkq.util.SpUtils
import com.wkq.util.showToast

class SpDemoActivity : BaseDemoActivity() {

    private lateinit var spInput: EditText
    private lateinit var spOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createMarkdownPage(
            iconText = getString(R.string.badge_sp),
            title = getString(R.string.section_sp_title),
            description = getString(R.string.sp_description),
            body = createBody()
        ))
        refreshSpValue()
    }

    private fun createBody(): View {
        val box = verticalBox()
        spInput = EditText(this).apply {
            hint = getString(R.string.sp_input_hint)
            textSize = 15f
            setSingleLine()
            setPadding(dp(12), 0, dp(12), 0)
            background = roundedStroke(android.graphics.Color.WHITE, android.graphics.Color.rgb(208, 215, 222), dp(8))
        }
        box.addView(spInput, matchFixedHeight(48))
        box.addView(buttonGrid(
            action(getString(R.string.sp_action_save_string)) {
                val value = spInput.text?.toString().orEmpty().ifBlank { "AndroidCoreUtils" }
                SpUtils.put(KEY_DEMO_TEXT, value)
                refreshSpValue()
                showToast(getString(R.string.toast_saved_string))
            },
            action(getString(R.string.sp_action_save_types)) {
                SpUtils.put("demo_int", 2026)
                SpUtils.put("demo_bool", true)
                SpUtils.put("demo_set", setOf("log", "image", "share"))
                refreshSpValue()
            },
            action(getString(R.string.sp_action_read)) { refreshSpValue() },
            action(getString(R.string.sp_action_all_keys)) {
                spOutput.text = getString(R.string.sp_result_all_keys, SpUtils.allKeys().joinToString("\n"))
            },
            action(getString(R.string.sp_action_remove_text)) {
                SpUtils.remove(KEY_DEMO_TEXT)
                refreshSpValue()
            },
            action(getString(R.string.sp_action_remove_batch)) {
                SpUtils.removeKeys(arrayOf("demo_int", "demo_bool", "demo_set"))
                refreshSpValue()
            },
            action(getString(R.string.sp_action_clear)) {
                SpUtils.clearAll()
                spInput.setText("")
                refreshSpValue()
            }
        ))
        spOutput = outputBox(getString(R.string.sp_output_waiting))
        box.addView(spOutput, topMargin())
        box.addView(codeBlock(getString(R.string.code_sp)))
        return box
    }

    private fun refreshSpValue() {
        spOutput.text = getString(
            R.string.sp_result_values,
            SpUtils.getString(KEY_DEMO_TEXT, "").ifBlank { getString(R.string.value_empty) },
            SpUtils.getInt("demo_int", -1),
            SpUtils.getBoolean("demo_bool", false),
            SpUtils.getStringSet("demo_set").joinToString(),
            SpUtils.isEmpty()
        )
    }
}
