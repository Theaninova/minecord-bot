package de.wulkanat.extensions

import Inaccessibles
import de.wulkanat.discordui.ColorEmoji
import de.wulkanat.discordui.Emoji
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageAction
import net.dv8tion.jda.internal.requests.Method
import net.dv8tion.jda.internal.requests.Route
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl
import net.dv8tion.jda.internal.utils.Checks
import java.util.concurrent.TimeUnit

fun MessageChannel.crosspostById(messageId: String): MessageAction {
    Checks.isSnowflake(messageId, "Message ID")

    val route = CROSSPOST_MESSAGE.compile(id, messageId)
    return MessageActionImpl(jda, route, this).append("This is not of your interest.")
}

fun Message.crosspost(): MessageAction {
    val messageId = Inaccessibles.toUnsignedString(idLong)

    return channel.crosspostById(messageId)
}

val CROSSPOST_MESSAGE: Route = Inaccessibles.getRoute(
    Method.POST,
    "channels/{channel_id}/messages/{message_id}/crosspost"
)

fun Message.reAddColor(color: ColorEmoji) {
    clearReactions(ColorEmoji.NONE).queue {
        addReaction(color).queue {
            addReaction(ColorEmoji.NONE).queue()
        }
    }
}

fun Message.addReaction(color: ColorEmoji): RestAction<Void> {
    return if (color.customEmoji != null) {
        addReaction(color.customEmoji!!)
    } else {
        addReaction(color.unicode)
    }
}

fun Message.clearReactions(color: ColorEmoji): RestAction<Void> {
    return if (color.customEmoji != null) {
        clearReactions(color.customEmoji!!)
    } else {
        clearReactions(color.unicode)
    }
}

fun MessageAction.queueSelfDestruct(seconds: Long) {
    queue { it.delete().queueAfter(seconds, TimeUnit.SECONDS) }
}

fun Message.addControlReactions(deafened: Boolean, mute: Boolean): Message {
    mutableListOf(
        addReaction(Emoji.STOP_BUTTON.unicodeEmote),
        addReaction(Emoji.OBSERVER.unicodeEmote),
        addReaction(Emoji.REPEAT.unicodeEmote),
        addReaction(Emoji.SKULL.unicodeEmote),
    ).alsoIf(mute) {
        addAll(
            clearReactions(if (deafened) Emoji.MUTE.unicodeEmote else Emoji.SPEAKER.unicodeEmote),
            addReaction(if (deafened) Emoji.SPEAKER.unicodeEmote else Emoji.MUTE.unicodeEmote)
        )
    }.queueAllSafe()

    return this
}

fun Message.regenerateDeadMute(deafened: Boolean, mute: Boolean): Message {
    mutableListOf(
        clearReactions(Emoji.REPEAT.unicodeEmote),
        clearReactions(Emoji.SKULL.unicodeEmote)
    ).alsoIf(mute) {
        addAll(
            clearReactions(Emoji.MUTE.unicodeEmote),
            clearReactions(Emoji.SPEAKER.unicodeEmote),
        )
    }.alsoAdd(
        addReaction(Emoji.REPEAT.unicodeEmote),
        addReaction(Emoji.SKULL.unicodeEmote),
    ).alsoIf(mute) {
        add(addReaction(if (deafened) Emoji.SPEAKER.unicodeEmote else Emoji.MUTE.unicodeEmote))
    }.queueAllSafe()

    return this
}

fun Message.refreshControlReactions(deafened: Boolean, mute: Boolean): Message {
    if (!mute) return this

    listOf(
        clearReactions(if (deafened) Emoji.MUTE.unicodeEmote else Emoji.SPEAKER.unicodeEmote),
        addReaction(if (deafened) Emoji.SPEAKER.unicodeEmote else Emoji.MUTE.unicodeEmote),
    ).queueAllSafe()

    return this
}

fun Message.addColorReactions(): Message {
    val list = mutableListOf<RestAction<Void>>()
    for (value in ColorEmoji.values()) {
        if (value.customEmoji == null) {
            list.add(addReaction(value.unicode))
        } else {
            list.add(addReaction(value.customEmoji!!))
        }
    }
    list.queueAllSafe()

    return this
}
