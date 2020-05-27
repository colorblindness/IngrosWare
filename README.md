<h1 align="center">IngrosWare</h1>
<div align="center">
  <strong> 1.12 Minecraft injection cheat.</strong>
</div>
<br />

# How to setup
Clone the workspace
```
https://github.com/oHare2/IngrosWare.git
```
Go into folder and open up a command prompt and do

**Eclipse** -
gradlew setupDevWorkspace eclipse build

**Intelij** -
gradlew setupDevWorkspace idea genIntellijRuns build

# Open in IDE
**Eclipse**
```Right click -> New -> Java Project -> Browse location -> Select IngrosWare folder -> Finish```

**Intelij**
```Open -> Select IngrosWare folder -> Import gradle project```

# Run

Add ```-Dfml.coreMods.load=best.reich.ingros.mixin.launch.IngrosLoader``` to VM options.
