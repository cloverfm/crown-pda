package com.sf.cwms.util

import android.os.Handler
import android.os.Looper
import android.os.Message

abstract class ThreadTask<T1, T2, T3> : Runnable {
    // Argument
    abstract var mArgument1: T1
    abstract var mArgument2: T2

    // Result
    abstract var mResult: T3

    // Handle the result
    val WORK_DONE = 0
    var mResultHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            // Call onPostExecute
            onPostExecute(mResult)
        }
    }

    // Execute
    fun execute(arg1: T1, arg2 : T2) {
        // Store the argument
        mArgument1 = arg1
        mArgument2 = arg2

        // Call onPreExecute
        onPreExecute()

        // Begin thread work
        val thread = Thread(this)
        thread.start()
    }

    override fun run() {
        // Call doInBackground
        mResult = doInBackground(mArgument1, mArgument2)

        // Notify main thread that the work is done
        mResultHandler.sendEmptyMessage(WORK_DONE)
    }

    // onPreExecute
    protected abstract fun onPreExecute()

    // doInBackground
    protected abstract fun doInBackground(arg1: T1, arg2:T2): T3

    // onPostExecute
    protected abstract fun onPostExecute(result: T3)
}