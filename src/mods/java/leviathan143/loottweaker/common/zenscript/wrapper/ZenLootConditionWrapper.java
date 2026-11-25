package leviathan143.loottweaker.common.zenscript.wrapper;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("loottweaker.vanilla.loot.LootCondition")
public class ZenLootConditionWrapper {
   public static final ZenLootConditionWrapper INVALID = new ZenLootConditionWrapper((LootCondition)null);
   public final LootCondition condition;

   public ZenLootConditionWrapper(LootCondition condition) {
      this.condition = condition;
   }

   public LootCondition unwrap() {
      return this.condition;
   }

   public boolean isValid() {
      return this.condition != null;
   }
}
