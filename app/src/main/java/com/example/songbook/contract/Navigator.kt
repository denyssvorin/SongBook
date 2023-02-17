package com.example.songbook.contract

import androidx.fragment.app.Fragment

typealias ResultListenr<T> = (T) -> Unit

fun Fragment.navigator() : Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun goBack()

    fun showSongsList(item_name: String)

    fun openFragmentFromBottomNav(fragment: Fragment)
}