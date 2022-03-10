# ObserveEncryptedPrefsBug
A minimum reproducible project to showcase a bug by which `SharedPreferences.OnSharedPreferenceChangeListener`
isn't triggered on key removals if the `SharedPreferences` are wrapped as `EncryptedSharedPreferences`.

## How to check

The project registers a `SharedPreferences.OnSharedPreferenceChangeListener` that will log when the preference has changed. There's a registered listener per each of the following objects:
* Plain `SharedPreferences`
* `EncryptedSharedPreferences`

It also provides the following UI to test their behavior:

<img src="https://user-images.githubusercontent.com/1465685/157738593-ff98efff-3bd3-4247-b8ef-7611ad68b7bb.png" alt="ui" width="300"/>


* **Plain**: All buttons act against a plain `SharedPreferences` file (`/data/data/<appid>/shared_prefs/plain.xml`)
* **Encrypted**: All buttons act against an `EncryptedSharedPreferences` file (`/data/data/<appid>/shared_prefs/encrypted.xml`)


* **Save**: Generates a random UUID and stores it in preferences with the key `"key"`
* **Remove**: Removes the stored preference using `Editor#remove()`
* **Clear**: Clears all keys using `Editor#clear()`
* **Log current**: Logs the current value in Logcat

As you can verify, the `Remove` button under "Encrypted" doesn't log anything, as the callback isn't triggered.
The `Remove` button under "Plain" logs just fine, as the callback is triggererd normally.
