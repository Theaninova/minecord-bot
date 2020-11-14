package de.wulkanat

import de.wulkanat.files.Config
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import kotlin.concurrent.timer

object MinecraftServer {
    private var process: Process? = null
    private var inputStream: InputStream? = null
    var outputStream: OutputStream? = null
        private set
    var streamCollector: (String) -> Unit = {}
    var onStatusChanged: (Boolean) -> Unit = {}
    var alive = false

    init {
        timer("OutputStream", daemon = true, initialDelay = 0L, period = Config.streamUpdateFrequency) {
            when (val status = process?.isAlive == true) {
                true -> {
                    inputStream?.readBytes()?.let {
                        streamCollector(String(it))
                    }
                }
                !alive -> {
                    alive = status
                    onStatusChanged(alive)
                }
            }
        }
    }

    fun start() {
        process = Runtime.getRuntime().exec("java -jar ${Config.serverJarName}")
        inputStream = process?.inputStream
        outputStream = process?.outputStream
    }

    fun stop() {
        process?.destroy()
        inputStream = null
        outputStream = null
    }

    fun restart() {
        GlobalScope.launch {
            stop()
            delay(1000)
            start()
        }
    }
}
