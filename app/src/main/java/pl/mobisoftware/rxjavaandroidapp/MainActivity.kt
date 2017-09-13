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
        val sinusTemperature = Observable.intervalRange(0, 360,0, 10, TimeUnit.MILLISECONDS)
                .repeat()
                .map { Math.sin(Math.toRadians(it.toDouble()))
                        .times(4)
                }
                .map { it.plus(10).toFloat()  }


        graphView = findViewById(R.id.temperatureGraph)


        val reactorsTemperature = Observable.combineLatest(reactor1Temperature, reactor2Temperature, sinusTemperature,
                Function3<Float, Float, Float, TemperaturesModel> { t1, t2, t3 -> TemperaturesModel(temperature1 = t1, temperature2 = t2, temperature3 = t3) })

        reactorsTemperatureDisposable = Observable.interval(0, 16, TimeUnit.MILLISECONDS)
                .withLatestFrom(reactorsTemperature, BiFunction<Long, TemperaturesModel, TemperaturesModel> { t1, t2 -> t2 })
                .scan { (index), currentTemp ->
                    currentTemp.index = index +1
                    currentTemp
                }
                .doOnNext { Timber.d("Emit temperature") }
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
