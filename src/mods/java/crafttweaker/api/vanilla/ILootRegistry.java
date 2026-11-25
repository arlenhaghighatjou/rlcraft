package crafttweaker.api.vanilla;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.WeightedItemStack;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("vanilla.ILootRegistry")
@ZenRegister
public interface ILootRegistry {
   @ZenMethod
   void addChestLoot(String var1, WeightedItemStack var2);

   @ZenMethod
   void addChestLoot(String var1, WeightedItemStack var2, int var3, int var4);

   @ZenMethod
   void removeChestLoot(String var1, IIngredient var2);

   @ZenMethod
   List<LootEntry> getLoot(String var1);

   @ZenGetter("lootTypes")
   List<String> getLootTypes();
}
