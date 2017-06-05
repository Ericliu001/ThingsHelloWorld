package com.example.ericliu.thingshelloworld

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.lang.ref.WeakReference


/**
 * Created by ericliu on 26/11/16.
 * A composite of a [Handler] to schedule operations periodically.
 */

class ViewRefreshHandler {

    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val decoratorMap: MutableMap<Class<out ViewRunnable<out Context>>, RunnableDecorator>

    init {
        decoratorMap = mutableMapOf<Class<out ViewRunnable<out Context>>, RunnableDecorator>()
    }

    fun executePerSecond(task: ViewRunnable<*>) {
        executePeriodically(task, MINI_SECS_ONE_SECOND)
    }

    fun executePerMinute(task: ViewRunnable<*>) {
        executePeriodically(task, MINI_SECS_ONE_MINUTE)
    }

    fun executePeriodically(task: ViewRunnable<*>, interval: Long) {
        cancelPendingTask(task)

        /**
         * creates a decorator class of the runnable being passed in.
         * after executing the run method in the runnable, call [.scheduleNext]
         * to schedule the next call.
         */
        val runnableDecorator = RunnableDecorator(task, interval)
        mHandler.post(runnableDecorator)

        decoratorMap.put(task.javaClass, runnableDecorator)
    }


    private inner class RunnableDecorator(private val runnable: ViewRunnable<*>, private val interval: Long) : Runnable {

        override fun run() {
            runnable.run()
            if (runnable.viewRef.get() != null && !runnable.terminate) {
                scheduleNext(this, interval)
            } else {
                // stop refreshing when the View is gone.
                cancelPendingTask(runnable)
            }
        }
    }

    private fun scheduleNext(runnable: RunnableDecorator?, interval: Long) {
        if (runnable != null) {
            mHandler.postDelayed(runnable, interval)
        }
    }

    private fun cancelPendingTask(task: ViewRunnable<*>?) {
        if (task != null) {
            val runnableDecorator = decoratorMap[task.javaClass]
            if (runnableDecorator != null) {
                mHandler.removeCallbacks(runnableDecorator)
                decoratorMap.remove(task.javaClass)
            }
        }
    }

    fun cancelAll() {
        mHandler.removeCallbacksAndMessages(null)
        decoratorMap.clear()
    }

    /**
     * A subclass class of [Runnable] which only holds a WeakReference of a View object,
     *
     *
     * as a result, it avoids the problem of memory leak caused by not releasing references of View objects.

     * @param <T> - the View instance to be operated on.
    </T> */
    abstract class ViewRunnable<T : Context>(view: T, args: Bundle?) : Runnable {
        internal val viewRef: WeakReference<T> = WeakReference(view)
        private val mArgs: Bundle
        internal var terminate = false // set true to stop executing repeating tasks

        init {
            if (args == null) {
                mArgs = Bundle()
            } else {
                mArgs = args
            }
        }

        override fun run() {
            val view = viewRef.get()
            if (view != null) {
                run(view, mArgs)
            }
        }

        /**
         * stop scheduling tasks
         */
        protected fun terminate() {
            terminate = true
        }

        protected abstract fun run(view: T, args: Bundle)
    }

    companion object {
        private val MINI_SECS_ONE_MINUTE = (1000 * 60).toLong()
        private val MINI_SECS_ONE_SECOND: Long = 1000
    }
}
