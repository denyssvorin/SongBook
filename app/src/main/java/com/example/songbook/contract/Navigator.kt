package com.example.songbook.contract

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.example.songbook.UserBandsListAdapter

typealias ResultListenr<T> = (T) -> Unit

fun Fragment.navigator() : Navigator {
    return requireActivity() as Navigator
}

interface Navigator {

    fun goBack()

    fun showSongsList(item_name: String)

    fun openFragmentFromBottomNav(fragment: Fragment)
}