package de.wulkanat.files

import de.wulkanat.files.concept.AutoSaveSerializable
import de.wulkanat.files.concept.SerializableObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

object Config : SerializableObject<Config.Data>("minecord.json", Data(), Data.serializer()) {
    override var parent: AutoSaveSerializable? = null

    val botAdmin get() = instance.botAdmin
    val token get() = instance.token
    var outputChannelId
        get() = instance.outputChannelId
        set(value) {
            instance.outputChannelId = value
            instance.save()
        }
    val serverJarName get() = instance.serverJarName
    val streamUpdateFrequency get() = instance.streamUpdateFrequency

    @Serializable
    data class Data(
        val botAdmin: Long = 1234,
        val token: String = "ABCDE",
        var outputChannelId: Long = 5000,
        val serverJarName: String = "server.jar",
        val streamUpdateFrequency: Long = 500,
    ) : AutoSaveSerializable {
        @Transient
        override var parent: AutoSaveSerializable? = null

        override fun propagateParent() { /* noop */ }
    }
}
