package com.example.songbook.contract

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface HasCustomActions {
    fun getCustomAction(): CustomAction
}

class CustomAction (
    @DrawableRes val iconRes: Int,
    @StringRes val textRes: Int,
    val onCustomAction: Runnable
    )
