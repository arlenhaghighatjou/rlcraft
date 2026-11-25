package com.lycanitesmobs.core.info.altar;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.creature.EntityChupacabra;
import com.lycanitesmobs.core.info.AltarInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AltarInfoPhosphorescentChupacabra extends AltarInfo {
   public AltarInfoPhosphorescentChupacabra(String name) {
      super(name);
   }

   public boolean quickCheck(Entity entity, World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock() == Blocks.DIAMOND_BLOCK;
   }

   public boolean fullCheck(Entity entity, World world, BlockPos pos) {
      if (!this.quickCheck(entity, world, pos)) {
         return false;
      } else {
         Block bodyBlock = Blocks.OBSIDIAN;
         if (world.getBlockState(pos.add(0, 1, 0)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(pos.add(0, -1, 0)).getBlock() != bodyBlock) {
            return false;
         } else {
            return this.checkRotationX(bodyBlock, entity, world, pos) ? true : this.checkRotationZ(bodyBlock, entity, world, pos);
         }
      }
   }

   private boolean checkRotationX(Block bodyBlock, Entity entity, World world, BlockPos pos) {
      if (world.getBlockState(pos.add(-1, 0, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(-2, 0, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(-1, -1, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(-1, -1, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(-2, -2, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(1, 0, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(2, 0, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(1, -1, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(1, -1, 0)).getBlock() != bodyBlock) {
         return false;
      } else {
         return world.getBlockState(pos.add(2, -2, 0)).getBlock() == bodyBlock;
      }
   }

   private boolean checkRotationZ(Block bodyBlock, Entity entity, World world, BlockPos pos) {
      if (world.getBlockState(pos.add(0, 0, -1)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, 0, -2)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, -1, -1)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, -2, -1)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, -2, -2)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, 0, 1)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, 0, 2)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, -1, 1)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, -2, 1)).getBlock() != bodyBlock) {
         return false;
      } else {
         return world.getBlockState(pos.add(0, -2, 2)).getBlock() == bodyBlock;
      }
   }

   public boolean activate(Entity entity, World world, BlockPos pos, int variant) {
      if (world.isRemote) {
         return true;
      } else {
         int x = pos.getX();
         int y = pos.getY();
         int z = pos.getZ();
         BaseCreatureEntity entityCreature = new EntityChupacabra(world);
         if (checkDimensions && !entityCreature.isNativeDimension(world)) {
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

            entityCreature.altarSummoned = true;
            entityCreature.forceBossHealthBar = true;
            entityCreature.applyVariant(3);
            entityCreature.setLocationAndAngles((double)x, (double)(y - 2), (double)z, 0.0F, 0.0F);
            world.spawnEntity(entityCreature);
            return true;
         }
      }
   }
}
