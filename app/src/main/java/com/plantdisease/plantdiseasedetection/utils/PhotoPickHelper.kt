package com.plantdisease.plantdiseasedetection.utils

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import com.plantdisease.plantdiseasedetection.ui.camera.CameraActivity

class PhotoPickHelper(
    context: Context
) {
    private val realPathUtil =
        RealPathUtil(context)
    private val permissionHelper =
        PermissionHelper(context)

    private var photoPickCallback: PhotoPickCallback = object :
        PhotoPickCallback {
        override fun showError(b: Boolean, e: Throwable) {

        }

        override fun setUpImage(currentPhotoPath: String) {

        }
    }
    private var activity: Activity? = null

    fun requestTakePhoto() {
        if (activity != null) {
            CameraActivity.newInstance(activity!!)
        }
    }

    fun requestPickPhoto() {
        permissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
            "Storage Permission",
            "Permission is needed to read the image files",
            object : PermissionHelper.PermissionGrant {
                override fun permissionGranted() {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    if (activity != null) {
                        activity!!.startActivityForResult(
                            Intent.createChooser(
                                intent,
                                "Complete action using"
                            ),
                            REQUEST_PICK_IMAGE
                        )
                    }
                }
            })
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data ?: return
        if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data.data != null) {
                photoPickCallback.setUpImage(realPathUtil.getRealPath(data.data!!))
            }
        } else if (requestCode == CameraActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            photoPickCallback.setUpImage(data.getStringExtra(CameraActivity.ARGS_PATH)!!)
        }
    }

    fun addPhotoPickCallback(photoPickCallback: PhotoPickCallback) {
        this.photoPickCallback = photoPickCallback
    }

    fun setActivity(activity: Activity) {
        this.activity = activity
        permissionHelper.setActivity(activity)
    }

    interface PhotoPickCallback {
        fun showError(b: Boolean, e: Throwable)

        fun setUpImage(currentPhotoPath: String)
    }

    companion object {
        const val REQUEST_PICK_IMAGE = 542
    }
}
