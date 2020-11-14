package de.wulkanat.extensions

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.util.concurrent.TimeUnit

fun MessageAction.queueSelfDestruct(seconds: Long) {
    queue { it.delete().queueAfter(seconds, TimeUnit.SECONDS) }
}

val Message.isFromSelfUser: Boolean
    get() {
        return author.idLong == jda.selfUser.idLong
    }
