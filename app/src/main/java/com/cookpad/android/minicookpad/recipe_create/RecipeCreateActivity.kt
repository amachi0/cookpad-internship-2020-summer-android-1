package com.cookpad.android.minicookpad.recipe_create

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cookpad.android.minicookpad.databinding.ActivityRecipeCreateBinding
import com.cookpad.android.minicookpad.datasource.FirebaseImageDataSource
import com.cookpad.android.minicookpad.datasource.FirebaseRecipeDataSource

class RecipeCreateActivity : AppCompatActivity(), RecipeCreateContract.View {
    private lateinit var binding: ActivityRecipeCreateBinding

    private val viewModel: RecipeCreateViewModel by viewModels()

    private val launcher: ActivityResultLauncher<Unit> by lazy {
        registerForActivityResult(ImageSelector()) { imageUri ->
            imageUri?.let {
                Glide.with(this)
                    .load(imageUri)
                    .into(binding.image)
                viewModel.updateImageUri(imageUri.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.apply {
            title = "レシピ作成"
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        val presenter = RecipeCreatePresenter(
            view = this,
            interactor = RecipeCreateInteractor(
                FirebaseRecipeDataSource(),
                FirebaseImageDataSource()
            ),
            routing = RecipeCreateRouting(this)
        )

        binding.image.setOnClickListener {
            launcher.launch(null)
        }

        binding.saveButton.setOnClickListener {
            val recipe = RecipeCreateContract.Recipe(
                title = binding.title.text.toString(),
                imageUri = viewModel.imageUri.value ?: "null",
                steps = listOf(
                    binding.step1.text.toString(),
                    binding.step2.text.toString(),
                    binding.step3.text.toString()
                )
            )
            presenter.onSaveRequested(recipe)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class ImageSelector : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit?): Intent =
            Intent.createChooser(
                Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                },
                "レシピ写真"
            )

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? = intent?.data
    }

    override fun renderSuccess() {
        Toast.makeText(applicationContext, "Success to upload recipe!!", Toast.LENGTH_SHORT).show()
    }

    override fun renderError(throwable: Throwable) {
        Toast.makeText(applicationContext, "Failed to upload recipe.", Toast.LENGTH_SHORT).show()
    }
}
