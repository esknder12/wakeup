package com.example

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.alarm.AlarmRingActivity
import com.example.data.model.AlarmItem
import com.example.ui.components.AddEditAlarmDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingWizardScreen
import com.example.ui.screens.RingtoneCatalogScreen
import com.example.ui.screens.SleepSoundsScreen
import com.example.ui.screens.TopperRoutineScreen
import com.example.ui.theme.AlarmyBackground
import com.example.ui.theme.AlarmyRed
import com.example.ui.theme.AlarmySurface
import com.example.ui.theme.AlarmyTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AlarmViewModel
import com.example.util.AlarmLockState

private const val PREFS = "nequ_prefs"
private const val KEY_ONBOARDED = "onboarding_complete"

class MainActivity : ComponentActivity() {

  private val viewModel: AlarmViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    setContent {
      MyApplicationTheme {
        NEQUApp(
          viewModel = viewModel,
          startWithOnboarding = !prefs.getBoolean(KEY_ONBOARDED, false),
          onOnboardingComplete = { prefs.edit().putBoolean(KEY_ONBOARDED, true).apply() },
          onRingAlarm = { alarm -> AlarmRingActivity.start(this, alarm.id) }
        )
      }
    }
  }

  /**
   * While an alarm mission is on screen the volume / media keys are swallowed here, so the
   * alarm cannot be turned down or muted without completing the mission. When no alarm is
   * ringing this is a no-op and the keys behave normally.
   */
  override fun dispatchKeyEvent(event: KeyEvent): Boolean {
    if (AlarmLockState.handleKeyEvent(event)) return true
    return super.dispatchKeyEvent(event)
  }
}

private enum class NEQUTab(val label: String, val icon: ImageVector) {
  ALARMS("Alarms", Icons.Default.Alarm),
  SOUNDS("Sounds", Icons.Default.MusicNote),
  SLEEP("Sleep", Icons.Default.Bedtime),
  ROUTINE("Routine", Icons.Default.School)
}

@Composable
fun NEQUApp(
  viewModel: AlarmViewModel,
  startWithOnboarding: Boolean,
  onOnboardingComplete: () -> Unit,
  onRingAlarm: (AlarmItem) -> Unit
) {
  var showOnboarding by rememberSaveable { mutableStateOf(startWithOnboarding) }

  if (showOnboarding) {
    OnboardingWizardScreen(
      onFinishWizard = { alarm ->
        viewModel.saveAlarm(alarm)
        onOnboardingComplete()
        showOnboarding = false
      },
      onSkipToHome = {
        onOnboardingComplete()
        showOnboarding = false
      }
    )
    return
  }

  val alarms by viewModel.alarms.collectAsState()
  val previewSoundId by viewModel.previewSoundId.collectAsState()
  val activeSleepSound by viewModel.activeSleepSound.collectAsState()
  val isPlayingSleepSound by viewModel.isPlayingSleepSound.collectAsState()
  val challengeDay by viewModel.challengeDay.collectAsState()

  var tabIndex by rememberSaveable { mutableStateOf(0) }
  var showEditor by rememberSaveable { mutableStateOf(false) }
  var editingAlarm by remember { mutableStateOf<AlarmItem?>(null) }

  val tabs = NEQUTab.values()

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = AlarmyBackground,
    bottomBar = {
      NavigationBar(containerColor = AlarmySurface) {
        tabs.forEachIndexed { index, tab ->
          NavigationBarItem(
            selected = tabIndex == index,
            onClick = { tabIndex = index },
            icon = { Icon(tab.icon, contentDescription = tab.label) },
            label = { Text(tab.label) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              selectedTextColor = AlarmyRed,
              indicatorColor = AlarmyRed,
              unselectedIconColor = AlarmyTextSecondary,
              unselectedTextColor = AlarmyTextSecondary
            )
          )
        }
      }
    },
    floatingActionButton = {
      if (tabIndex == 0) {
        FloatingActionButton(
          onClick = {
            editingAlarm = null
            showEditor = true
          },
          containerColor = AlarmyRed
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add alarm", tint = Color.White)
        }
      }
    }
  ) { innerPadding ->
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
      when (tabIndex) {
        0 -> HomeScreen(
          alarms = alarms,
          nextAlarmText = viewModel.calculateNextAlarmText(alarms),
          onAddAlarmClick = {
            editingAlarm = null
            showEditor = true
          },
          onEditAlarmClick = { alarm ->
            editingAlarm = alarm
            showEditor = true
          },
          onToggleAlarm = { alarm -> viewModel.toggleAlarm(alarm) },
          onTestMissionClick = { alarm -> onRingAlarm(alarm) },
          onOpenTopperRoutine = { tabIndex = 3 }
        )

        1 -> RingtoneCatalogScreen(
          previewSoundId = previewSoundId,
          onPreviewClick = { sound -> viewModel.previewRingtone(sound) },
          onSelectSound = { sound -> viewModel.previewRingtone(sound) }
        )

        2 -> SleepSoundsScreen(
          activeSound = activeSleepSound,
          isPlaying = isPlayingSleepSound,
          onSoundClick = { sound -> viewModel.toggleSleepSound(sound) },
          onStopClick = {
            activeSleepSound?.let { playing -> viewModel.toggleSleepSound(playing) }
          }
        )

        else -> TopperRoutineScreen(
          challengeDay = challengeDay,
          onStartChallengeClick = { viewModel.addTopperRoutineAlarms() }
        )
      }
    }
  }

  if (showEditor) {
    AddEditAlarmDialog(
      alarm = editingAlarm,
      onDismiss = { showEditor = false },
      onSave = { alarm ->
        viewModel.saveAlarm(alarm)
        showEditor = false
      }
    )
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
