package com.plantdisease.plantdiseasedetection.ui.components


import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.disposables.Disposable
import java.util.*

open class BaseBottomSheet : BottomSheetDialogFragment() {

    private val mDisposables = ArrayList<Disposable>()

    protected fun manage(Disposable: Disposable) {
        mDisposables.add(Disposable)
    }


    fun show(fragmentManager: FragmentManager?) {
        if (!isAdded && fragmentManager != null) {
            val ft = fragmentManager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (disposable in mDisposables) {
            disposable.dispose()
        }
        mDisposables.clear()
    }
}
