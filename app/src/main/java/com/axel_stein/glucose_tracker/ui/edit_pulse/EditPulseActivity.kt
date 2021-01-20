package com.axel_stein.glucose_tracker.ui.edit_pulse

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditPulseBinding
import com.axel_stein.glucose_tracker.ui.EditorActivity
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.ui.setEditorText
import com.axel_stein.glucose_tracker.utils.ui.setupEditor
import com.axel_stein.glucose_tracker.utils.ui.showEmptyFieldError
import com.google.android.material.snackbar.Snackbar

class EditPulseActivity : EditorActivity(), OnConfirmListener {
    private val args: EditPulseActivityArgs by navArgs()
    private val viewModel: EditPulseViewModel by viewModels { EditPulseFactory(this, args.id) }
    private lateinit var binding: ActivityEditPulseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditPulseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        setupDateTime(binding.btnDate, binding.btnTime, viewModel)
        setupEditor()

        viewModel.showMessageLiveData.observe(this, {
            val msg = it.getContent()
            if (msg != null) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
            }
        })
        viewModel.actionFinishLiveData.observe(this, {
            it.handleEvent()
            finish()
        })
    }

    private fun setupEditor() {
        binding.editor.setupEditor { text ->
            viewModel.setPulse(text)
        }

        viewModel.pulseLiveData.observe(this, { value ->
            binding.editor.setEditorText(value)
        })

        viewModel.errorPulseEmptyLiveData.observe(this, { error ->
            binding.inputLayout.showEmptyFieldError(error)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_editor, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.dialog_msg_delete_insulin_log)
                    .positiveBtnText(R.string.action_delete)
                    .negativeBtnText(R.string.action_cancel)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfirm(tag: String?) {
        viewModel.delete()
    }

}