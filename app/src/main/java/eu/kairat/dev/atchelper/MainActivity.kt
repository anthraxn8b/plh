package eu.kairat.dev.atchelper

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import eu.kairat.dev.atchelper.checklist.data.AirframeChecklistsDatasource
import eu.kairat.dev.atchelper.checklist.data.TtsHelper
import eu.kairat.dev.atchelper.checklist.data.VoskHelper
import eu.kairat.dev.atchelper.checklist.data.structure.AirframeData
import eu.kairat.dev.atchelper.databinding.ActivityMainBinding
import eu.kairat.dev.atchelper.ui.SharedViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var selectedAirframeIndex = 0

    lateinit var ttsh: TtsHelper

    lateinit var voskh : VoskHelper

    lateinit var airframeData : List<AirframeData>

    private var sharedViewModel : SharedViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        sharedViewModel!!.callsign.observe(this, Observer {
            // TODO: Perform an action with the latest item data.
            Log.d("MAIN ACT", "CALLSIGN CHANGED!")
        })

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_init, R.id.navigation_checklists, R.id.navigation_atc))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Log.d("MAIN ACT", "Loading custom data...")

        Log.d("MAIN ACT", "Load AIRFRAMES...")
        airframeData = AirframeChecklistsDatasource().loadAirframeChecklist(this)

        Log.d("MAIN ACT", "Load TTS...")
        ttsh = TtsHelper(this)

        Log.d("MAIN ACT", "Load STT...")
        voskh = VoskHelper(this)

        Log.d("MAIN ACT", "Loading custom data...DONE.")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.reset -> return true
            R.id.next_section -> return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsh.destroy()
        voskh.onDestroy()
    }
}

fun Context.getAirframeChecklists() : List<AirframeData> {
    return AirframeChecklistsDatasource().loadAirframeChecklist(this)
}
