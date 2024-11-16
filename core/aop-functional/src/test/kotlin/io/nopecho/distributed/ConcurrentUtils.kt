package io.nopecho.distributed

import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ConcurrentUtils {
    private const val DEFAULT_THREAD_COUNT = 200

    fun run(threadCount: Int = DEFAULT_THREAD_COUNT, task: () -> Unit): List<Throwable> {
        val result = mutableListOf<Throwable>()
        val (executor, startLatch, doneLatch) = setup(threadCount)

        repeat(threadCount) {
            executor.submit {
                try {
                    startLatch.await()
                    task()
                } catch (e: Throwable) {
                    synchronized(result) {
                        println("error: ${e.message}")
                        result.add(e)
                    }
                } finally {
                    doneLatch.countDown()
                }
            }
        }
        release(executor, startLatch, doneLatch)
        return result
    }

    fun <T> call(threadCount: Int = DEFAULT_THREAD_COUNT, task: () -> T): List<T> {
        val result = mutableListOf<T>()
        val (executor, startLatch, doneLatch) = setup(threadCount)
        repeat(threadCount) {
            executor.submit {
                try {
                    startLatch.await()
                    synchronized(result) {
                        result.add(task())
                    }
                } catch (e: Throwable) {
                    println("error: ${e.message}")
                } finally {
                    doneLatch.countDown()
                }
            }
        }
        release(executor, startLatch, doneLatch)
        return result
    }

    private fun release(
        executor: ExecutorService,
        startLatch: CountDownLatch,
        doneLatch: CountDownLatch,
    ) {
        startLatch.countDown()
        doneLatch.await()
        executor.shutdown()
    }

    private fun setup(threadCount: Int): Triple<ExecutorService, CountDownLatch, CountDownLatch> {
        val executor = Executors.newFixedThreadPool(threadCount)
        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threadCount)
        return Triple(executor, startLatch, doneLatch)
    }
}