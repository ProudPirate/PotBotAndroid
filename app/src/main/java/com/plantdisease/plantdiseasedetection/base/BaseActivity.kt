package com.plantdisease.plantdiseasedetection.base

import androidx.appcompat.app.AppCompatActivity
import com.plantdisease.plantdiseasedetection.utils.LoadingUiHelper
import com.plantdisease.plantdiseasedetection.ui.components.MenuBottomSheet

abstract class BaseActivity : AppCompatActivity() {

    private var progressDialog: LoadingUiHelper.ProgressDialogFragment? = null

    fun showLoading(type: LoadingUiHelper.Type = LoadingUiHelper.Type.FULL_SCREEN) {
        if (progressDialog == null) {
            progressDialog =
                LoadingUiHelper.showProgress(
                    supportFragmentManager,
                    type
                )
        }
    }

    fun hideLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }


    fun showBottomOptionMenu(
        menuRes: Int,
        title: String = "",
        onMenuSelected: (Int) -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        val sheet = MenuBottomSheet.Builder(this)
            .setOnMenuSelectedListener(object : MenuBottomSheet.MenuSelectedListener {
                override fun onDismiss() {
                    onDismiss()
                }

                override fun onMenuSelected(id: Int) {
                    onMenuSelected(id)
                }
            })
            .setTitle(title)
            .setMenuItems(menuRes)
            .create()
        sheet.show(supportFragmentManager)
    }
}