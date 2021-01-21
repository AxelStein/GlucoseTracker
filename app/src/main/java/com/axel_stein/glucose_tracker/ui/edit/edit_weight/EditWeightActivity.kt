package com.axel_stein.glucose_tracker.ui.edit.edit_weight

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditWeightBinding
import com.axel_stein.glucose_tracker.ui.EditorActivity
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.utils.ui.*
import com.google.android.material.snackbar.Snackbar

class EditWeightActivity : EditorActivity(), ConfirmDialog.OnConfirmListener {
    private val args: EditWeightActivityArgs by navArgs()
    private val viewModel: EditWeightViewModel by viewModels { EditWeightFactory(this, args.id) }
    private lateinit var binding: ActivityEditWeightBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar(binding.toolbar)
        setupDateTime(binding.btnDate, binding.btnTime, viewModel)
        setupWeightEditor()

        viewModel.bmiResultLiveData.observe(this, {
            if (it.value == 0f) binding.textBmi.hide()
            else {
                binding.textBmi.show()
                val categories = resources.getStringArray(R.array.bmi_categories)
                binding.textBmi.text = getString(R.string.bmi_result, it.value, categories[it.category])
            }
        })
        viewModel.showHintIndicateHeightLiveData.observe(this, {
            setViewVisible(it, binding.textIndicateHeight)
        })

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

    private fun setupWeightEditor() {
        binding.editWeight.setupEditor { text ->
            viewModel.setWeight(text)
        }

        viewModel.weightLiveData.observe(this, { value ->
            binding.editWeight.setEditorText(value)
        })

        viewModel.errorNoteEmptyLiveData.observe(this, { error ->
            binding.inputLayoutWeight.showEmptyFieldError(error)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_editor, menu)
        menu?.findItem(R.id.menu_delete)?.isVisible = args.id != 0L
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder().from(this)
                    .title(R.string.dialog_title_confirm)
                    .message(R.string.dialog_msg_delete_note)
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