package de.wulkanat

import de.wulkanat.cli.Cli
import de.wulkanat.cli.discordUsageEmbed
import de.wulkanat.cli.makeCli
import de.wulkanat.extensions.queueSelfDestruct
import de.wulkanat.files.Config
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class CliAdapter : ListenerAdapter() {
    private val cli: Cli<MessageReceivedEvent> = makeCli(prefix = "$$") {
        command name "reset" does "Reset the server and restart" through reset
        command name "stop" does "Stop the server" through stop
        command name "start" does "Start the server" through start
        command name "restart" does "Restart the server" through restart
        command name "here" does "Set the current channel to receive server output" through here
    }

    private val reset: (Any, Any, MessageReceivedEvent) -> Unit = { _, _, _ ->
        TODO()
    }

    private val stop: (Any, Any, MessageReceivedEvent) -> Unit = { _, _, _ ->
        MinecraftServer.stop()
    }

    private val start: (Any, Any, MessageReceivedEvent) -> Unit = { _, _, _ ->
        MinecraftServer.start()
    }

    private val restart: (Any, Any, MessageReceivedEvent) -> Unit = { _, _, _ ->
        MinecraftServer.restart()
    }

    private val here: (Any, Any, MessageReceivedEvent) -> Unit = { _, _, event ->
        Config.outputChannelId = event.channel.idLong
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.author.isBot || event.channelType == ChannelType.PRIVATE) return
        val msg = event.message.contentRaw

        cli.parse(
            msg, event,
            commandMisuse = { command, message ->
                event.message.channel.sendMessage(command.discordUsageEmbed(message)).queueSelfDestruct(10)
                event.message.delete().queue()
            },
            helpMessage = {
                event.message.channel.sendMessage(it.discordUsageEmbed()).queueSelfDestruct(10)
                event.message.delete().queue()
            }
        )
    }
}
