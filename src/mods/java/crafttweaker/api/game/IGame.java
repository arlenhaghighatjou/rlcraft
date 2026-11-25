package crafttweaker.api.game;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockDefinition;
import crafttweaker.api.enchantments.IEnchantmentDefinition;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.item.IItemDefinition;
import crafttweaker.api.liquid.ILiquidDefinition;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.potions.IPotion;
import crafttweaker.api.world.IBiome;
import java.util.List;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("crafttweaker.game.IGame")
@ZenRegister
public interface IGame {
   @ZenGetter("items")
   List<IItemDefinition> getItems();

   @ZenGetter("blocks")
   List<IBlockDefinition> getBlocks();

   @ZenGetter("liquids")
   List<ILiquidDefinition> getLiquids();

   @ZenMethod
   ILiquidStack getLiquid(String var1);

   @ZenGetter("biomes")
   List<IBiome> getBiomes();

   @ZenGetter("potions")
   List<IPotion> getPotions();

   @ZenGetter("entities")
   List<IEntityDefinition> getEntities();

   @ZenMethod
   IEntityDefinition getEntity(String var1);

   @ZenGetter("enchantments")
   List<IEnchantmentDefinition> getEnchantments();

   @ZenMethod
   void setLocalization(String var1, String var2);

   @ZenMethod
   void setLocalization(String var1, String var2, String var3);

   @ZenMethod
   String localize(String var1);

   @ZenMethod
   String localize(String var1, String var2);
}
