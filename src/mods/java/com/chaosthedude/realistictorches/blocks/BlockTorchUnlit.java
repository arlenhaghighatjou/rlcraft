package com.chaosthedude.realistictorches.blocks;

import com.chaosthedude.realistictorches.config.ConfigHandler;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTorchUnlit extends BlockRealisticTorch {
   public static final String NAME = "torch_unlit";

   public BlockTorchUnlit() {
      this.setUnlocalizedName("realistictorches.torch_unlit");
      this.setCreativeTab(CreativeTabs.DECORATIONS);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState state, World world, BlockPos pos, EaglercraftRandom rand) {
      if (ConfigHandler.unlitParticlesEnabled) {
         EnumFacing facing = (EnumFacing)state.getValue(FACING);
         double x = (double)pos.getX() + 0.5D;
         double y = (double)pos.getY() + 0.7D;
         double z = (double)pos.getZ() + 0.5D;
         double mod1 = 0.22D;
         double mod2 = 0.27D;
         if (facing.getAxis().isHorizontal()) {
            EnumFacing opposite = facing.getOpposite();
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + 0.27D * (double)opposite.getFrontOffsetX(), y + 0.22D, z + 0.27D * (double)opposite.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
         } else {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
         }

      }
   }
}
