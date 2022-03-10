package uk.co.origamibits.observeencryptedprefsbug

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import uk.co.origamibits.observeencryptedprefsbug.ui.theme.ObserveEncryptedPrefsBugTheme
import java.util.UUID

class MainActivity : ComponentActivity() {

    lateinit var encryptedSharedPreferences: SharedPreferences
    lateinit var plainSharedPreferences: SharedPreferences

    private val encryptedListener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        Log.d(LOG_TAG, "Encrypted key changed: $k")
    }

    private val plainListener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        Log.d(LOG_TAG, "Plain key changed: $k")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            this,
            "encrypted",
            MasterKey.Builder(this)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        plainSharedPreferences = getSharedPreferences("plain", MODE_PRIVATE)

        encryptedSharedPreferences.registerOnSharedPreferenceChangeListener(encryptedListener)
        plainSharedPreferences.registerOnSharedPreferenceChangeListener(plainListener)

        setContent {
            ObserveEncryptedPrefsBugTheme {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text("Plain")
                    PreferenceManagementComponent(
                        modifier = Modifier.fillMaxWidth(),
                        saveValue = { plainSharedPreferences.saveRandomUUID() },
                        removeValue = { plainSharedPreferences.removeRandomUUID() },
                        logCurrentValue = { plainSharedPreferences.logCurrentValue(prefix = "Plain") }
                    )
                    Text("Encrypted")
                    PreferenceManagementComponent(
                        modifier = Modifier.fillMaxWidth(),
                        saveValue = { encryptedSharedPreferences.saveRandomUUID() },
                        removeValue = { encryptedSharedPreferences.removeRandomUUID() },
                        logCurrentValue = { encryptedSharedPreferences.logCurrentValue(prefix = "Encrypted") },
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        encryptedSharedPreferences.unregisterOnSharedPreferenceChangeListener(encryptedListener)
        plainSharedPreferences.unregisterOnSharedPreferenceChangeListener(plainListener)
        super.onDestroy()
    }
}

private fun SharedPreferences.saveRandomUUID() {
    edit().putString(KEY, UUID.randomUUID().toString()).apply()
}

private fun SharedPreferences.removeRandomUUID() {
    edit().remove(KEY).apply()
}

private fun SharedPreferences.logCurrentValue(prefix: String) {
    Log.d(LOG_TAG, "$prefix current value: ${getString(KEY, null)}")
}

@Composable
fun PreferenceManagementComponent(
    modifier: Modifier = Modifier,
    saveValue: (String) -> Unit,
    removeValue: () -> Unit,
    logCurrentValue: () -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(onClick = { saveValue(UUID.randomUUID().toString()) }) {
                Text("Save")
            }
            Button(onClick = removeValue) {
                Text("Remove")
            }
            Button(onClick = logCurrentValue) {
                Text("Log current")
            }
        }
    }
}

private val KEY = "key"
private val LOG_TAG = "PREFS_BUG"