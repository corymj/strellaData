import react.*
import react.dom.*
import kotlinext.js.*
import kotlinx.html.js.*
import kotlinx.coroutines.*

private val scope = MainScope()

val App = fc<PropsWithChildren> { _ ->
    val (dataPoints, setDataPoints) = useState(emptyList<DataPoint>())
    val (mean, setMean) = useState<Double>(0.0)
    val (median, setMedian) = useState(0)
    val (mode, setMode) = useState<String>("")

    useEffect {
        scope.launch {
            setDataPoints(getDataPoints())
            setMean(getMean())
            setMedian(getMedian())
            setMode(getMode())
        }
    }
    h1 {
        +"Data Points in Full-Stack Kotlin"
    }
    ul {
        dataPoints.forEach { item ->
            +"${item.value} "
        }
        li {
            +"Mean: $mean"
        }
        li {
            +"Median: $median"
        }
        li {
            +"Mode: $mode"
        }
    }
    child(
        InputComponent,
        props = jsObject {
            onSubmit = { input ->
                val dataPoint = DataPoint(input.toInt())
                scope.launch {
                    addDataPoint(dataPoint)
                    setDataPoints(getDataPoints())
                }
            }
        }
    )
}