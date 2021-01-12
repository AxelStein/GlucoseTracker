package com.axel_stein.glucose_tracker.ui.edit_insulin

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.axel_stein.glucose_tracker.R
import com.axel_stein.glucose_tracker.databinding.ActivityEditInsulinBinding
import com.axel_stein.glucose_tracker.ui.dialogs.ConfirmDialog
import com.axel_stein.glucose_tracker.ui.edit_insulin_log.EditInsulinLogActivityArgs
import com.axel_stein.glucose_tracker.utils.*

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
        setupTypeSpinner()

        viewModel.actionFinishLiveData().observe(this, { if (it) finish() })
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupEditor() {
        binding.editTitle.setupEditor { text ->
            viewModel.setTitle(text)
        }

        viewModel.titleLiveData().observe(this, { text ->
            binding.editTitle.setEditorText(text)
        })

        viewModel.errorEmptyTitleLiveData().observe(this, { error ->
            binding.inputLayoutTitle.showEmptyFieldError(binding.editTitle, error)
        })
    }

    private fun setupTypeSpinner() {
        binding.typeSpinner.setupSpinner(binding.inputLayoutType) { position ->
            viewModel.setType(position)
        }
        binding.typeSpinner.setSpinnerItems(resources.getStringArray(R.array.insulin_types))

        viewModel.typeLiveData().observe(this, { type ->
            binding.typeSpinner.setSpinnerSelection(type)
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