### 🚀 Features & improvements

- Added achievements, async, ui, keyboard, scene, log, loop and audio modules to documentation
- Added reference documentation for ecs components, noteworthy utils and additional entity systems
- Ability to change reaction on completed achievements
- Added `TargetLockComponent` for rotating an entity towards a target
- Directly add degrees to `Rotation` using `Rotation.addDegrees(double)`
- Find shortest distance from one `Rotation` to another using `Rotation.delta(Rotation)`

### 🪛 Bug Fixes

- Fixed typo in `Scheduler` class
- Fixed typo in `Window.filesDroppedOnWindow`
- Fixed countless typos in JavaDoc

### 🧽 Cleanup & refactoring

- Combined the `SignalComponent` with the `AreaTriggerComponent`

### 📦 Dependency updates

- ...