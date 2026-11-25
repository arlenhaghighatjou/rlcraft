package leviathan143.loottweaker.common.zenscript;

import crafttweaker.annotations.ZenRegister;
import leviathan143.loottweaker.common.DeprecationWarningManager;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootTableWrapper;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

/** @deprecated */
@Deprecated
@ZenRegister
@ZenClass("loottweaker.vanilla.loot.LootTables")
public class DeprecatedZenLootTableTweakManager {
   public DeprecatedZenLootTableTweakManager() {
   }

   /** @deprecated */
   @Deprecated
   @ZenMethod
   public static ZenLootTableWrapper getTable(String tableName) {
      DeprecationWarningManager.addWarning();
      return ZenLootTableTweakManager.getTable(tableName);
   }

   /** @deprecated */
   @Deprecated
   @ZenMethod
   public static ZenLootTableWrapper getTableUnchecked(String tableName) {
      DeprecationWarningManager.addWarning();
      return ZenLootTableTweakManager.getTableUnchecked(tableName);
   }
}
