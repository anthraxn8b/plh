package eu.kairat.dev.atchelper.checklist.data.structure

data class AirframeData(
    val airframe : String,
    val airframeId : String,
    val checklists : List<Checklist>
) {
    data class Checklist(
        val name : String,
        var complete : Boolean,
        val sections : List<ChecklistSection>
    )
    data class ChecklistSection(
        val name : String,
        var complete : Boolean,
        val items : List<ChecklistItem>
    )
    data class ChecklistItem(
        val visibleDescription:  String,
        val audioDescription:    String?,
        val visibleTask: String?,
        val audioTask:   String?,
        val audioConfirmation:   String?,
        var confirmed:           Boolean
    )
}