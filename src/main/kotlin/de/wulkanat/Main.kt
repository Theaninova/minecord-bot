package de.wulkanat

import de.wulkanat.files.Config
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Activity

val jda: JDA = JDABuilder.createDefault(Config.token).apply {
    setActivity(Activity.playing("Minecraft"))
    addEventListeners(
        CliAdapter(),
        AdminCliAdapter(),
        ErrorHandler(),
        ServerInputAdapter(),
    )
}.build().awaitReady()

fun main() {
    Admin.jda = jda
    Admin.info()

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            println("Shutting down...")
            println("Sending shutdown notice to Admin, waiting 5s...")
            Admin.println("Shutting down")
            sleep(5000)
        }
    })

    jda.getTextChannelById(Config.outputChannelId)?.let { channel ->
        MinecraftServer.onStatusChanged = {
            if (it) {
                jda.presence.activity = Activity.watching("for start command")
                jda.presence.isIdle = true
            } else {
                jda.presence.activity = Activity.playing("Minecraft")
                jda.presence.isIdle = false
            }
        }
        MinecraftServer.streamCollector = {
            channel.sendMessage(it)
        }
    }
}
