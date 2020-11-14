package de.wulkanat.files

import de.wulkanat.files.concept.AutoSaveSerializable
import de.wulkanat.files.concept.SerializableObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

object Config : SerializableObject<Config.Data>("config.json", Data(), Data.serializer()) {
    override var parent: AutoSaveSerializable? = null

    val botAdmin get() = instance.botAdmin
    val token get() = instance.token
    val muteDelay get() = instance.muteDelay

    @Serializable
    data class Data(
        val botAdmin: Long = 1234,
        val token: String = "ABCDE",
        val muteDelay: Long = 5000,
    ) : AutoSaveSerializable {
        @Transient
        override var parent: AutoSaveSerializable? = null

        override fun propagateParent() { /* noop */
        }
    }
}