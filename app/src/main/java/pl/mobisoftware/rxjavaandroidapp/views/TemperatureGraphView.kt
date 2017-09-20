package pl.mobisoftware.rxjavaandroidapp.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import io.reactivex.functions.Consumer

/**
 * Created by dpodolak on 07.09.2017.
 */

class TemperatureGraphView : GraphView, Consumer<TemperaturesModel> {

    var maxPointsPerX = 100
        set(value) {
            viewport.setMaxX(value.toDouble())
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun accept(t: TemperaturesModel) {
        temp1Graph.appendData(DataPoint(t.index.toDouble(), t.temperature1.toDouble()), true, 1000)
        temp2Graph.appendData(DataPoint(t.index.toDouble(), t.temperature2.toDouble()), true, 1000)
        temp3Graph.appendData(DataPoint(t.index.toDouble(), t.temperature3.toDouble()), true, 1000)
    }

    val temp1Graph = LineGraphSeries<DataPoint>()
    val temp2Graph = LineGraphSeries<DataPoint>()
    val temp3Graph = LineGraphSeries<DataPoint>()

    init {
        temp1Graph.color = Color.RED
        temp2Graph.color = Color.BLUE
        temp3Graph.color = Color.GREEN

        viewport.isXAxisBoundsManual = true

        temp1Graph.title = "Temperature 1"
        temp2Graph.title = "Temperature 2"
        temp3Graph.title = "Temperature 3"

        legendRenderer.isVisible = true
        legendRenderer.setFixedPosition(0, 0)

        addSeries(temp1Graph)
        addSeries(temp2Graph)
        addSeries(temp3Graph)

        viewport.setMinX(0.0)
        viewport.setMaxX(maxPointsPerX.toDouble())
        viewport.setMaxY(50.0)
        viewport.setMinY(0.0)
    }

}
