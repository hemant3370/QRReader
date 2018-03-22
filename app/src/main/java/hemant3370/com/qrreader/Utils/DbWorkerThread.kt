package hemant3370.com.qrreader.Utils

import android.os.Handler
import android.os.HandlerThread

/**
 * Created by hemantsingh on 22/03/18.
 */
class DbWorkerThread(threadName: String) : HandlerThread(threadName) {

    private lateinit var mWorkerHandler: Handler

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mWorkerHandler = Handler(looper)
    }

    fun postTask(task: Runnable) {
        mWorkerHandler.post(task)
    }

}