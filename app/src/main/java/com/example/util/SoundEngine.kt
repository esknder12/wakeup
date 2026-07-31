package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.data.model.AlarmySound
import com.example.data.model.SoundSynthType
import kotlinx.coroutines.*
import java.util.Locale
import kotlin.math.sin
import kotlin.random.Random

class SoundEngine(private val context: Context) {

    private var audioTrack: AudioTrack? = null
    private var tts: TextToSpeech? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    var currentlyPlayingId: String? = null
        private set

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
            }
        }
    }

    fun playSound(sound: AlarmySound, volume: Float = 1.0f, onStop: () -> Unit = {}) {
        stopCurrentSound()
        currentlyPlayingId = sound.id

        playbackJob = scope.launch {
            try {
                when (sound.synthType) {
                    SoundSynthType.VOICE_TTS -> {
                        val prompt = sound.ttsPrompt ?: "Wake up! Time to get moving!"
                        while (isActive && currentlyPlayingId == sound.id) {
                            tts?.speak(prompt, TextToSpeech.QUEUE_FLUSH, null, "ALARM_TTS")
                            delay(4000)
                        }
                    }
                    SoundSynthType.SIREN_END_OF_WORLD -> playSirenLoop(800.0, 1600.0, 1.2f * volume)
                    SoundSynthType.DISASTER_ALERT -> playTwoToneAlert(650.0, 950.0, volume)
                    SoundSynthType.ROOSTER_CROW -> playRoosterChime(volume)
                    SoundSynthType.HEARTBEAT_WARNING -> playHeartbeatWarning(volume)
                    SoundSynthType.VINTAGE_BELL -> playVintageBell(volume)
                    SoundSynthType.EXCITED_WHISTLE -> playWhistle(volume)
                    SoundSynthType.BEEP_DIGITAL -> playBeepLoop(1000.0, volume)
                    SoundSynthType.TRENDING_BEAT -> playRhythmicBeat(volume)
                    SoundSynthType.MEME_CAT -> playMemeCatTone(volume)
                    SoundSynthType.SLEEP_RAIN -> playRainNoise(volume * 0.4f)
                    SoundSynthType.SLEEP_FIREPLACE -> playFireplaceNoise(volume * 0.5f)
                    SoundSynthType.SLEEP_WHITENOISE -> playWhiteNoise(volume * 0.35f)
                    SoundSynthType.SLEEP_ALPHA_WAVE -> playPureTone(432.0, volume * 0.45f)
                    SoundSynthType.SLEEP_AIRPLANE -> playPureTone(120.0, volume * 0.5f, noiseMix = 0.4f)
                    SoundSynthType.SLEEP_SINGING_BOWL -> playSingingBowl(volume * 0.5f)
                }
            } catch (e: Exception) {
                Log.e("SoundEngine", "Error playing sound ${sound.id}: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) {
                    onStop()
                }
            }
        }
    }

    fun stopCurrentSound() {
        currentlyPlayingId = null
        playbackJob?.cancel()
        playbackJob = null
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            // ignore
        }
        audioTrack = null
        try {
            if (tts?.isSpeaking == true) {
                tts?.stop()
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun createAudioTrack(sampleRate: Int = 44100): AudioTrack {
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        return AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize.coerceAtLeast(4096))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build().also {
                audioTrack = it
                it.play()
            }
    }

    private suspend fun playSirenLoop(minFreq: Double, maxFreq: Double, volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var phase = 0.0
        var freqPhase = 0.0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val freq = minFreq + (maxFreq - minFreq) * (0.5 * (1.0 + sin(freqPhase)))
                freqPhase += 2.0 * Math.PI * 1.5 / sampleRate
                val sample = sin(phase) * 32767 * volume.coerceIn(0f, 1f)
                phase += 2.0 * Math.PI * freq / sampleRate
                buffer[i] = sample.toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playTwoToneAlert(freq1: Double, freq2: Double, volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var phase = 0.0
        var time = 0.0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val freq = if ((time * 3.0).toInt() % 2 == 0) freq1 else freq2
                val sample = sin(phase) * 32767 * volume.coerceIn(0f, 1f)
                phase += 2.0 * Math.PI * freq / sampleRate
                time += 1.0 / sampleRate
                buffer[i] = sample.toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playBeepLoop(freq: Double, volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var phase = 0.0
        var time = 0.0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val cycle = (time * 4.0).toInt() % 2
                val sample = if (cycle == 0) sin(phase) * 32000 * volume else 0.0
                phase += 2.0 * Math.PI * freq / sampleRate
                time += 1.0 / sampleRate
                buffer[i] = sample.toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playRoosterChime(volume: Float) {
        playSirenLoop(500.0, 1400.0, volume)
    }

    private suspend fun playHeartbeatWarning(volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var phase = 0.0
        var time = 0.0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val pulse = sin(time * 8.0) * sin(time * 2.0)
                val freq = 120.0 + 80.0 * pulse
                val sample = sin(phase) * 28000 * volume * if (pulse > 0) 1.0 else 0.2
                phase += 2.0 * Math.PI * freq / sampleRate
                time += 1.0 / sampleRate
                buffer[i] = sample.toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playVintageBell(volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var phase1 = 0.0
        var phase2 = 0.0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val sample = (sin(phase1) * 0.6 + sin(phase2) * 0.4) * 32000 * volume
                phase1 += 2.0 * Math.PI * 1850.0 / sampleRate
                phase2 += 2.0 * Math.PI * 2200.0 / sampleRate
                buffer[i] = sample.toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playWhistle(volume: Float) {
        playTwoToneAlert(1800.0, 2400.0, volume)
    }

    private suspend fun playRhythmicBeat(volume: Float) {
        playTwoToneAlert(440.0, 880.0, volume)
    }

    private suspend fun playMemeCatTone(volume: Float) {
        playSirenLoop(400.0, 1100.0, volume)
    }

    private suspend fun playRainNoise(volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var lastSample = 0.0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val white = Random.nextDouble(-1.0, 1.0)
                val pink = (lastSample * 0.85) + (white * 0.15)
                lastSample = pink
                buffer[i] = (pink * 32767 * volume).toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playFireplaceNoise(volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var crackleTimer = 0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val base = Random.nextDouble(-0.3, 0.3)
                val crackle = if (Random.nextInt(1000) > 997) Random.nextDouble(0.7, 1.0) else 0.0
                buffer[i] = ((base + crackle) * 32767 * volume).toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playWhiteNoise(volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val white = Random.nextDouble(-1.0, 1.0)
                buffer[i] = (white * 32000 * volume).toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playPureTone(freq: Double, volume: Float, noiseMix: Float = 0f) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var phase = 0.0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val tone = sin(phase)
                val noise = Random.nextDouble(-1.0, 1.0)
                val mixed = tone * (1f - noiseMix) + noise * noiseMix
                phase += 2.0 * Math.PI * freq / sampleRate
                buffer[i] = (mixed * 32767 * volume).toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    private suspend fun playSingingBowl(volume: Float) {
        val sampleRate = 44100
        val track = createAudioTrack(sampleRate)
        val buffer = ShortArray(1024)
        var phase1 = 0.0
        var phase2 = 0.0

        while (currentCoroutineContext().isActive && currentlyPlayingId != null) {
            for (i in buffer.indices) {
                val sample = (sin(phase1) * 0.7 + sin(phase2) * 0.3) * 32000 * volume
                phase1 += 2.0 * Math.PI * 256.0 / sampleRate // root resonance
                phase2 += 2.0 * Math.PI * 512.0 / sampleRate // harmonic
                buffer[i] = sample.toInt().toShort()
            }
            track.write(buffer, 0, buffer.size)
            yield()
        }
    }

    fun destroy() {
        stopCurrentSound()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            // ignore
        }
    }
}
