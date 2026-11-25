package com.lycanitesmobs.core.info.altar;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityGrue;
import com.lycanitesmobs.core.info.AltarInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AltarInfoLunarGrue extends AltarInfo {
   public AltarInfoLunarGrue(String name) {
      super(name);
   }

   public boolean quickCheck(Entity entity, World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock() == Blocks.DIAMOND_BLOCK;
   }

   public boolean fullCheck(Entity entity, World world, BlockPos pos) {
      if (!this.quickCheck(entity, world, pos)) {
         return false;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         Block bodyBlock = Blocks.OBSIDIAN;
         if (world.getBlockState(new BlockPos(x, y + 1, z)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(new BlockPos(x, y + 2, z)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(new BlockPos(x, y - 1, z)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(new BlockPos(x, y - 2, z)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(new BlockPos(x, y - 3, z)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(new BlockPos(x, y - 4, z)).getBlock() != bodyBlock) {
            return false;
         } else {
            return this.checkRotationX(bodyBlock, entity, world, x, y, z) ? true : this.checkRotationZ(bodyBlock, entity, world, x, y, z);
         }
      }
   }

   private boolean checkRotationX(Block bodyBlock, Entity entity, World world, int x, int y, int z) {
      if (world.getBlockState(new BlockPos(x - 1, y + 2, z)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(new BlockPos(x - 2, y + 2, z)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(new BlockPos(x - 2, y + 1, z)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(new BlockPos(x + 1, y + 2, z)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(new BlockPos(x + 2, y + 2, z)).getBlock() != bodyBlock) {
         return false;
      } else {
         return world.getBlockState(new BlockPos(x + 2, y + 1, z)).getBlock() == bodyBlock;
      }
   }

   private boolean checkRotationZ(Block bodyBlock, Entity entity, World world, int x, int y, int z) {
      if (world.getBlockState(new BlockPos(x, y + 2, z - 1)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(new BlockPos(x, y + 2, z - 2)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(new BlockPos(x, y + 1, z - 2)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(new BlockPos(x, y + 2, z + 1)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(new BlockPos(x, y + 2, z + 2)).getBlock() != bodyBlock) {
         return false;
      } else {
         return world.getBlockState(new BlockPos(x, y + 1, z + 2)).getBlock() == bodyBlock;
      }
   }

   public boolean activate(Entity entity, World world, BlockPos pos, int variant) {
      if (world.isRemote) {
         return true;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         BaseCreatureEntity entityGrue = new EntityGrue(world);
         if (checkDimensions && !entityGrue.isNativeDimension(world)) {
            return false;
         } else {
            int size = 4;

            for(int xTarget = x - size; xTarget <= x + size; ++xTarget) {
               for(int zTarget = z - size; zTarget <= z + size; ++zTarget) {
                  for(int yTarget = y - size; yTarget <= y + size; ++yTarget) {
                     BlockPos clearPos = new BlockPos(xTarget, yTarget, zTarget);
                     if (yTarget > 0 && world.getTileEntity(clearPos) == null) {
                        world.setBlockToAir(clearPos);
                     }
                  }
               }
            }

            entityGrue.altarSummoned = true;
            entityGrue.forceBossHealthBar = true;
            entityGrue.applyVariant(3);
            entityGrue.setLocationAndAngles((double)x, (double)(y - 2), (double)z, 0.0F, 0.0F);
            world.spawnEntity(entityGrue);
            return true;
         }
      }
   }
}
