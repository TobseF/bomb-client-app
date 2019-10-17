# 💣📡📱 ExitGame Bomb - Android Client App

[![Kotlin](https://img.shields.io/badge/Kotlin-1.3.41-blue.svg?style=flat&logo=kotlin&logoColor=white)](http://kotlinlang.org)
[![Kotlin-Coroutines](https://img.shields.io/badge/Kotlin--Coroutines-1.3.0--RC2-orange.svg)](https://kotlinlang.org/docs/reference/coroutines-overview.html)
[![LibGDX](https://img.shields.io/badge/LibGDX-1.9.10-red.svg)](https://libgdx.badlogicgames.com/news.html)

This app is part of an [ExitGame](https://github.com/TobseF/exit-game-bomb-app) where payers have to deactivate a bomb by coding challenges.
The _client app_ can configure the bomb remotely.

The Bomb runs on an android device. It starts with a countdown before it explodes.
It's not accessible for the players. Players have to deactivate the bomb with [coding challenges](https://github.com/TobseF/exit-game-coding-challenge).
The deactivation commands are sent to an REST interface of the [bomb app](https://github.com/TobseF/exit-game-bomb-app).
### 📦 [Dowload](https://github.com/TobseF/bomb-client-app/releases/latest/download/bomb-client.apk)

## 🚀 Start
The App runs on Android and Desktop. So you can run, test and develop it without an Android device or emulator.
To start the app run the _Gradle_ task:

* **Run Desktop:** _desktop > Tasks > other > run_
* **Run Android:** _android > Tasks > other > run_
* **Build Android:** _android > Tasks > build > build_

You can also run the `DesktopLauncher` class direct and skip the gradle build - which can be faster. 
Then you have to set the _Working directory_ of the _Run Configuration_ to  `${your-path}\bomb-app\android\assets`.

After an android build, the apps apks are present in `bomb-app\android\build\outputs\apk\debug` and `...\release`. You can install them with:  
`adb install -r "${full-path}\bomb-app\android\build\outputs\apk\debug\android-debug.apk"`

Run Gradle tasks with at least **[Gradle 5.5](https://gradle.org/install/)** and ensure it runs with a 
**[Java 1.8 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)**.
You can check this here:  
_IntelliJ > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JVM_

## 💡 External Hardware
💡 The comb can be paired with external Hardware. It connects to a Phillips Hue bridge and controls lights.
So lights switch to ![RED](https://placehold.it/15/f03c15/000000?text=+) if the bomb gets activated and to 
![GREEN](https://placehold.it/15/c5f015/000000?text=+) if it gets disarmed.
See `HueService` or [yetanotherhueapi](https://github.com/TobseF/yetanotherhueapi) for details.

🔊 To bomb also plays audio files, so it may be a good idea to connect it with an external Bluetooth speaker.

⏰ The bomb can connect to an external Arduino hardware based 7-segment countdown timer.   
  This optional project is available here: [Arduino-Countdown-Timer](https://github.com/TobseF/Arduino-Countdown-Timer/tree/master).
  See `TimerService` for interface details.

## 🔧 Config
The settings screen allows you to change the bombs configuration.

|  Setting    |                         Description                                     |
|-------------|:------------------------------------------------------------------------|
| Bomb time * | The time before the bomb explodes in minutes.                           |
| Timer IP    | IP Address of the external timer.                                       |
| Hue IP      | IP Address of Phillips Hue bridge.                                      |
| Hue Room    | Name of the room with the Hue Lights. Change it in the Phillips Hue App.|
| Hue Key     | API Key from a paired HUE. Pair the Hue to retrieve it.                 |
| Debug       | If selected, you can switch the screens by <kbd>Space</kbd> or touch.   |
|  \* The _Bomb time_ can be also changed by the REST interface.                        | 


|  Button   |                         Description                                |
|-----------|:-------------------------------------------------------------------|
| Reset Hue | Clear the Hue API key, so you have to pair the  Hue again.         |
| Pair      | Pair the bomb with the Phillips Hue bridge. Creates a new API key. |
| Start     | Start the bomb in inactive mode.                                   |
  
## 🐞 Troubleshooting
```
Exception in thread "LWJGL Application" com.badlogic.gdx.utils.GdxRuntimeException: 
Couldn't load file: icons/icon_16.png
```
You forgot to to set the _Working directory_ of the _Run Configuration_ to  `${your-path}\bomb-app\android\assets`. 

---

```
Could not resolve all files for configuration ':core:compileClasspath'.
   > Could not find io.github.tobsef:yetanotherhueapi:1.3.0.
```

You need the [yetanotherhueapi](https://github.com/TobseF/yetanotherhueapi) maven dependency. For now it may be not present int the maven central.
So check it out and manual install it with:  
`git clone https://github.com/TobseF/yetanotherhueapi.git`  
`mvn install -DskipTests`  

---

```
android:mergeDexDebug FAILED
DexArchiveMergerException: Error while merging dex archives
Program type already present: com.libktx.game.AndroidLauncher
```
Run a _android > Tasks > build > clean_ with gradle, and try it again. 

---

```
Error running 'Run Desktop': Class 'com.libktx.game.desktop.DesktopLauncher' not found in module 'desktop'
```
Just launch it again.

---

```
Exception in thread "main" java.lang.UnsupportedClassVersionError: com/intellij/junit5/JUnit5IdeaTestRunner : 
Unsupported major.minor version 52.0
```
Ensure you run unit tests with an Java 1.8 JDK.
