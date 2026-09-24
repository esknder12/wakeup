package com.example.data.model

enum class SoundCategory(val title: String) {
    LOUD("Loud Ringtones"),
    TRENDING("Trending & Viral"),
    VOICE("Voice & Motivation"),
    SLEEP("Sleep Sounds & Noise"),
    CLASSIC("Classic Alarms")
}

data class AlarmySound(
    val id: String,
    val title: String,
    val category: SoundCategory,
    val description: String,
    val duration: String = "Loop",
    val isLoud: Boolean = false,
    val isPremium: Boolean = false,
    val synthType: SoundSynthType = SoundSynthType.SIREN_END_OF_WORLD,
    val ttsPrompt: String? = null
)

enum class SoundSynthType {
    SIREN_END_OF_WORLD,
    DISASTER_ALERT,
    ROOSTER_CROW,
    HEARTBEAT_WARNING,
    VINTAGE_BELL,
    EXCITED_WHISTLE,
    BEEP_DIGITAL,
    TRENDING_BEAT,
    MEME_CAT,
    VOICE_TTS,
    SLEEP_RAIN,
    SLEEP_FIREPLACE,
    SLEEP_WHITENOISE,
    SLEEP_ALPHA_WAVE,
    SLEEP_AIRPLANE,
    SLEEP_SINGING_BOWL
}

