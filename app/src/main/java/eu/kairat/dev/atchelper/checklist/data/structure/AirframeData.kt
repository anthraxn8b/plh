package eu.kairat.dev.atchelper.checklist.data.structure

data class AirframeData(
    val airframe   : String,
    val airframeId : String,
    val checklists : List<Checklist>,
    var complete   : Boolean
) {

    data class Checklist(
        val name     : String,
        val sections : List<ChecklistSection>,
        var complete : Boolean
    )
    data class ChecklistSection(
        val name     : String,
        val items    : List<ChecklistItem>,
        var complete : Boolean
    )
    data class ChecklistItem(
        val visibleDescription         : String,
        val audioDescription           : String?,
        val visibleTask                : String?,
        val audioTask                  : String?,
        private val audioConfirmations : List<String>?,
        var confirmed                  : Boolean
    ) {
        private val prepListAcceptedPhrases = mutableListOf<String>()
        init {
            prepListAcceptedPhrases.add(standardTaskName)
            prepListAcceptedPhrases.add(standardConfirmationPhrase)
            if (!audioConfirmations.isNullOrEmpty()) {
                audioConfirmations.forEach { prepListAcceptedPhrases.add(it.lowercase()) }
            }
        }

        var acceptedPhrases = prepListAcceptedPhrases.toList()

        companion object {
            const val standardTaskName = "check"
            const val standardConfirmationPhrase = "checked"
        }
    }
}
