package vazkii.quark.building.feature;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.building.item.ItemTrowel;

public class Trowel extends Feature {
   public static Item trowel;
   public static int damage;

   public Trowel() {
   }

   public void setupConfig() {
      damage = this.loadPropInt("Trowel Max Durability", "Amount of blocks placed is this value - 1. Default is 255 (4 stacks).\nSet to 0 to make the Trowel unbreakable", 255);
   }

   public void preInit(FMLPreInitializationEvent event) {
      trowel = new ItemTrowel(damage);
      RecipeHandler.addOreDictRecipe(new ItemStack(trowel), new Object[]{"S  ", " II", 'S', "stickWood", 'I', "ingotIron"});
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
