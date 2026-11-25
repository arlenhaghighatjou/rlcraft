package com.lycanitesmobs.core.info.altar;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.AltarInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AltarInfoRahovart extends AltarInfo {
   public AltarInfoRahovart(String name) {
      super(name);
   }

   public boolean quickCheck(Entity entity, World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock() == ObjectManager.getBlock("soulcubedemonic");
   }

   public boolean fullCheck(Entity entity, World world, BlockPos pos) {
      if (!this.quickCheck(entity, world, pos)) {
         return false;
      } else {
         Block bodyBlock = Blocks.OBSIDIAN;
         if (world.getBlockState(pos.add(0, -2, 0)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(pos.add(0, -1, 0)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(pos.add(0, 1, 0)).getBlock() != bodyBlock) {
            return false;
         } else if (world.getBlockState(pos.add(0, 2, 0)).getBlock() != bodyBlock) {
            return false;
         } else if (!this.checkPillar(bodyBlock, entity, world, pos.add(-2, 0, -2))) {
            return false;
         } else if (!this.checkPillar(bodyBlock, entity, world, pos.add(2, 0, -2))) {
            return false;
         } else if (!this.checkPillar(bodyBlock, entity, world, pos.add(-2, 0, 2))) {
            return false;
         } else {
            return this.checkPillar(bodyBlock, entity, world, pos.add(2, 0, 2));
         }
      }
   }

   public boolean checkPillar(Block bodyBlock, Entity entity, World world, BlockPos pos) {
      if (world.getBlockState(pos.add(0, -2, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos.add(0, -1, 0)).getBlock() != bodyBlock) {
         return false;
      } else if (world.getBlockState(pos).getBlock() != bodyBlock) {
         return false;
      } else {
         return world.getBlockState(pos.add(0, 1, 0)).getBlock() == Blocks.DIAMOND_BLOCK;
      }
   }

   public boolean activate(Entity entity, World world, BlockPos pos, int variant) {
      if (world.isRemote) {
         return true;
      } else {
         ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
         if (worldExt == null) {
            return false;
         } else {
            pos = new BlockPos(pos.getX(), Math.max(1, pos.getY() - 3), pos.getZ());
            if (entity != null) {
               pos = this.getFacingPosition(pos, 10.0D, (double)entity.rotationYaw);
            }

            return super.activate(entity, world, pos, variant);
         }
      }
   }
}
