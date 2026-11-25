package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonTemperature;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

public class ModifierWet extends ModifierBase {
   public ModifierWet() {
      super("Wet");
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      Block block = state.getBlock();
      if (block instanceof IFluidBlock) {
         Fluid fluid = ((IFluidBlock)block).getFluid();
         if (fluid != null) {
            JsonTemperature tempInfo = (JsonTemperature)JsonConfig.fluidTemperatures.get(fluid.getName());
            if (tempInfo != null) {
               return tempInfo.temperature;
            }
         }
      }

      if (state.getMaterial() == Material.WATER) {
         return (float)ModConfig.server.temperature.wetValue;
      } else {
         return world.isRainingAt(pos) ? (float)ModConfig.server.temperature.wetValue : 0.0F;
      }
   }
}
