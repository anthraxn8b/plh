package eu.kairat.dev.atchelper.ui.init

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import eu.kairat.dev.atchelper.databinding.FragmentInitBinding
import eu.kairat.dev.atchelper.getAirframeChecklists
import eu.kairat.dev.atchelper.ui.CustomFragment

class InitFragment : CustomFragment(
    "FRAG INIT",
    "FLIGHT INITIALIZATION PAGE",
    "FLIGHT SETUP DATA"
) {
    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentInitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewCustom(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        Log.d(logTag, "Constructing $this ...")
        Log.d(logTag, "Selected index is: ${act().selectedAirframeIndex}")

        // create view binding
        _binding = FragmentInitBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // create the view-model
        val viewModel = ViewModelProvider(this)[InitViewModel::class.java]

        // populate the view-model with the potential data selection
        // (may be a good idea if the underlying data list can change...)
        //viewModel.setAirframes(requireContext().getAirframeChecklists())

        // Observe if the view-models selected airframe changes and write the lists index value to
        // the activity. From there other fragments can get the information about the selected
        // airframe.
        // TODO: The view-model is currently changed by view-model business logic.
        //       Should it also be changed by the spinner (a view element)?
        //val spinnerView: Spinner = binding.airframe
        //viewModel.airframeIndex.observe(viewLifecycleOwner) {
        //    // this is the implementation of the observer that is added to the observers list...
        //    Log.d(logTag, "Automatically set item index: $it")
        //    Log.d(logTag, "Here the view could be updated. In our case that is not what we want...")
        //    Log.d(logTag, "Forwarding the index to the activity...")
        //    act().selectedAirframeIndex = it
        //    //spinnerView.setSelection(it)
        //}

        // DO CUSTOM STUFF /////////////////////////////////////////////////////////////////////////

        Log.d(logTag, "Doing custom stuff...")
        Log.d(logTag, "Doing custom stuff...DONE.")

        val spinner = binding.airframe

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            requireContext().getAirframeChecklists().map { it.airframe }.toTypedArray()
        )
        .also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItemText = parent.getItemAtPosition(position).toString()
                Log.d(logTag, "Selected item index: $position")
                Log.d(logTag, "Selected item text:  $selectedItemText")
                viewModel.setAirframeId(selectedItemText)
                // TODO: Do not store the position but the complete airframe data!
                act().selectedAirframeIndex = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // TODO: Block other fragments / invalidate config
                Log.d(logTag, "Unselected all items.")
                viewModel.setAirframeId("")
                // TODO: Do not store the position but the complete airframe data!
                act().selectedAirframeIndex = -1
            }
        }

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
