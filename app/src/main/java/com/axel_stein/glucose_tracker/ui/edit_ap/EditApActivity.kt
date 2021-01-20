package com.axel_stein.glucose_tracker.ui.edit_ap

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditApBinding
import com.axel_stein.glucose_tracker.ui.EditorActivity
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog.OnConfirmListener
import com.axel_stein.glucose_tracker.utils.ui.setEditorText
import com.axel_stein.glucose_tracker.utils.ui.setupEditor
import com.axel_stein.glucose_tracker.utils.ui.showEmptyFieldError
import com.google.android.material.snackbar.Snackbar

class EditApActivity : EditorActivity(), OnConfirmListener {
    private val args: EditApActivityArgs by navArgs()
    private val viewModel: EditApViewModel by viewModels { EditApFactory(this, args.id) }
    private lateinit var binding: ActivityEditApBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditApBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        setupDateTime(binding.btnDate, binding.btnTime, viewModel)
        setupSystolicEditor()
        setupDiastolicEditor()

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

    private fun setupSystolicEditor() {
        binding.systolic.setupEditor { text ->
            viewModel.setSystolic(text)
        }

        viewModel.systolicLiveData.observe(this, { value ->
            binding.systolic.setEditorText(value)
        })

        viewModel.errorSystolicEmptyLiveData.observe(this, { error ->
            binding.inputLayoutSystolic.showEmptyFieldError(error)
        })
    }

    private fun setupDiastolicEditor() {
        binding.diastolic.setupEditor { text ->
            viewModel.setDiastolic(text)
        }

        viewModel.diastolicLiveData.observe(this, { value ->
            binding.diastolic.setEditorText(value, false)
        })

        viewModel.errorDiastolicEmptyLiveData.observe(this, { error ->
            binding.inputLayoutDiastolic.showEmptyFieldError(error)
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