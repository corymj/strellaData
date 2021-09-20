import kotlinx.serialization.*

@Serializable
data class DataPoint(val value: Int) {
    val id: Int = value.hashCode()

    companion object {
        const val path = "/data"
    }
}
