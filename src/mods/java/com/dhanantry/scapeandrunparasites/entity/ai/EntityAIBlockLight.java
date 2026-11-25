package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEndGateway;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EntityAIBlockLight extends EntityAIBase {
   private final EntityParasiteBase parent;
   private int ticks = 0;
   private int range;
   private int lightTrigger;
   private BlockPos target;
   private World world;
   private int progressB;
   private int idle;
   private int neededTime;
   private Block block;
   private int disss;
   private int prevProgressB = -1;
   private ArrayList<Long> cant;

   public EntityAIBlockLight(EntityParasiteBase in, int range, int lightLook) {
      this.parent = in;
      this.world = this.parent.world;
      this.range = range;
      this.lightTrigger = lightLook;
      this.cant = new ArrayList();
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      ++this.ticks;
      if (this.ticks < 40) {
         return false;
      } else {
         this.ticks = 0;
         if (this.parent.world.getGameRules().getBoolean("mobGriefing") && this.parent.getAttackTarget() == null) {
            BlockPos ll = this.findSource();
            if (ll != null) {
               this.target = ll;
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      return this.target != null && this.world.getBlockState(this.target).getBlock() == this.block && this.parent.getAttackTarget() == null;
   }

   public void startExecuting() {
      this.progressB = 0;
      this.idle = 0;
      this.block = this.world.getBlockState(this.target).getBlock();
      float multiplier = 10.0F;
      IBlockState state = this.world.getBlockState(this.target);
      this.neededTime = (int)(state.getBlockHardness(this.world, this.target) * Math.max(0.0F, multiplier));
   }

   public void resetTask() {
      this.parent.getNavigator().clearPath();
      this.prevProgressB = -1;
      this.target = null;
   }

   public void updateTask() {
      double r = 5.0D;
      double distance = this.realdistanceSq(this.target, this.parent.posX, this.parent.posY, this.parent.posZ);
      if (distance > r) {
         this.parent.getNavigator().tryMoveToXYZ((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ(), 1.1D);
      }

      if (this.parent.isRiding()) {
         this.resetTask();
      } else {
         ++this.idle;
         if ((int)Math.round(distance) == this.disss) {
            ++this.idle;
         } else {
            this.idle = 0;
         }

         if (this.idle == 120) {
            this.parent.skillBreakBlocks();
         }

         if (this.idle >= 240) {
            boolean canAdd = true;

            for(int p = 0; p < this.cant.size(); ++p) {
               if (((Long)this.cant.get(p)).longValue() == this.target.toLong()) {
                  canAdd = false;
               }
            }

            if (canAdd) {
               this.cant.add(Long.valueOf(this.target.toLong()));
            }

            this.resetTask();
            this.ticks += 30;
         } else {
            this.disss = (int)Math.round(distance);
            if (distance <= r && this.target != null) {
               this.parent.getNavigator().tryMoveToXYZ((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ(), 0.0D);
               ++this.progressB;
               this.idle = 0;
               int i = (int)((float)this.progressB / (float)this.neededTime);
               if (i != this.prevProgressB) {
                  this.world.sendBlockBreakProgress(this.parent.getEntityId(), this.target, i);
                  this.prevProgressB = i;
               }

               if (this.progressB >= this.neededTime) {
                  this.world.destroyBlock(this.target, true);
                  this.progressB = 0;
                  this.ticks += 30;
               }
            }

         }
      }
   }

   private BlockPos findSource() {
      if (this.world.getLightFor(EnumSkyBlock.BLOCK, this.parent.getPosition()) < this.lightTrigger && this.world.rand.nextInt(3) != 0) {
         return null;
      } else {
         double distanceM = 40000.0D;
         BlockPos light = null;
         if (this.parent.isRiding()) {
            return light;
         } else {
            List<BlockPos> sources = new ArrayList();

            for(int i = -this.range; i <= this.range; ++i) {
               for(int j = -4; (float)j <= this.parent.height; ++j) {
                  for(int k = -this.range; k <= this.range; ++k) {
                     BlockPos atm = new BlockPos(this.parent.posX + (double)i, this.parent.posY + (double)j, this.parent.posZ + (double)k);
                     IBlockState state = this.world.getBlockState(atm);
                     Block block = state.getBlock();
                     if (!(block instanceof BlockLiquid) && !(block instanceof BlockPortal) && !(block instanceof BlockEndGateway) && !(block instanceof BlockEndPortalFrame) && state.getMaterial() != Material.PORTAL && (state.getLightValue() >= this.lightTrigger || state.getMaterial() == Material.CIRCUITS)) {
                        if (this.cant.size() == 0) {
                           sources.add(atm);
                        } else {
                           boolean canAdd = true;

                           for(int p = 0; p < this.cant.size(); ++p) {
                              if (((Long)this.cant.get(p)).longValue() == atm.toLong()) {
                                 canAdd = false;
                                 break;
                              }
                           }

                           if (canAdd) {
                              sources.add(atm);
                           }
                        }
                     }
                  }
               }
            }

            for(BlockPos pos : sources) {
               double distance = pos.distanceSq(this.parent.posX, this.parent.posY, this.parent.posZ);
               if (distance < distanceM) {
                  light = pos;
                  distanceM = distance;
               }
            }

            return light;
         }
      }
   }

   private double realdistanceSq(BlockPos pos, double toX, double toY, double toZ) {
      double d0 = (double)pos.getX() + 0.5D - toX;
      double d1 = (double)pos.getY() + 0.5D - toY;
      double d2 = (double)pos.getZ() + 0.5D - toZ;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }
}
