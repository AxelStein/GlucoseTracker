package com.axel_stein.glucose_tracker.ui.edit_insulin

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditInsulinBinding
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.edit_insulin_log.EditInsulinLogActivityArgs
import com.axel_stein.glucose_tracker.utils.CArrayAdapter
import com.axel_stein.glucose_tracker.utils.hideKeyboard
import com.axel_stein.glucose_tracker.utils.show
import com.axel_stein.glucose_tracker.utils.showKeyboard

class EditInsulinActivity : AppCompatActivity(), ConfirmDialog.OnConfirmListener {
    private val args: EditInsulinLogActivityArgs by navArgs()
    private val viewModel: EditInsulinViewModel by viewModels { EditInsulinFactory(this, args.id) }
    private lateinit var binding: ActivityEditInsulinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInsulinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupEditor()
        setupType()

        viewModel.actionFinishLiveData().observe(this, { if (it) finish() })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupEditor() {
        binding.editTitle.doAfterTextChanged {
            viewModel.setTitle(it.toString())
        }
        binding.editTitle.setOnEditorActionListener { v, actionId, _ ->
            var consumed = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                (v as EditText).hideKeyboard()
                consumed = true
            }
            consumed
        }

        var focusEdit = true
        viewModel.titleLiveData().observe(this, { value ->
            if (value != binding.editTitle.text.toString()) {
                binding.editTitle.setText(value.toString())
                binding.editTitle.setSelection(binding.editTitle.length())
            }
            if (focusEdit) {
                focusEdit = false
                if (value.isNullOrEmpty()) {
                    binding.editTitle.showKeyboard()
                } else {
                    binding.editTitle.hideKeyboard()
                }
            }
        })

        binding.inputLayoutTitle.show()

        viewModel.errorEmptyTitleLiveData().observe(this, { error ->
            if (error) {
                binding.inputLayoutTitle.error = getString(R.string.no_value)
                binding.editTitle.showKeyboard()
            }
            binding.inputLayoutTitle.isErrorEnabled = error
        })
    }

    private fun setupType() {
        val adapter = CArrayAdapter(
            this,
            R.layout.popup_item,
            resources.getStringArray(R.array.insulin_types)
        )

        binding.typeDropdown.inputType = 0  // disable ime input
        binding.typeDropdown.setOnKeyListener { _, _, _ -> true }  // disable hardware keyboard input
        binding.typeDropdown.setAdapter(adapter)
        binding.typeDropdown.setOnClickListener { binding.editTitle.hideKeyboard() }

        binding.inputLayoutType.setEndIconOnClickListener {
            // override default behavior in order to close ime
            binding.typeDropdown.performClick()
        }
        binding.typeDropdown.setOnItemClickListener { _, _, position, _ ->
            binding.inputLayoutType.clearFocus()
            viewModel.setType(position)
        }
        binding.typeDropdown.setOnDismissListener { binding.inputLayoutType.clearFocus() }

        viewModel.typeLiveData().observe(this, { value ->
            if (value != binding.typeDropdown.listSelection) {
                binding.typeDropdown.listSelection = value
                binding.typeDropdown.setText(adapter.getItem(value), false)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_insulin, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> viewModel.save()
            R.id.menu_delete -> {
                ConfirmDialog.Builder()
                    .from(this)
                    .title(R.string.title_confirm)
                    .message(R.string.msg_delete_insulin)
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