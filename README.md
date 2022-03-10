# ObserveEncryptedPrefsBug
A minimum reproducible project to showcase a bug by which `SharedPreferences.OnSharedPreferenceChangeListener`
isn't triggered if the `SharedPreferences` are wrapped as `EncryptedSharedPreferences`.

## How to check

The project registers a `SharedPreferences.OnSharedPreferenceChangeListener` per each of the following objects:
* Plain `SharedPreferences`
* `EncryptedSharedPreferences`

It also provides the following UI to test their behavior:

<img src="https://user-images.githubusercontent.com/1465685/157737031-6098947d-a532-48aa-9f3f-587cbea355c8.png" alt="ui" width="300"/>

* Plain: All buttons act vs a plain `SharedPreferences` file (`/data/data/<appid>/shared_prefs/plain.xml`)
* Encrypted: All buttons act vs an `EncryptedSharedPreferences` tile (`/data/data/<appid>/shared_prefs/encrypted.xml`)

* Save: Generates a random UUID and stores it in preferences with the key `"key"`
* Remove: Removes the stored preference using `Editor#remove()`
* Logs the current value in Logcat

Both SharedPreferences approaches have a registered Listener, that will log when the preference has changed.

As you can verify, the `Remove` button under Encrypted doesn't log anything, as the callback isn't triggered.
The `Remove` button under "Plain" logs just fine, as the callback is triggererd normally.

