package eu.kairat.dev.atchelper.ui.checklists

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import eu.kairat.dev.atchelper.checklist.data.ChecklistItemAdapter
import eu.kairat.dev.atchelper.databinding.FragmentChecklistsBinding
import eu.kairat.dev.atchelper.ui.CustomFragment

class ChecklistsFragment : ViewHelperMenuBuilder.ChecklistMenu, CustomFragment(
    "FRAG CHECKLIST",
    "CHECKLISTS",
    "< ON BEFORE STARTUP >"
) {
    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentChecklistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewCustom(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        Log.d(logTag, "Constructing $this ...")

        // create view binding
        _binding = FragmentChecklistsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // DO CUSTOM STUFF /////////////////////////////////////////////////////////////////////////

        Log.d(logTag, "Doing custom stuff...")

        Log.d(logTag, "Loading data...")

        // PROBLEM: No layout manager attached...
        // FIX:     http://www.chansek.com/RecyclerView-no-adapter-attached-skipping-layout/
        val manager = LinearLayoutManager(requireContext())
        binding.checklistsChecklistItemsRecycler.layoutManager = manager

        setNextChecklistSectionAdapter()

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.checklistsChecklistItemsRecycler.setHasFixedSize(true)

        return root
    }

    override fun reset() {
        ChecklistLogic.resetChecklists(act().airframeData)
        setNextChecklistSectionAdapter()
    }

    override fun gotoNext() {
        Log.d(logTag, "NOT YET IMPLEMENTED!")
    }

    override fun select() {
        ChecklistLogic.resetChecklists(act().airframeData)
        createDialogChecklistSelection()
    }

    override fun toggleStt() {
        //act().stth.toggleOnOff()
        act().voskh.toggleOnOff()
    }

    private fun createDialogChecklistSelection() {

        val checklists = act().airframeData[act().selectedAirframeIndex].checklists

        ViewHelperPopupSelect.showSelectList(context, "checklist selection", checklists.map { it.name }) { indexChecklist ->
            ViewHelperPopupSelect.showSelectList(context ,"checklist section selection", checklists[indexChecklist].sections.map { it.name }) { indexSection ->
                Log.d( logTag, "Selected checklist section: ${checklists[indexChecklist].name} > ${checklists[indexChecklist].sections[indexSection].name}")
                checklists[indexChecklist].complete = false
                checklists[indexChecklist].sections[indexSection].complete = false
                checklists[indexChecklist].sections[indexSection].items.forEach { it.confirmed = false }
                setChecklistSectionAdapter(
                    ChecklistLogic.FullSectionPath(
                        act().airframeData[act().selectedAirframeIndex],
                        checklists[indexChecklist],
                        checklists[indexChecklist].sections[indexSection]
                    )
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewHelperMenuBuilder.buildMenu(act(), this, viewLifecycleOwner)
    }

    fun setNextChecklistSectionAdapter() {
        val data = ChecklistLogic.findFirstIncompleteSection(act().airframeData[act().selectedAirframeIndex])
        if(null == data) {
            Log.d(logTag, "All checklists are completed!")
            // TODO: Handle no checklist selected!
            return
        }
        setChecklistSectionAdapter(data)
    }

    private fun setChecklistSectionAdapter(data: ChecklistLogic.FullSectionPath) {
        setSubtitle(data.airframeData.airframe)
        setTitle("${data.checklist.name} > ${data.section.name}")

        val adapter = ChecklistItemAdapter(
            requireContext(),
            this,
            act().ttsh,
            //act().stth,
            act().voskh,
            data.airframeData,
            data.checklist,
            data.section
        )

        binding.checklistsChecklistItemsRecycler.adapter = adapter
    }

    override fun onDestroyView() {
        Log.d(logTag, "Destroying $this ...")
        super.onDestroyView()
        _binding = null
        Log.d(logTag, "Destroying $this ...DONE.")
    }

}