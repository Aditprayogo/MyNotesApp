package com.aditprayogo.cumsumerapp

import android.view.View

class CustomOnItemClickListener(private val position: Int, private val onItemCLickCallback: OnItemClickCallback) : View.OnClickListener{

    interface OnItemClickCallback {
        fun onItemClicked(view: View, position: Int)
    }

    override fun onClick(view: View) {
        onItemCLickCallback.onItemClicked(view,position)
    }
}