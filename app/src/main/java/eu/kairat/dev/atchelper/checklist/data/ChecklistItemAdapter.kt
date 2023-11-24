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
    //private val stth: SttHelper,
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
        //private val stth: SttHelper,
        private val voskh: VoskHelper,
        private val adapter: RecyclerView.Adapter<ChecklistItemViewHolder>,
        private val airframe: AirframeData,
        private val checklist: Checklist,
        private val section: ChecklistSection
    ) : RecyclerView.ViewHolder(view) {

        private val logTag = "CL VIEW HOLDER"

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
            var task = "check"
            if (!item.visibleTask.isNullOrEmpty()) {
                task = item.visibleTask
            }
            visibleTaskTextView.text = task
            checklistItemView.setOnClickListener {
                toggle()
            }
            selfFormat()
        }

        private fun toggle() {
            //stth.stopListening()
            voskh.stopListening()

            if (section.items[adapterPosition].confirmed) {
                Log.d(logTag, "Cannot unconfirm a confirmed item!")
                return
            }
            if (adapterPosition > 0 && !section.items[adapterPosition - 1].confirmed) {
                Log.d(logTag, "Cannot jump over items!")
                return
            }

            ttsh.confirmPosition(section.items[adapterPosition])

            section.items[adapterPosition].confirmed = true
            selfFormat()

            if (section.items.size == adapterPosition + 1) {
                // because unselecting is already handled, the toggle only can be a Task
                Log.d(logTag, "Last element in section confirmed.")

                section.complete = true

                // check if checklist is complete
                if (null == checklist.sections.firstOrNull { !it.complete }) {
                    // all sections of this checklist are complete - so the checklist is
                    checklist.complete = true

                    // TODO: Check if there is a next checklist. If not... Selecting checklist and section should be possible via menu.

                    // if there is another checklist
                    Log.d(logTag, "Opening dialog for switch to next checklist.")
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
                    // there are more sections to process in the current checklist
                    Log.d(logTag, "Opening dialog for switch to next section.")

                    ViewHelperPopupSelect.showYesNo(context,
                        "Next?",
                        "Continue to next section?",
                        "YES",
                        "no",
                        fun() { checklistsFragment.setNextChecklistSectionAdapter() },
                        // TODO: Implement behavior! Maybe empty page with showing "No checklist selected."
                        fun() { Log.w(logTag, "Not selecting next section. TODO: Implement me!") })
                }

            }

            // handle next element (next unchecked element on list)
            section.items.forEachIndexed { _, _ ->
                run {
                    adapter.notifyItemChanged(adapterPosition + 1)
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

            if (section.items[adapterPosition].confirmed) {
                Log.d(logTag, "CONFIRMED: $adapterPosition")

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

                if (0 == adapterPosition || section.items[adapterPosition - 1].confirmed) {
                    Log.d(logTag, "UNCONFIRMED - NEXT: $adapterPosition")

                    ttsh.readPositionThenExecute(section.items[adapterPosition], fun() {
                        //stth.listen(fun(text: String) {
                        voskh.listen(listOf("check", "checked"), 5, fun() {
                            Log.d(logTag, "Triggering...")
                            toggle()
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
                    Log.d(logTag, "UNCONFIRMED: $adapterPosition")

                    if (adapterPosition % 2 == 0) {
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
            ttsh, /*stth*/
            voskh,
            this,
            airframe,
            checklist,
            section
        )
    }

    override fun getItemCount(): Int {
        Log.d(logTag, "Called getItemCount(). Result: " + section.items.size)
        return section.items.size
    }

    override fun onBindViewHolder(holder: ChecklistItemViewHolder, position: Int) {
        Log.d(logTag, "Called onBindViewHolder().")
        holder.init()
    }
}/*
fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    val color = ContextCompat.getColor(context, colorRes)
    setTextColor(color)
}
*/
