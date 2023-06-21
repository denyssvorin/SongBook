package com.example.songbook.ui.singlesong

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.View.GONE
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.example.songbook.R
import com.example.songbook.data.Song
import com.example.songbook.databinding.FragmentSingleSongBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlin.math.abs

@AndroidEntryPoint
class SingleSongFragment : Fragment(), SingleSongViewModel.OnAddToFavoriteClickListener,
    BottomSheetChangeTextSize.BottomSheetListener {

    private val args: SingleSongFragmentArgs by navArgs()
    private val viewModel: SingleSongViewModel by viewModels()

    private var _binding: FragmentSingleSongBinding? = null
    private val binding get() = _binding!!

    private lateinit var addToFavoriteIcon: MenuItem

    private lateinit var iconScroll: MenuItem
    private var isScrolling = false

    private lateinit var textView: TextView

    private var scrollStartPosition = 0
    private var scrollEndPosition = 0
    private var isUserScrolling = false
    private var currentScrollPosition = 0


    private lateinit var animator: ValueAnimator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSingleSongBinding.inflate(inflater, container, false)
        setupMenu()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFromSharPref()

        binding.textViewTextSong.text = args.song.textSong
        viewModel.isFavorite = viewModel.resultSuccessFavorite.value ?: args.song.isFavorite
        viewModel.resultSuccessFavorite.observe(viewLifecycleOwner) { favValue ->
            if (favValue) {
                addToFavoriteIcon.setIcon(R.drawable.ic_favorite_checked)
                showAddCustomToast()
            } else {
                addToFavoriteIcon.setIcon(R.drawable.ic_favorite_border)
                showRemoveCustomToast()
            }
        }

        textView = binding.textViewTextSong
        viewModel.isScrollIcon = isScrolling

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideBottomNavigation()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hideBottomNavigation()
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showBottomNavigation()
        }
    }

    private fun hideBottomNavigation() {
        if (isAdded) {
            val activity = this.requireActivity()
            val navBar = activity.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            navBar?.visibility = GONE
        }
    }

    private fun showBottomNavigation() {
        if (isAdded) {
            val activity = this.requireActivity()
            val navBar = activity.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            navBar?.visibility = View.VISIBLE
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onPrepareMenu(menu: Menu) {
                super.onPrepareMenu(menu)
                val changeTextSizeIcon = menu.findItem(R.id.action_change_text_size)
                changeTextSizeIcon.isVisible = true
                val moreIcon = menu.findItem(R.id.action_more)
                moreIcon.isVisible = true

                addToFavoriteIcon = menu.findItem(R.id.action_add_to_favorite)
                addToFavoriteIcon.isVisible = true
                iconScroll = menu.findItem(R.id.action_play)
                iconScroll.isVisible = false

                if (viewModel.isFavorite) {
                    addToFavoriteIcon.setIcon(R.drawable.ic_favorite_checked)
                } else {
                    addToFavoriteIcon.setIcon(R.drawable.ic_favorite_border)
                }

            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_app_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_add_to_favorite -> {
                        viewModel.setFavorite(args.song)

                        return true
                    }
                    R.id.action_change_text_size -> {
                        val bottomSheet = BottomSheetChangeTextSize(this@SingleSongFragment)
                        bottomSheet.show(parentFragmentManager, "exampleBottomSheet")

                        return true
                    }
                    R.id.action_play -> {
                        viewModel.setPlayIcon()
                        if (viewModel.isScrollIcon) {
                            startScrolling()
                            Toast.makeText(requireContext(), "text start", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            stopScrolling()
                            Toast.makeText(requireContext(), "text stop", Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun startScrolling() {
        val maxScroll = textView.layout.height - (0.93 * binding.linearLayout.height).toInt()
        Log.i("TAG", "startScrolling: maxScroll $maxScroll")
        val duration = abs(maxScroll * 10L)

        val scrollView = binding.scrollView
        scrollView.post {
            scrollStartPosition = currentScrollPosition
            Log.i("TAG", "scrollStartPosition: $scrollStartPosition")
            scrollEndPosition = maxScroll

            animator = ValueAnimator.ofInt(scrollStartPosition, scrollEndPosition)
            animator.duration = duration
            animator.interpolator = LinearInterpolator()

            animator.addUpdateListener { animation ->
                if (!isUserScrolling) {
                    val animatedValue = animation.animatedValue as Int
                    scrollView.scrollTo(0, animatedValue)
                    currentScrollPosition = animatedValue
                }
            }

            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    isScrolling = false
                    updateButtonIcon()
                }

                override fun onAnimationCancel(animation: Animator) {
                    isScrolling = false
                    updateButtonIcon()
                }

                override fun onAnimationRepeat(animation: Animator) {}
            })

            scrollView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                    isUserScrolling = true
                    animator.cancel()
                } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    isUserScrolling = false
                    currentScrollPosition = scrollView.scrollY
                    animator.setIntValues(currentScrollPosition, scrollEndPosition)
                    animator.start()
                }
                false
            }

            animator.start()
        }

        isScrolling = true
        updateButtonIcon()
    }


    private fun stopScrolling() {
        val scrollView = binding.scrollView
        scrollView.removeCallbacks(null)
        animator.cancel()

        isScrolling = false
        updateButtonIcon()
    }

    private fun updateButtonIcon() {
        iconScroll.icon = if (isScrolling) {
            // Встановіть значок зупинки, наприклад, R.drawable.ic_pause
            getDrawable(requireContext(), R.drawable.ic_pause)
        } else {
            // Встановіть значок старту, наприклад, R.drawable.ic_play
            getDrawable(requireContext(), R.drawable.ic_play)
        }
    }

    private fun setupFromSharPref() {
        val sharedPref =
            activity?.getSharedPreferences("app_settings", AppCompatActivity.MODE_PRIVATE)

        val savedTextSize = sharedPref?.getInt("single_song_text_size", 16) ?: 16

        binding.textViewTextSong.textSize = savedTextSize.toFloat()
    }

    private fun showAddCustomToast() {
        val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT)
        val root: ViewGroup? = null
        val toastLayout = layoutInflater.inflate(R.layout.custom_toast_add_layout, root)
        toast.view = toastLayout
        toast.show()
    }

    private fun showRemoveCustomToast() {
        val toast = Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT)
        val root: ViewGroup? = null
        val toastLayout = layoutInflater.inflate(R.layout.custom_toast_remove_layout, root)
        toast.view = toastLayout
        toast.show()
    }

    override fun addToFavorite(song: Song, isFavorite: Boolean) {
        viewModel.addToFavoriteSong(song, isFavorite)
    }

    override fun increaseText(value: Float) {
        binding.textViewTextSong.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
    }

    override fun decreaseText(value: Float) {
        binding.textViewTextSong.setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
    }
}