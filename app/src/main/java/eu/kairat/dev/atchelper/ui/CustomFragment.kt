package eu.kairat.dev.atchelper.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import eu.kairat.dev.atchelper.MainActivity

/**
 * This abstract class handles the fragments title and subtitle.
 */
abstract class CustomFragment(
    val logTag : String,
    private val title : String,
    private val subTitle : String
) : Fragment() {

    abstract fun onCreateViewCustom(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initTitleAndSubTitle()
        return onCreateViewCustom(inflater, container, savedInstanceState)
    }

    private fun initTitleAndSubTitle() {
        Log.d(logTag, "Setting title to \"$title\" and subtitle to \"$subTitle\"...")
        setTitle(title)
        setSubtitle(subTitle)
        Log.d(logTag, "Setting title to \"$title\" and subtitle to \"$subTitle\"...Done.")
    }

    fun setTitle(title : String) {
        Log.d(logTag, "Setting title to \"$title\"...")

        val actionBar = actionBar()
        actionBar.title = title.uppercase()

        Log.d(logTag, "Setting title to \"$title\"...Done.")
    }

    fun setSubtitle(subtitle : String) {
        Log.d(logTag, "Setting subtitle to \"$subtitle\"...")

        val actionBar = actionBar()
        actionBar.subtitle = subtitle.uppercase()

        Log.d(logTag, "Setting subtitle to \"$subtitle\"...Done.")
    }

    fun act() : MainActivity = (requireActivity() as MainActivity)
    private fun actionBar() : ActionBar = (requireActivity() as MainActivity).supportActionBar!!

}