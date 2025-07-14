package com.example.moodjournal.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.moodjournal.VoicePrefs
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object VoicePrefsSerializer : Serializer<VoicePrefs> {
    override val defaultValue: VoicePrefs = VoicePrefs.newBuilder()
        .setPromptTtsEnabled(true)
        .setVoiceInputEnabled(true)
        .build()

    override suspend fun readFrom(input: InputStream): VoicePrefs {
        try {
            return VoicePrefs.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: VoicePrefs, output: OutputStream) = t.writeTo(output)
}