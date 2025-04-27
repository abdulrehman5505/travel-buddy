package com.project.travelbuddy.util

import android.app.AlertDialog
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.travelbuddy.R

object Constant {

    fun validateData(
        tieEmail: TextInputEditText,
        tiePassword: TextInputEditText,
        tilEmail: TextInputLayout,
        tilPassword: TextInputLayout
    ): Pair<String, String>? {
        val email = tieEmail.text.toString().trim()
        val password = tiePassword.text.toString()

        if (TextUtils.isEmpty(email)) {
            tilEmail.error = "Please enter email"
            return null
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Invalid Email format"
            return null
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.error = "Please enter password"
            return null
        }

        if (password.length < 6) {
            tilPassword.error = "Password must be at least 6 characters long "
            return null
        }

        return Pair(email, password)

    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private var progressDialog: AlertDialog? = null

    fun showLoading(context: Context) {
        if (progressDialog != null && progressDialog!!.isShowing) return

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)

        builder.setView(dialogView)
        builder.setCancelable(false)
        progressDialog = builder.create()
        progressDialog?.show()
    }

    fun hideLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }
}