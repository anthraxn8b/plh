package eu.kairat.dev.atchelper.checklist.data

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import eu.kairat.dev.atchelper.R
import eu.kairat.dev.atchelper.checklist.data.structure.AirframeData
import eu.kairat.dev.atchelper.checklist.data.structure.AirframeData.Checklist
import eu.kairat.dev.atchelper.checklist.data.structure.AirframeData.ChecklistSection
import eu.kairat.dev.atchelper.ui.checklists.ChecklistsFragment
import eu.kairat.dev.atchelper.ui.checklists.ViewHelperPopupSelect

class ChecklistItemAdapter(
    private val context: Context,
    private val checklistsFragment: ChecklistsFragment,
    private val ttsh: TtsHelper,
    private val voskh: VoskHelper,
    private val airframe: AirframeData,
    private val checklist: Checklist,
    private val section: ChecklistSection
) : RecyclerView.Adapter<ChecklistItemAdapter.ChecklistItemViewHolder>() {

    private val logTag = "CL ADAPTER"

    class ChecklistItemViewHolder(
        private val context: Context,
        view: View,
        private val checklistsFragment: ChecklistsFragment,
        private val ttsh: TtsHelper,
        private val voskh: VoskHelper,
        private val adapter: RecyclerView.Adapter<ChecklistItemViewHolder>,
        private val airframe: AirframeData,
        private val checklist: Checklist,
        private val section: ChecklistSection
    ) : RecyclerView.ViewHolder(view) {

        private val logTag = "CL_VIEW_HOLDER"

        // TODO: If the section ends, automatically select the next section. This configuration must be changeable.
        private val autoContinue = true

        // view elements
        private val checklistItemView: RelativeLayout = view.findViewById(R.id.checklist_item_row)
        private val visibleDescriptionTextView: TextView =
            checklistItemView.findViewById(R.id.visible_description)
        private val visibleTaskTextView: TextView =
            checklistItemView.findViewById(R.id.visible_task)
        private val checkedStatusImageView: ImageView =
            checklistItemView.findViewById(R.id.status_symbol)

        fun init() {
            val item = section.items[adapterPosition]

            visibleDescriptionTextView.text = item.visibleDescription
            visibleTaskTextView.text =
                if (!item.visibleTask.isNullOrEmpty()) item.visibleTask
                else AirframeData.ChecklistItem.standardTaskName
            checklistItemView.setOnClickListener {
                toggle(AirframeData.ChecklistItem.standardConfirmationPhrase)
            }
            selfFormat()
        }

        private fun toggle(audioConfirmation: String) {
            val sequenceAdapterPosition = adapterPosition
            Log.d(logTag, "Toggling position $sequenceAdapterPosition...")

            voskh.stopListening()

            if (section.items[sequenceAdapterPosition].confirmed) {
                Log.d(logTag, "Cannot unconfirm a confirmed item!")
                return
            }
            if (sequenceAdapterPosition > 0 && !section.items[sequenceAdapterPosition - 1].confirmed) {
                Log.d(logTag, "Cannot jump over items!")
                return
            }

            ttsh.confirmPosition(audioConfirmation)

            section.items[sequenceAdapterPosition].confirmed = true
            Log.d(logTag, "Self formatting position $sequenceAdapterPosition...")
            selfFormat()
            Log.d(logTag, "Self formatting position $sequenceAdapterPosition...done.")

            if (section.items.size == sequenceAdapterPosition + 1) {
                // because unselecting is already handled, the toggle only can be a Task
                Log.d(logTag, "Last element in section confirmed.")

                section.complete = true

                // check if checklist is complete
                if (null == checklist.sections.firstOrNull { !it.complete }) {
                    // all sections of this checklist are complete - so the checklist is
                    checklist.complete = true
                    ttsh.readChecklistComplete(checklist, null)

                    // TODO: Check if there is a next checklist. If not... Selecting checklist and section should be possible via menu.
                    if (null == airframe.checklists.firstOrNull { !it.complete }) {
                        // all checklists of this airframe are complete - so the airframe is
                        airframe.complete = true


                        // if there is no other checklist
                        //Log.d(logTag, "Opening dialog for switch to next checklist.")
                        ViewHelperPopupSelect.showYesNo(context,
                            "CLEAR ALL CHECKLISTS?",
                            "There is no section and/or checklist left. Do you want to clear all checklists?",
                            "YES",
                            "no",
                            fun() {
                                Log.d(
                                    logTag,
                                    "Clearing all checklists!"
                                ); checklistsFragment.reset()
                            },
                            // TODO: Implement behavior! Maybe empty page with showing "No checklist selected."
                            fun() { Log.w(logTag, "Not clearing checklists. TODO: Implement me!") })
                    } else {
                        // if there is another checklist

                        if(!autoContinue) {
                            Log.d(logTag, "Opening dialog for switch to next checklist.")
                            ViewHelperPopupSelect.showYesNo(context,
                                "Next?",
                                "Continue to next checklist?",
                                "YES",
                                "no",
                                fun() { checklistsFragment.setNextChecklistSectionAdapter() },
                                // TODO: Implement behavior! Maybe empty page with showing "No checklist selected."
                                fun() {
                                    Log.w(
                                        logTag,
                                        "Not selecting next section. TODO: Implement me!"
                                    )
                                })
                        } else {
                            checklistsFragment.setNextChecklistSectionAdapter()
                        }
                    }
                } else {
                    // there are more sections to process in the current checklist
                    ttsh.readChecklistComplete(checklist, section)

                    if(!autoContinue) {
                        Log.d(logTag, "Opening dialog for switch to next section.")
                        ViewHelperPopupSelect.showYesNo(context,
                            "Next?",
                            "Continue to next section?",
                            "YES",
                            "no",
                            fun() { checklistsFragment.setNextChecklistSectionAdapter() },
                            // TODO: Implement behavior! Maybe empty page with showing "No checklist selected."
                            fun() {
                                Log.w(
                                    logTag,
                                    "Not selecting next section. TODO: Implement me!"
                                )
                            })
                    } else {
                        checklistsFragment.setNextChecklistSectionAdapter()
                    }
                }

            }

            // handle next element (next unchecked element on list)
            section.items.forEachIndexed { _, _ ->
                run {
                    adapter.notifyItemChanged(sequenceAdapterPosition + 1)
                    checklistsFragment.scrollTo(sequenceAdapterPosition + 1)
                }
            }
        }

        private fun colorRes(@ColorRes colorRes: Int): Int {
            return ContextCompat.getColor(context, colorRes)
        }

        private fun toPx(dp: Int): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics
            )
        }

        private fun selfFormat() {
            val sequenceAdapterPosition = adapterPosition

            if (section.items[sequenceAdapterPosition].confirmed) {
                Log.d(logTag, "CONFIRMED: $sequenceAdapterPosition")

                checklistItemView.setBackgroundColor(colorRes(R.color.cl_text_desc_checked_backgroundColor))
                visibleDescriptionTextView.setTextColor(colorRes(R.color.cl_text_desc_checked_textColor))
                visibleTaskTextView.setBackgroundColor(colorRes(R.color.cl_text_conf_checked_backgroundColor))
                checkedStatusImageView.setBackgroundColor(colorRes(R.color.cl_text_conf_checked_backgroundColor))
                checkedStatusImageView.setImageResource(R.drawable.baseline_check_box_24)
                visibleDescriptionTextView.typeface = Typeface.DEFAULT
                visibleTaskTextView.typeface = Typeface.DEFAULT

                checklistItemView.layoutParams.height = toPx(40).toInt()
                visibleDescriptionTextView.layoutParams.height = toPx(40).toInt()
                visibleTaskTextView.layoutParams.height = toPx(40).toInt()
                checkedStatusImageView.layoutParams.height = toPx(40).toInt()
                checkedStatusImageView.scaleY = 1.0f

            } else {

                if (0 == sequenceAdapterPosition || section.items[sequenceAdapterPosition - 1].confirmed) {
                    Log.d(logTag, "UNCONFIRMED - NEXT: $sequenceAdapterPosition")

                    ttsh.readPositionThenExecute(section.items[sequenceAdapterPosition], fun() {
                        voskh.listen(
                            section.items[sequenceAdapterPosition].acceptedPhrases,
                            5,
                            fun(audioConfirmation: String) {
                            Log.d(logTag, "Triggering...")
                            toggle(audioConfirmation)
                        })
                    })

                    checklistItemView.setBackgroundColor(colorRes(R.color.cl_text_desc_next_backgroundColor))
                    visibleDescriptionTextView.setTextColor(colorRes(R.color.cl_text_desc_next_textColor))
                    visibleTaskTextView.setBackgroundColor(colorRes(R.color.cl_text_conf_next_backgroundColor))
                    checkedStatusImageView.setBackgroundColor(colorRes(R.color.cl_text_conf_next_backgroundColor))
                    checkedStatusImageView.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
                    visibleDescriptionTextView.typeface = Typeface.DEFAULT_BOLD
                    visibleTaskTextView.typeface = Typeface.DEFAULT_BOLD

                    checklistItemView.layoutParams.height = toPx(60).toInt()
                    visibleDescriptionTextView.layoutParams.height = toPx(60).toInt()
                    visibleTaskTextView.layoutParams.height = toPx(60).toInt()
                    checkedStatusImageView.layoutParams.height = toPx(60).toInt()
                    checkedStatusImageView.scaleY = 1.5f

                } else {
                    Log.d(logTag, "UNCONFIRMED: $sequenceAdapterPosition")

                    if (sequenceAdapterPosition % 2 == 0) {
                        checklistItemView.setBackgroundColor(colorRes(R.color.cl_text_desc_unchecked_even_backgroundColor))
                    } else {
                        checklistItemView.setBackgroundColor(colorRes(R.color.cl_text_desc_unchecked_odd_backgroundColor))
                    }
                    visibleDescriptionTextView.setTextColor(colorRes(R.color.cl_text_desc_unchecked_textColor))
                    visibleTaskTextView.setBackgroundColor(colorRes(R.color.cl_text_conf_unchecked_backgroundColor))
                    checkedStatusImageView.setBackgroundColor(colorRes(R.color.cl_text_conf_unchecked_backgroundColor))
                    checkedStatusImageView.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
                    visibleDescriptionTextView.typeface = Typeface.DEFAULT
                    visibleTaskTextView.typeface = Typeface.DEFAULT

                    checklistItemView.layoutParams.height = toPx(40).toInt()
                    visibleDescriptionTextView.layoutParams.height = toPx(40).toInt()
                    visibleTaskTextView.layoutParams.height = toPx(40).toInt()
                    checkedStatusImageView.layoutParams.height = toPx(40).toInt()
                    checkedStatusImageView.scaleY = 1.0f
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistItemViewHolder {
        Log.d(logTag, "Called onCreateViewHolder().")

        // create a new view
        val adapterLayoutView =
            LayoutInflater.from(parent.context).inflate(R.layout.checklist_item_view, parent, false)

        return ChecklistItemViewHolder(
            context,
            adapterLayoutView,
            checklistsFragment,
            ttsh,
            voskh,
            this,
            airframe,
            checklist,
            section
        )
    }

    override fun getItemCount(): Int {
        Log.v(logTag, "Called getItemCount(). Result: " + section.items.size)
        return section.items.size
    }

    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        Log.d(logTag, "Called onBindViewHolder().")
        holder.init()
    }
}
