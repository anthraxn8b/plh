package eu.kairat.dev.atchelper.ui.atc

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.kairat.dev.atchelper.databinding.FragmentAtcBinding
import eu.kairat.dev.atchelper.ui.CustomFragment

class AtcFragment : CustomFragment(
    "FRAG ATC",
    "COMM TEMPLATES",
    "< REQUEST CLEARANCE >"
) {
    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentAtcBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewCustom(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        Log.d(logTag, "Constructing $this ...")

        // create view binding
        _binding = FragmentAtcBinding.inflate(inflater, container, false)
        val root: View = binding.root

/*
        val viewModel = ViewModelProvider(this)[AtcViewModel::class.java]

        val textView: TextView = binding.textAtc
        atcViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
*/
        // DO CUSTOM STUFF /////////////////////////////////////////////////////////////////////////

        Log.d(logTag, "Doing custom stuff...")
        Log.d(logTag, "Doing custom stuff...DONE.")

        Log.d(logTag, "Constructing $this ...DONE.")
        return root
    }

    override fun onDestroyView() {
        Log.d(logTag, "Destroying $this ...")
        super.onDestroyView()
        _binding = null
        Log.d(logTag, "Destroying $this ...DONE.")
    }

}