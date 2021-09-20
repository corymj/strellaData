import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

val dataPoints: MutableList<DataPoint> = mutableListOf()

fun getMean(): Double {
    val data = dataPoints.map{it -> it.value}
    return data.average()
}

fun median(l: List<Int>) = l.sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }

fun getMedian(): Int {
    val data = dataPoints.map{it -> it.value}
    return median(data)
}

fun <T> modeOf(a: MutableList<T>): String {
    val sortedByFreq = a.groupBy { it }.entries.sortedByDescending { it.value.size }
    val maxFreq = sortedByFreq.first().value.size
    val modes = sortedByFreq.takeWhile { it.value.size == maxFreq }
    return if (modes.size == 1)
        "The mode of the collection is ${modes.first().key} which has a frequency of $maxFreq"
    else {
        "There are ${modes.size} modes with a frequency of $maxFreq, namely : " +
                modes.map { it.key }.joinToString(", ")
    }
}

fun getMode(): String {
    val data = dataPoints.map{ it -> it.value} as MutableList<Int>
    return modeOf(data)
}

fun main() {
    embeddedServer(Netty, port=9090) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
            route(DataPoint.path) {
                get {
                    call.respond(dataPoints)
                }
                get("/mean") {
                    call.respond(getMean())
                }
                get("/median") {
                    call.respond(getMedian())
                }
                get("/mode") {
                    call.respond(getMode())
                }
                post {
                    // add data points here
                    dataPoints.add(call.receive<DataPoint>())
                    call.respond(HttpStatusCode.OK)
                }
                static {
                    resources("")
                }
            }
            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
        }
    }.start(wait = true)
}