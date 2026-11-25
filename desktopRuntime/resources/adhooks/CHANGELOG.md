## TODO:
* test RenderHelper.disableStandardItemLighting();
* add no harm to legs potion effect after boosting
* add stun effect for pudge hook
* fix hook jitter when switching from shooting to reeling
* allow pudge hook to be attached to poles, iron bars
* allow pudge hook to cut plants and tear out wood fences
* fix moving in water and on boats
* dismount shooter when rope tension > shooter's weight

## 3.3.1.0
+ added: Russian translation (thanks to Venikk99)
+ added: "maxEntityWeight" config setting for each hook (thanks to Shivaxi)
* __requires: ForgeEndertech 4.5.6.0 or above__

## 3.3.0.0
+ implemented: ability to swing (thanks to Qsandas, ZoraergazU and waldotheranger)
+ implemented: synchronization of custom parameters with clients
+ added: Chinese translation (thanks to beardpico)
* reworked: network code and movement synchronization
* improved: BoostUp ability (now it is more accurate and causes less fall damage)
* reduced: network bandwidth usage by removing excessive motion updates
* fixed: rubber band effect (spontaneous bouncing up and down) (thanks to MischiefOfMice)
* fixed: very slow movement when dragging another entity, even a small one
* __requires: ForgeEndertech 4.5.3.0 or above__

## 3.2.3.0
* updated to the latest ForgeEndertech lib
* __requires: ForgeEndertech 4.5.0.0 or above__

## 3.2.2.0
+ implemented: localizable tooltips (thanks to mc_china)
* fixed: some errors in tooltips
* __requires: ForgeEndertech 4.4.2.0 or above__

## 3.2.1.0
+ implemented: different behavior when interacting with blocks depending on their material
	(breaking glass, passing through leaves, inability to spear hard material, etc.)
+ added: "canHookAnyBlock" config option to enable old hook behavior
* fixed: hook and rope rendering in black, where possible
* __requires: ForgeEndertech 4.3.19.0 or above__

## 3.2.0.0
+ added: new voxel models for all hooks and launchers
- removed: xp penalty when renewing string for the same launcher more than once 
* improved: recipes for all launchers (please delete old configs)
* __requires: ForgeEndertech 4.3.18.0 or above__

## 3.1.3.0
+ added: config options to disable the ability to pull entities/players (thanks to purendar2)
* __requires: ForgeEndertech 4.3.8.0 or above__

## 3.1.2.1
* moved: web recipe from config to json preventing log warnings

## 3.1.2.0
! ATTENTION: delete old configs (.minecraft/config/adhooks folder) for changes to take effect
* increased: strength of all hooks
* moved: unhook keybinding from configs to Controls/Gameplay menu
* fixed: key processing being not synchronized with a main thread which may cause hook not being launched
* fixed: pudge hook loosening
* fixed: rope length exceeding the allowable bounds when pulling
* improved: physics and dynamics
* fixed: not working fast reeling when holding pull button

## 3.1.1.0
! ATTENTION: delete old configs (.minecraft/config/adhooks folder) for changes to take effect 
* increased: durability for all hooks
* increased: rope length for all hooks
* increased: rope elasticity for spear hook launcher
* reduced: reeling speed for spear hook launcher

## 3.1.0.0
* fixed: invalid event annotation error (thanks to Yayes)
* updated to recommended forge-1.12.2-14.23.0.2491

## 3.0.0.0
* ported to Minecraft 1.12

## 2.0.5.0
+ added config parameter for defining a custom key for the Unhook action (thanks to thefirstgamemaster)

## 2.0.4.0
+ added: pudge hook now dismounts riding entities
* improved: weight calculations for better physics
* improved: boost up strength (delete old configs)
* fixed: boost up not working everytime 
* fixed: pudge hook getting stuck on immovable entities
* fixed: rope jerking motion when hooking an entity
* fixed: id remap not working when updating from old versions

## 2.0.3.0
+ added config parameter for hook fire resistance
* hooks are flammable now

## 2.0.2.0
* deep code reorganization
* updated to the latest recommended forge-1.11.2-13.20.0.2228

## 2.0.1.1
* fixed accidental startup crash with NullPointerException

## 2.0.1.0
* fixed recipes may not being initialized
* some code improvements
* updated to the latest forge-1.11.2-13.20.0.2223

## 2.0.0.0
* ported to Minecraft 1.11.2 (forge-1.11.2-13.20.0.2203)
* changed format for all ids to comply with MC 1.11 standart

## 1.0.2.4
* fixed asynchronous hook creation which might cause server crashes with:
* ConcurrentModificationException (thanks to RunicGlympse)

## 1.0.2.3
* fixed: rendering glitch with multiple hooks launched

## 1.0.2.2
* fixed occasional server crashes with:
* java.util.ConcurrentModificationException (thanks to Hastling)

## 1.0.2.1
* fixed dedicated server crash with:
* java.lang.NoSuchMethodError: toModelResLoc_Inventory() (thanks to Vazkii)

## 1.0.2.0
* added new ability: "Boost up" (see launcher control tooltip)
* increased Spear Hook Launcher default reeling speed to 0.6
* requires ForgeEndertech 1.10.2-1.1.3.0

## 1.0.1.0
* added more informative tooltips
* fixed incorrect durability settings
* requires ForgeEndertech 1.10.2-1.1.2.0

## 1.0.0.2
* fixed dedicated server crash with:
* java.lang.NoClassDefFoundError: net/minecraft/client/renderer/entity/Render

## 1.0.0.1
* rebuild to accept older Minecraft versions [1.9.4, 1.10]
* requires ForgeEndertech 1.10.2-1.1.1.1

## 1.0.0.0
* ported to Minecraft 1.10.2
* fixed rope jerks when hooking some entity
* fixed hook rotation glitches when reeling begins
* fixed rope sagging glitch when hooking blocks
* requires ForgeEndertech 1.10.2-1.1.1.0

