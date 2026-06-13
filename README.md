# AdvancedSpark

AdvancedSpark is a Fabric add-on foundation for `doctor4t/wathe`.

## Build Notes

- Target Minecraft: `1.21.1`
- Target Java: `21`
- wathe is ARR and is not vendored here.
- Put the original wathe jar at `libs/wathe-1.3.2-1.21.1.jar` when compiling against wathe internals becomes necessary.
- The current foundation slice does not import wathe classes, so it can compile without the local wathe jar.
- Runtime metadata still declares a dependency on the `wathe` mod.

## Commands

```powershell
.\gradlew.bat build
```
