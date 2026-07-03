package com.wkq.androidcoreutils

import android.content.Intent
import android.os.Bundle
import android.view.View

class MainActivity : BaseDemoActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createHomePage())
    }

    private fun createHomePage(): View {
        val box = verticalBox()
        box.addView(buttonGrid(
            action(getString(R.string.section_log_title)) { open(LogDemoActivity::class.java) },
            action(getString(R.string.section_image_title)) { open(ImageDemoActivity::class.java) },
            action(getString(R.string.section_picker_title)) { open(PickerDemoActivity::class.java) },
            action(getString(R.string.section_share_title)) { open(ShareDemoActivity::class.java) },
            action(getString(R.string.section_sp_title)) { open(SpDemoActivity::class.java) }
        ))
        return createMarkdownPage(
            iconText = getString(R.string.badge_home),
            title = getString(R.string.demo_title),
            description = getString(R.string.demo_home_description),
            body = box
        )
    }

    private fun open(clazz: Class<*>) {
        startActivity(Intent(this, clazz))
    }
}
