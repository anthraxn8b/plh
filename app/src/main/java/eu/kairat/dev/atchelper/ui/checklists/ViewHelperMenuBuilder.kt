package eu.kairat.dev.atchelper.ui.checklists

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import eu.kairat.dev.atchelper.MainActivity
import eu.kairat.dev.atchelper.R

class ViewHelperMenuBuilder {

    interface ChecklistMenu {
        fun reset()
        fun gotoNext()
        fun select()
        fun toggleStt()
    }

    companion object {
        private const val logTag = "VIEW HELPER MENU BUILDER"
        
        fun buildMenu(
            mainActivity: MainActivity,
            menuFunctions: ChecklistMenu,
            viewLifecycleOwner: LifecycleOwner
        ) {
            val mp = object : MenuProvider {

                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_checklists, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId) {
                        R.id.reset -> {
                            Log.d(logTag, "MENU: RESET")
                            menuFunctions.reset()
                            return true
                        }
                        R.id.next_section ->  {
                            Log.d(logTag, "MENU: NEXT SECTION")
                            menuFunctions.gotoNext()
                            return true
                        }
                        R.id.select_checklist -> {
                            Log.d(logTag, "MENU: CHECKLISTS")
                            menuFunctions.select()
                            return true
                        }
                        R.id.toggle_stt -> {
                            Log.d(logTag, "MENU: TOGGLE STT")
                            menuFunctions.toggleStt()
                            return true
                        }
                    }
                    return false
                }

            }

            // Add the MenuProvider to the MenuHost
            val menuHost: MenuHost = mainActivity
            menuHost.addMenuProvider(
                mp, // your Fragment implements MenuProvider, so we use this here
                viewLifecycleOwner, // Only show the Menu when your Fragment's View exists
                Lifecycle.State.RESUMED // And when the Fragment is RESUMED
            )

        }
    }
}
