package crafttweaker.api.vanilla;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.WeightedItemStack;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("vanilla.ISeedRegistry")
@ZenRegister
public interface ISeedRegistry {
   @ZenMethod
   void addSeed(WeightedItemStack var1);

   @ZenMethod
   void removeSeed(IIngredient var1);

   @ZenGetter("seeds")
   List<WeightedItemStack> getSeeds();
}
