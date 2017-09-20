package pl.mobisoftware.rxjavaandroidapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import pl.mobisoftware.rxjavaandroidapp.views.TemperatureGraphView
import pl.mobisoftware.rxjavaandroidapp.views.TemperaturesModel
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var graphView: TemperatureGraphView

    var reactorsTemperatureDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)

        val reactor1Temperature = getReactorTemperature(3400)
        val reactor2Temperature = getReactorTemperature(1800)
        val reactor3Temperature = getReactorTemperature(1200)


        graphView = findViewById(R.id.temperatureGraph)
        graphView.maxPointsPerX = 1000


        val reactorsTemperature = Observable.combineLatest(reactor1Temperature, reactor2Temperature, reactor3Temperature,
                Function3<Float, Float, Float, TemperaturesModel> { t1, t2, t3 -> TemperaturesModel(temperature1 = t1, temperature2 = t2, temperature3 = t3) })


        Observable.interval(0, 16, TimeUnit.MILLISECONDS)
                .withLatestFrom(reactorsTemperature, BiFunction<Long, TemperaturesModel, TemperaturesModel> { aLong, t2 -> t2 })
//        reactorsTemperature

                .scan(BiFunction<TemperaturesModel, TemperaturesModel, TemperaturesModel> { previousTemp, currentTemp ->
                    currentTemp.index = previousTemp.index + 1
                    currentTemp
                }
                )
                .doOnError { it.printStackTrace() }
                .doOnComplete { Timber.d("OnComplete") }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(graphView)
    }

    override fun onDestroy() {
        super.onDestroy()
        reactorsTemperatureDisposable?.dispose()
    }

    fun getReactorTemperature(interval: Long): Observable<Float> {
        val randomTemp = Random()
        return Observable.interval(0L, interval, TimeUnit.MILLISECONDS)
                .map { randomTemp.nextFloat().times(20).plus(20) }
    }


}
