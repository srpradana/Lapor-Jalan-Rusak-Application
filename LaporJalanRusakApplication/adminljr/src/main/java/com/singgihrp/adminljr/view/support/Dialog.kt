package com.singgihrp.adminljr.view.support

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.singgihrp.adminljr.R

class Dialog(val activity: Activity) {

    private lateinit var dialog : AlertDialog

    @SuppressLint("InflateParams")
    fun startDialog(){
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater

        builder.setView(inflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}