object AlarmySoundCatalog {
    val allSounds = listOf(
        // -- LOUD RINGTONES THAT HIT YOUR EARDRUMS --
        AlarmySound(
            id = "end_of_world",
            title = "End of the World",
            category = SoundCategory.LOUD,
            description = "Deafening emergency broadcast siren designed for the heaviest sleepers",
            isLoud = true,
            synthType = SoundSynthType.SIREN_END_OF_WORLD
        ),
        AlarmySound(
            id = "disaster_alert",
            title = "Disaster Alert",
            category = SoundCategory.LOUD,
            description = "High-decibel dual-tone evacuation alarm that cuts through deep sleep",
            isLoud = true,
            synthType = SoundSynthType.DISASTER_ALERT
        ),
        AlarmySound(
            id = "cock_a_doodle",
            title = "Cock a doodle doo",
            category = SoundCategory.LOUD,
            description = "Energetic farm rooster call with high-frequency wake-up chime",
            isLoud = true,
            synthType = SoundSynthType.ROOSTER_CROW
        ),
        AlarmySound(
            id = "heartbeat_warning",
            title = "Heartbeat Warning",
            category = SoundCategory.LOUD,
            description = "Intense accelerating cardiac pulse with emergency alarm overlay",
            isLoud = true,
            synthType = SoundSynthType.HEARTBEAT_WARNING
        ),
        AlarmySound(
            id = "broken_vintage_alarm",
            title = "Broken Vintage Alarm",
            category = SoundCategory.LOUD,
            description = "Aggressive twin-bell mechanical hammer clatter",
            isLoud = true,
            synthType = SoundSynthType.VINTAGE_BELL
        ),
        AlarmySound(
            id = "excited_whistle",
            title = "Excited Whistle",
            category = SoundCategory.LOUD,
            description = "Sharp, piercing rhythmic sports whistle",
            isLoud = true,
            synthType = SoundSynthType.EXCITED_WHISTLE
        ),
        AlarmySound(
            id = "loud_beep",
            title = "Classic Loud Beep",
            category = SoundCategory.LOUD,
            description = "Standard high-gain digital alarm beep pattern",
            isLoud = true,
            synthType = SoundSynthType.BEEP_DIGITAL
        ),

        // -- TRENDING & VIRAL --
        AlarmySound(
            id = "squid_game_round",
            title = "Squid Game - Round and Round",
            category = SoundCategory.TRENDING,
            description = "Iconic rhythmic synth beat inspired by viral challenges",
            synthType = SoundSynthType.TRENDING_BEAT
        ),
        AlarmySound(
            id = "kompa_groove",
            title = "Kompa Morning Groove",
            category = SoundCategory.TRENDING,
            description = "Upbeat Caribbean dance rhythm to get you moving out of bed",
            synthType = SoundSynthType.TRENDING_BEAT
        ),
        AlarmySound(
            id = "pedro_pedro",
            title = "Pedro Pedro Pedro",
            category = SoundCategory.TRENDING,
            description = "High-energy viral euro-dance melody",
            synthType = SoundSynthType.TRENDING_BEAT
        ),
        AlarmySound(
            id = "oiia_oiia_cat",
            title = "OIIA OIIA Cat",
            category = SoundCategory.TRENDING,
            description = "Spinning cat meme sound effect that makes waking up fun",
            synthType = SoundSynthType.MEME_CAT
        ),

        // -- VOICE & MOTIVATION (WITH TTS) --
        AlarmySound(
            id = "motivate_dont_give_up",
            title = "[Motivation] Don't give up",
            category = SoundCategory.VOICE,
            description = "Inspiring voice coach: 'Wake up! Your time is limited, don't waste it in bed!'",
            synthType = SoundSynthType.VOICE_TTS,
            ttsPrompt = "Wake up right now! Your time is limited, do not waste it sleeping! Get up and achieve your goals today!"
        ),
        AlarmySound(
            id = "motivate_grind",
            title = "[Motivation] Get up and Grind",
            category = SoundCategory.VOICE,
            description = "High-discipline voice command for students and exam aspirants",
            synthType = SoundSynthType.VOICE_TTS,
            ttsPrompt = "No snooze! Toppers are already studying! Get out of bed and conquer your morning routine!"
        ),
        AlarmySound(
            id = "voice_soldier_drill",
            title = "[Military] Morning Drill",
            category = SoundCategory.VOICE,
            description = "Loud sergeant command to jump out of bed immediately",
            isLoud = true,
            synthType = SoundSynthType.VOICE_TTS,
            ttsPrompt = "Attention! On your feet soldier! Five, four, three, two, one, out of bed right now!"
        ),
        AlarmySound(
            id = "voice_gentle_coach",
            title = "[Gentle] Morning Blessing",
            category = SoundCategory.VOICE,
            description = "Calm, positive morning affirmations for a peaceful start",
            synthType = SoundSynthType.VOICE_TTS,
            ttsPrompt = "Good morning. Take a deep breath. Today is full of opportunity. Let us begin with energy and peace."
        ),

        // -- DIVERSE SLEEP SOUNDS --
        AlarmySound(
            id = "sleep_light_rain",
            title = "Light Rain",
            category = SoundCategory.SLEEP,
            description = "Gentle raindrops falling on leaves and a windowpane",
            duration = "Continuous",
            synthType = SoundSynthType.SLEEP_RAIN
        ),
        AlarmySound(
            id = "sleep_fireplace",
            title = "Crackling Fireplace",
            category = SoundCategory.SLEEP,
            description = "Warm wood embers crackling in a cozy winter hearth",
            duration = "Continuous",
            synthType = SoundSynthType.SLEEP_FIREPLACE
        ),
        AlarmySound(
            id = "sleep_whitenoise",
            title = "Light Whitenoise",
            category = SoundCategory.SLEEP,
            description = "Consistent acoustic masking to block outside disturbances",
            duration = "Continuous",
            synthType = SoundSynthType.SLEEP_WHITENOISE
        ),
        AlarmySound(
            id = "sleep_alpha_wave",
            title = "Alpha Wave (432Hz)",
            category = SoundCategory.SLEEP,
            description = "Deep brainwave frequency for relaxation, study focus, and REM sleep",
            duration = "Continuous",
            synthType = SoundSynthType.SLEEP_ALPHA_WAVE
        ),
        AlarmySound(
            id = "sleep_airplane",
            title = "Airplane at Night",
            category = SoundCategory.SLEEP,
            description = "Low-frequency ambient cabin drone during a night flight",
            duration = "Continuous",
            synthType = SoundSynthType.SLEEP_AIRPLANE
        ),
        AlarmySound(
            id = "sleep_singing_bowl",
            title = "Tibetan Singing Bowl",
            category = SoundCategory.SLEEP,
            description = "Harmonic singing bowl resonance for meditation and deep sleep",
            duration = "Continuous",
            synthType = SoundSynthType.SLEEP_SINGING_BOWL
        )
    )

    fun getAlarmRingtones(): List<AlarmySound> = allSounds.filter { 
        it.category != SoundCategory.SLEEP 
    }

    fun getSleepSounds(): List<AlarmySound> = allSounds.filter { 
        it.category == SoundCategory.SLEEP 
    }

    fun findById(id: String): AlarmySound = allSounds.find { it.id == id } 
        ?: allSounds.first()
}
