package com.pes2017.recomp

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity

/**
 * Settings > Controls > Arrange: the on-screen pad editor without a match.
 * Landscape like the match, so a control put somewhere here is in the same
 * place in play, but the system bars stay visible so the strip's text is not
 * under the status bar or a cutout. The same strip as the in-match editor
 * (ControlEditPanel); every change is saved as it is made.
 */
class ControlsEditorActivity : AppCompatActivity() {

    private lateinit var overlay: TouchOverlayView
    private lateinit var panel: ControlEditPanel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = Prefs(this)
        window.setBackgroundDrawable(ColorDrawable(Color.rgb(18, 58, 34)))
        val root = FrameLayout(this)
        // A dark pitch, so the translucent controls read as they do over a match.
        root.setBackgroundColor(Color.rgb(18, 58, 34))

        val hint = TextView(this).apply {
            setText(R.string.arrange_hint)
            setTextColor(Color.argb(150, 255, 255, 255))
            textSize = 13f
            gravity = Gravity.CENTER
        }
        root.addView(
            hint,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER,
            ),
        )

        overlay = TouchOverlayView(this)
        overlay.layout = ControlLayout.load(this)
        overlay.playStation = prefs.playStationButtons
        overlay.onLayoutEdited = { overlay.layout.save(this) }
        overlay.onSelectionChanged = { panel.show(it) }
        root.addView(
            overlay,
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT),
        )
        setContentView(root)
        applySystemBars(lightIcons = false)

        panel = ControlEditPanel(this, root, overlay) { finish() }
        overlay.editing = true
        panel.attach()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = finish()
        })
    }

    override fun onPause() {
        super.onPause()
        overlay.layout.save(this)
    }
}
