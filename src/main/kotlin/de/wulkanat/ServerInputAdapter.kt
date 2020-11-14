package de.wulkanat

import de.wulkanat.extensions.isFromSelfUser
import de.wulkanat.files.Config
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ServerInputAdapter : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.isFromSelfUser || event.channel.idLong != Config.outputChannelId) return
        MinecraftServer.outputStream?.write(event.message.contentRaw.toByteArray())
    }
}
