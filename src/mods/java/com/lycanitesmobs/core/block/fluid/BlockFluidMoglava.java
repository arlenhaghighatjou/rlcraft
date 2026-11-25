package com.lycanitesmobs.core.block.fluid;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.block.BlockFluidBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidMoglava extends BlockFluidBase {
   public BlockFluidMoglava(Fluid fluid) {
      super(fluid, Material.LAVA, LycanitesMobs.modInfo, "moglava");
      this.setLightOpacity(1);
      this.setLightLevel(1.0F);
      this.destroyItems = false;
   }

   public String getLocalizedName() {
      return LanguageManager.translate(this.getUnlocalizedName() + ".name");
   }

   public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.getDescription(stack, world));
   }

   public String getDescription(ItemStack itemStack, World world) {
      return LanguageManager.translate(this.getUnlocalizedName() + ".description");
   }

   public boolean canDisplace(IBlockAccess world, BlockPos pos) {
      IBlockState blockState = world.getBlockState(pos);
      if (blockState.getBlock() != this) {
         if (blockState == Blocks.WATER) {
            if (world instanceof World) {
               ((World)world).setBlockState(pos, Blocks.STONE.getDefaultState());
            }

            return false;
         } else {
            return blockState.getMaterial().isLiquid() ? false : super.canDisplace(world, pos);
         }
      } else {
         if (blockState.getBlock().getMetaFromState(blockState) != 0) {
            byte otherSourceBlocks = 0;
            ArrayList<BlockPos> adjBlockPositions = new ArrayList();
            adjBlockPositions.add(pos.add(-1, 0, 0));
            adjBlockPositions.add(pos.add(1, 0, 0));
            adjBlockPositions.add(pos.add(0, 1, 0));
            adjBlockPositions.add(pos.add(0, 0, -1));
            adjBlockPositions.add(pos.add(0, 0, 1));

            for(BlockPos adjBlockPos : adjBlockPositions) {
               IBlockState adjBlockState = world.getBlockState(adjBlockPos);
               Block adjBlock = adjBlockState.getBlock();
               int adjMetadata = adjBlock.getMetaFromState(adjBlockState);
               if (adjBlock == this && adjMetadata == 0) {
                  ++otherSourceBlocks;
               }

               if (otherSourceBlocks > 1) {
                  break;
               }
            }

            if (otherSourceBlocks > 1 && world instanceof World) {
               ((World)world).setBlockState(pos, this.getDefaultState());
            }
         }

         return false;
      }
   }

   public boolean displaceIfPossible(World world, BlockPos pos) {
      return world.getBlockState(pos).getMaterial().isLiquid() ? this.canDisplace(world, pos) : super.displaceIfPossible(world, pos);
   }

   public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
      if (entity instanceof EntityItem) {
         entity.attackEntityFrom(DamageSource.LAVA, 10.0F);
      }

      super.onEntityCollidedWithBlock(world, pos, state, entity);
   }

   @SideOnly(Side.CLIENT)
   public void randomDisplayTick(IBlockState blockState, World world, BlockPos pos, Random random) {
      if (random.nextInt(100) == 0) {
         float f = (float)pos.getX() + random.nextFloat();
         float f1 = (float)pos.getY() + random.nextFloat() * 0.5F;
         float f2 = (float)pos.getZ() + random.nextFloat();
         world.spawnParticle(EnumParticleTypes.LAVA, (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D, new int[0]);
      }

      super.randomDisplayTick(blockState, world, pos, random);
   }
}
