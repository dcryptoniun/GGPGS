# GGPGS - Godot Google Play Games Services Plugin
## under development
GGPGS is a plugin for integrating Google Play Games Services functionalities into Godot projects, enabling developers to easily implement features such as user authentication and data retrieval from Google Play Games Services.

## Features Progress
- [✅] Sign in
- [❌] Sign out
- [✅] Profile data(Display Name, Player ID)
- [✅] Avater image
- [❌] Save Game and Load Game
- [❌] Achievements
- [❌] Leaderboards
- [❌] Events


## Want to build the plugin yourself?
**Note:** [Android Studio](https://developer.android.com/studio) is the recommended IDE for
developing Godot Android plugins. 
You can install the latest version from https://developer.android.com/studio.
- After updating the plugin In Android Studio, open the project and ensure all dependencies are resolved.
- Run the following command in the terminal:
```.
./gradlew assemble
```
- On successful completion of the build, the output files can be found in [`plugin/demo/addons`](plugin/demo/addons)

### Testing the Android plugin
You can use the included [Godot demo project](plugin/demo/project.godot) to test the built Android 
plugin



## Demo

- Open the demo in Godot (4.4 not tested on lower versions but should work in 4.*)
- Navigate to `Project` -> `Project Settings...` -> `Plugins`, and ensure the plugin is enabled
- Install the Godot Android build template by clicking on `Project` -> `Install Android Build Template...`
- Open [`plugin/demo/main.gd`](plugin/demo/main.gd) and update the logic as needed to reference 
  your plugin and its methods
- Connect an Android device to your machine and run the demo on it


