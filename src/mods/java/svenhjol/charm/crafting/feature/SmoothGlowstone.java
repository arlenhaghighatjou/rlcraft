package svenhjol.charm.crafting.feature;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import svenhjol.charm.crafting.block.BlockSmoothGlowstone;
import svenhjol.meson.Feature;

public class SmoothGlowstone extends Feature {
   public static BlockSmoothGlowstone block;
   public static float hardness;

   public SmoothGlowstone() {
   }

   public String getDescription() {
      return "Smelt glowstone in a furnace to get smooth glowstone.";
   }

   public void configure() {
      super.configure();
      hardness = 0.3F;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      block = new BlockSmoothGlowstone();
   }

   public void init(FMLInitializationEvent event) {
      FurnaceRecipes instance = FurnaceRecipes.instance();
      float xp = 0.2F;
      ItemStack output = new ItemStack(block);
      instance.addSmeltingRecipeForBlock(Blocks.GLOWSTONE, output, xp);
   }
}
