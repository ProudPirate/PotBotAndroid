package com.plantdisease.plantdiseasedetection.ui.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Size
import android.view.Surface
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.plantdisease.plantdiseasedetection.base.BaseActivity
import com.plantdisease.plantdiseasedetection.R
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


// This is an arbitrary number we are using to keep track of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts.
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class CameraActivity : BaseActivity() {
    private val targetResolution: Size
        get() {
            val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
            return Size(
                metrics.widthPixels,
                metrics.widthPixels
            )
        }
    private val executor = Executors.newSingleThreadExecutor()
    private var lensFacing = CameraX.LensFacing.BACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_camera)

        // Request camera permissions
        if (allPermissionsGranted()) {
            previewView.post { startCamera(lensFacing) }
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        backBtn.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        // Every time the provided texture view changes, recompute layout
        previewView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }

    private fun startCamera(lensFacing: CameraX.LensFacing = this.lensFacing) {

        CameraX.unbindAll()

        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            targetResolution.apply { setTargetResolution(this) }
            setLensFacing(lensFacing)
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = previewView.parent as ViewGroup
            parent.removeView(previewView)
            parent.addView(previewView, 0)

            previewView.surfaceTexture = it.surfaceTexture
            updateTransform()
        }
        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)
        captureView?.setOnClickListener {
            val file = File(getImagePath())
            showLoading()
            imageCapture.takePicture(file, executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        hideLoading()
                    }

                    override fun onImageSaved(file: File) {
                        hideLoading()
                        this@CameraActivity.onImageSaved()
                    }
                })
        }

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }


    private fun getImagePath(): String {
        return if (intent.hasExtra(ARGS_PATH))
            intent.getStringExtra(ARGS_PATH)!!
        else
            throw RuntimeException("Path not supplied to camera activity")
    }

    fun onImageSaved() {
        val intent = Intent()
        intent.putExtra(ARGS_PATH, getImagePath())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = previewView.width / 2f
        val centerY = previewView.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (previewView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        previewView.setTransform(matrix)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                previewView.post { startCamera(lensFacing) }
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    /**
     * Check if all permission specified in the manifest have been granted
     */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    companion object {

        const val ARGS_PATH: String = "args:path"
        const val REQUEST_CODE: Int = 100

        fun newInstance(activity: Activity, path: String = "") {
            val intent = Intent(activity, CameraActivity::class.java)
            intent.putExtra(ARGS_PATH, if (path.isEmpty()) activity.createTempFile() else path)
            activity.startActivityForResult(intent,
                REQUEST_CODE
            )
        }
    }
}

fun Context.createTempFile(): String {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val savedPhoto = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        storageDir      /* directory */
    )
    return savedPhoto.absolutePath
}