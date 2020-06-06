package com.plantdisease.plantdiseasedetection.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import com.plantdisease.plantdiseasedetection.Config.Companion.PLANTS
import com.plantdisease.plantdiseasedetection.R
import com.plantdisease.plantdiseasedetection.base.BaseActivity
import com.plantdisease.plantdiseasedetection.imageloader.GlideLoader
import com.plantdisease.plantdiseasedetection.ui.components.MenuBottomSheet
import com.plantdisease.plantdiseasedetection.ui.main.viewmodel.MainViewModel
import com.plantdisease.plantdiseasedetection.utils.PhotoPickHelper
import com.plantdisease.plantdiseasedetection.utils.ResultState
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity(),
    PhotoPickHelper.PhotoPickCallback {

    private val imageLoader: GlideLoader = GlideLoader(this)
    private val photoPickHelper: PhotoPickHelper =
        PhotoPickHelper(this)

    private lateinit var mediaPickerBottomSheet: MenuBottomSheet

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoPickHelper.addPhotoPickCallback(this)
        photoPickHelper.setActivity(this)

        initializeBottomOptions()

        cardView.setOnClickListener {
            mediaPickerBottomSheet.show(supportFragmentManager)
        }

        if (viewModel.photoPath.isNotEmpty()) {
            setUpImage(viewModel.photoPath)
        }

        scanBtn.setOnClickListener {
            viewModel.uploadPhoto()
        }


        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.dropdown_menu_popup_item,
            PLANTS
        )

        with(filled_exposed_dropdown) {
            setAdapter(adapter)
            onItemClickListener = OnItemClickListener { _, _, position, _ ->
                viewModel.plant = PLANTS[position]
                setButtonValidity()
            }
        }

        viewModel.uploadResult.observe(this, Observer {
            when (it.status) {
                ResultState.Status.SUCCESS -> {
                    hideLoading()
                    it.data?.also { response ->
                        showImageDetail(
                            response.plant,
                            response.status
                        )
                    }
                }
                ResultState.Status.ERROR -> {
                    hideLoading()
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ResultState.Status.LOADING -> {
                    showLoading()
                }
            }
        })
    }

    private fun setButtonValidity() {
        scanBtn.isEnabled = viewModel.photoPath.isNotEmpty() && viewModel.plant.isNotEmpty()
    }


    private fun showImageDetail(plant: String, status: String) {
        createIcon.visibility = GONE
        plantStatusLayout.visibility = VISIBLE
        preview.visibility = VISIBLE

        plantName.text = String.format(getString(R.string.plant_name_format, plant))
        plantStatus.text = String.format(getString(R.string.plant_status_format, status))
    }

    private fun initializeBottomOptions() {
        mediaPickerBottomSheet = MenuBottomSheet.Builder(
            this
        )
            .setOnMenuSelectedListener(object :
                MenuBottomSheet.MenuSelectedListener {
                override fun onDismiss() {
                }

                override fun onMenuSelected(id: Int) {
                    when (id) {
                        R.id.chooseAction -> {
                            photoPickHelper.requestPickPhoto()
                        }
                        R.id.takeAction -> {
                            photoPickHelper.requestTakePhoto()
                        }
                    }
                }
            })
            .setMenuItems(R.menu.photo_action_menu)
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        photoPickHelper.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun showError(b: Boolean, e: Throwable) {

    }

    override fun setUpImage(currentPhotoPath: String) {
        viewModel.photoPath = currentPhotoPath

        setButtonValidity()

        createIcon.visibility = GONE
        plantStatusLayout.visibility = GONE
        preview.visibility = VISIBLE

        imageLoader.loadImage(currentPhotoPath, preview)
    }
}
