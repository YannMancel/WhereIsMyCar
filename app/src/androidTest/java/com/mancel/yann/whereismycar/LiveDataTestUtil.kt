package com.mancel.yann.whereismycar

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Created by Yann MANCEL on 13/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar
 */
object LiveDataTestUtil {

    // See: https://github.com/android/architecture-components-samples/blob/1532c562bb4d25f8469471478195160fd96b5db7/BasicSample/app/src/androidTest/java/com/example/android/persistence/LiveDataTestUtil.java

    // METHODS -------------------------------------------------------------------------------------

    /**
     * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds.
     * Once we got a notification via onChanged, we stop observing.
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(InterruptedException::class)
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data[0] = o
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        return data[0] as T
    }
}