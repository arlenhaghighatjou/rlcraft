package svenhjol.charm.tweaks.feature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import svenhjol.meson.Feature;

public class PickaxesBreakPistons extends Feature {
   public PickaxesBreakPistons() {
   }

   public String getDescription() {
      return "Pickaxes break pistons (and sticky pistons) faster.";
   }

   public void postInit(FMLPostInitializationEvent event) {
      BlockPistonBase basePiston = (BlockPistonBase)Block.REGISTRY.getObject(new ResourceLocation("minecraft", "piston"));
      BlockPistonBase stickyPiston = (BlockPistonBase)Block.REGISTRY.getObject(new ResourceLocation("minecraft", "sticky_piston"));
      basePiston.setHarvestLevel("pickaxe", 0);
      stickyPiston.setHarvestLevel("pickaxe", 0);
   }
}
