package eu.kairat.dev.atchelper.checklist.data

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import eu.kairat.dev.atchelper.checklist.data.structure.AirframeData

class AirframeChecklistsDatasource {

    fun loadAirframeChecklist(ctx : Context): List<AirframeData> {

        return ctx.assets.readAssetFileNames(".airframedata.json").map {
            Gson().fromJson(
                ctx.assets.readAssetFile(it),
                AirframeData::class.java
            )
        }
    }

    private fun AssetManager.readAssetFile(fileName : String) : String {
        return open(fileName).bufferedReader().use { it.readText() }
    }

    private fun AssetManager.readAssetFileNames(suffix : String) : List<String> {
        val files = list("")
        if (null != files) {
            return files.filterNotNull().filter {it.endsWith(suffix)}
        }
        return listOf()
    }
}
