package com.wkq.util.log

import java.io.PrintWriter
import java.io.StringWriter

/**
 * Optional global crash handler used by ALog.
 *
 * It records the uncaught exception and then delegates to the previous handler
 * so the host app keeps its normal crash behavior.
 */
internal class CrashHandler : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(t: Thread, e: Throwable) {
        ALog.e("Crash", buildCrashText(t, e))
        ALog.flushSync()
        ALog.closeSync()

        defaultHandler?.uncaughtException(t, e)
    }

    private fun buildCrashText(thread: Thread, throwable: Throwable): String {
        val writer = StringWriter()
        PrintWriter(writer).use { printWriter ->
            printWriter.println("Uncaught exception in thread: ${thread.name}")
            throwable.printStackTrace(printWriter)
        }
        return writer.toString()
    }
}
