# AGENTS.md

## Cursor Cloud specific instructions

DRACS-App is a **native Android app** (Java, package `com.khalil.DRACS`) built with the
Gradle wrapper + Android Gradle Plugin 8.2.2. It pulls its content from a Firebase
Firestore `pages` collection (documents `rna`, `ps`, `fda`, `je`, `fp`). See `README.md`
for the product overview and Firestore schema.

### Toolchain (pre-installed in the VM snapshot; refreshed by the update script)
- **JDK 17** at `/usr/lib/jvm/java-17-openjdk-amd64` (AGP 8.2.2 requires JDK 17; the VM's
  default `java` is 21). Gradle is pinned to JDK 17 via `org.gradle.java.home` in
  `~/.gradle/gradle.properties`, so builds work regardless of the shell's `java`.
- **Android SDK** at `~/android-sdk` (`platforms;android-35`, `build-tools;35.0.0`,
  `platform-tools`). `local.properties` (gitignored) sets `sdk.dir` to this path.
- `~/.bashrc` exports `JAVA_HOME`, `ANDROID_HOME`, and adds `sdkmanager`/`adb` to `PATH`
  for interactive use.

### Firebase config (required to build)
The `com.google.gms.google-services` plugin needs `app/google-services.json`, which is
**gitignored**. It is regenerated on startup by the update script from the committed
`google-services-base64.txt` (note: that file is UTF‑16 LE, so decode with
`iconv -f UTF-16 -t UTF-8 ... | tr -cd 'A-Za-z0-9+/=' | base64 -d`). Without it, the
build fails at the google-services task.

### Common commands (run from repo root)
- Build debug APK: `./gradlew :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`
- Lint: `./gradlew :app:lintDebug` → HTML report at `app/build/reports/lint-results-debug.html`

### Gotchas
- **No emulator here.** The VM has no `/dev/kvm`, so the Android emulator cannot run. Verify
  changes via `assembleDebug` + `lintDebug`, and (for content/Firebase changes) by querying
  the live Firestore REST API with the key in `app/google-services.json`, e.g.
  `curl "https://firestore.googleapis.com/v1/projects/dracs-bb810/databases/(default)/documents/pages?key=<api_key>"`.
- **Unit tests don't compile.** `app/src/test/.../ExampleUnitTest.java` uses JUnit, but
  `app/build.gradle.kts` declares no `testImplementation("junit:junit:...")`, so
  `:app:testDebugUnitTest` (and therefore `./gradlew build`) fails to compile. This is a
  pre-existing repo defect, not an environment problem; add the JUnit test dependency if you
  need unit tests to run.
- The Kotlin-metadata `e:` messages during `lintDebug` (Firebase libs compiled with Kotlin
  2.1.0 vs expected 1.9.0) are non-fatal warnings; lint still succeeds.
