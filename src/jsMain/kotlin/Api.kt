import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

import kotlinx.browser.window

val endpoint = window.location.origin

val jsonClient = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

suspend fun getDataPoints(): List<DataPoint> {
    return jsonClient.get(endpoint + DataPoint.path)
}

suspend fun getMean(): Double {
    return jsonClient.get(endpoint + DataPoint.path + "/mean")
}

suspend fun getMedian(): Int {
    return jsonClient.get(endpoint + DataPoint.path +"/median")
}

suspend fun getMode(): String {
    return jsonClient.get(endpoint + DataPoint.path + "/mode")
}

suspend fun addDataPoint(dataPoint: DataPoint) {
    jsonClient.post<Unit>(endpoint + DataPoint.path) {
        contentType(ContentType.Application.Json)
        body = dataPoint
    }
}
