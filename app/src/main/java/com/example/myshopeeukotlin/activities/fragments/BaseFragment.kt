package com.example.myshopeeukotlin.activities.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.myshopeeukotlin.R

open class BaseFragment : Fragment() {
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(requireActivity())
        /* Set the Screen content from a layout resources
        The require will be inflated ,added all top-levels views to the screen */
        mProgressDialog.setContentView(R.layout.dialog_progress_bar)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        // start the dialog and display it on screen
        mProgressDialog.show()

    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}