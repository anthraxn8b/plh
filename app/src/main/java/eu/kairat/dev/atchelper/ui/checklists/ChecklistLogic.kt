package eu.kairat.dev.atchelper.ui.checklists

import android.util.Log
import eu.kairat.dev.atchelper.checklist.data.structure.AirframeData

class ChecklistLogic {

    data class FullSectionPath(
        val airframeData: AirframeData,
        val checklist: AirframeData.Checklist,
        val section: AirframeData.ChecklistSection
    )

    companion object {
        private const val logTag = "CL LOGIC"

        fun resetChecklists(airframeData: List<AirframeData>) {
            Log.d(logTag, "MENU: RESET")
            airframeData.forEach { airframe ->
                airframe.checklists.forEach { checklist ->
                    checklist.complete = false
                    checklist.sections.forEach { section ->
                        section.complete = false
                        section.items.forEach { item ->
                            item.confirmed = false
                        }
                    }
                }
            }
        }

        fun preFinish(airframeData: AirframeData, checklistIndex: Int, sectionIndex: Int) {
            airframeData.checklists.forEachIndexed { currentChecklistIndex, checklist ->
                if(currentChecklistIndex < checklistIndex) {
                    checklist.complete = true
                    checklist.sections.forEach { section ->
                        section.complete = true
                        section.items.forEach { item ->
                            item.confirmed = true
                        }
                    }
                }
                if(currentChecklistIndex == checklistIndex) {
                    checklist.complete = false
                    checklist.sections.forEachIndexed { currentSectionIndex, section ->
                        if(currentSectionIndex < sectionIndex) {
                            section.complete = true
                            section.items.forEach { item ->
                                item.confirmed = true
                            }
                        }
                        if(currentSectionIndex >= sectionIndex) {
                            section.complete = false
                            section.items.forEach { item ->
                                item.confirmed = false
                            }
                        }
                    }
                }
                if(currentChecklistIndex > checklistIndex) {
                    checklist.complete = false
                    checklist.sections.forEach { section ->
                        section.complete = false
                        section.items.forEach { item ->
                            item.confirmed = false
                        }
                    }

                }
            }
        }

        fun findFirstIncompleteSection(airframeData: AirframeData) : FullSectionPath? {
            val checklist = airframeData.checklists.firstOrNull { !it.complete }
            if(null != checklist) {
                Log.d(logTag, "Checklist: ${checklist.name}")
                val section = checklist.sections.firstOrNull { !it.complete }
                if (null != section) {
                    Log.d(logTag, "Section: ${section.name}")
                    return FullSectionPath(airframeData, checklist, section)
                }
            }
            return null
        }

    }

}