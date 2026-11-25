package com.ferreusveritas.dynamictrees.entities.animation;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AnimationHandlerPhysics implements IAnimationHandler {
   public AnimationHandlerPhysics() {
   }

   public String getName() {
      return "physics";
   }

   AnimationHandlerPhysics.HandlerData getData(EntityFallingTree entity) {
      return entity.animationHandlerData instanceof AnimationHandlerPhysics.HandlerData ? (AnimationHandlerPhysics.HandlerData)entity.animationHandlerData : new AnimationHandlerPhysics.HandlerData();
   }

   public void initMotion(EntityFallingTree entity) {
      entity.animationHandlerData = new AnimationHandlerPhysics.HandlerData();
      BlockPos cutPos = entity.getDestroyData().cutPos;
      long seed = entity.world.rand.nextLong();
      EaglercraftRandom random = new EaglercraftRandom(seed ^ ((long)cutPos.getX() << 32 | (long)cutPos.getZ()));
      float mass = entity.getDestroyData().woodVolume;
      float inertialMass = MathHelper.clamp(mass, 1.0F, 3.0F);
      entity.motionX /= (double)inertialMass;
      entity.motionY /= (double)inertialMass;
      entity.motionZ /= (double)inertialMass;
      this.getData(entity).rotPit = (random.nextFloat() - 0.5F) * 4.0F / inertialMass;
      this.getData(entity).rotYaw = (random.nextFloat() - 0.5F) * 4.0F / inertialMass;
      EnumFacing cutDir = entity.getDestroyData().cutDir;
      entity.motionX += (double)cutDir.getOpposite().getFrontOffsetX() * 0.1D;
      entity.motionY += (double)cutDir.getOpposite().getFrontOffsetY() * 0.1D;
      entity.motionZ += (double)cutDir.getOpposite().getFrontOffsetZ() * 0.1D;
      EntityFallingTree.standardDropLeavesPayLoad(entity);
   }

   public void handleMotion(EntityFallingTree entity) {
      if (!entity.landed) {
         entity.motionY -= 0.029999999329447746D;
         entity.motionX *= 0.9800000190734863D;
         entity.motionY *= 0.9800000190734863D;
         entity.motionZ *= 0.9800000190734863D;
         AnimationHandlerPhysics.HandlerData var10000 = this.getData(entity);
         var10000.rotYaw *= 0.98F;
         var10000 = this.getData(entity);
         var10000.rotPit *= 0.98F;
         entity.posX += entity.motionX;
         entity.posY += entity.motionY;
         entity.posZ += entity.motionZ;
         entity.rotationPitch = MathHelper.wrapDegrees(entity.rotationPitch + this.getData(entity).rotPit);
         entity.rotationYaw = MathHelper.wrapDegrees(entity.rotationYaw + this.getData(entity).rotYaw);
         int radius = 8;
         if (entity.getDestroyData().getNumBranches() > 0) {
            IBlockState state = entity.getDestroyData().getBranchBlockState(0);
            if (TreeHelper.isBranch(state)) {
               radius = ((BlockBranch)state.getBlock()).getRadius(state);
            }

            World world = entity.world;
            AxisAlignedBB fallBox = new AxisAlignedBB(entity.posX - (double)radius, entity.posY, entity.posZ - (double)radius, entity.posX + (double)radius, entity.posY + 1.0D, entity.posZ + (double)radius);
            BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
            IBlockState collState = world.getBlockState(pos);
            if (!TreeHelper.isLeaves(collState) && !TreeHelper.isBranch(collState) && collState.getBlock() != ModBlocks.blockTrunkShell) {
               if (collState.getBlock() instanceof BlockLiquid) {
                  entity.motionY += 0.029999999329447746D;
                  entity.motionX *= 0.800000011920929D;
                  entity.motionY *= 0.800000011920929D;
                  entity.motionZ *= 0.800000011920929D;
                  var10000 = this.getData(entity);
                  var10000.rotYaw *= 0.8F;
                  var10000 = this.getData(entity);
                  var10000.rotPit *= 0.8F;
                  entity.motionY += 0.01D;
                  entity.onFire = false;
               } else {
                  AxisAlignedBB collBox = collState.getCollisionBoundingBox(world, pos);
                  if (collBox != null) {
                     collBox = collBox.offset(pos);
                     if (fallBox.intersects(collBox)) {
                        entity.motionY = 0.0D;
                        entity.posY = collBox.maxY;
                        entity.prevPosY = entity.posY;
                        entity.landed = true;
                        entity.onGround = true;
                        if (entity.onFire && entity.world.isAirBlock(pos.up())) {
                           entity.world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                        }
                     }
                  }
               }
            }

         }
      }
   }

   public void dropPayload(EntityFallingTree entity) {
      World world = entity.world;
      entity.getPayload().forEach((i) -> {
         Block.spawnAsEntity(world, new BlockPos(entity.posX, entity.posY, entity.posZ), i);
      });
      entity.getDestroyData().leavesDrops.forEach((bis) -> {
         Block.spawnAsEntity(world, entity.getDestroyData().cutPos.add(bis.pos), bis.stack);
      });
   }

   public boolean shouldDie(EntityFallingTree entity) {
      boolean dead = entity.landed || entity.ticksExisted > 120;
      if (dead) {
         entity.cleanupRootyDirt();
      }

      return dead;
   }

   @SideOnly(Side.CLIENT)
   public void renderTransform(EntityFallingTree entity, float entityYaw, float partialTicks) {
      float yaw = MathHelper.wrapDegrees(com.ferreusveritas.dynamictrees.util.MathHelper.angleDegreesInterpolate(entity.prevRotationYaw, entity.rotationYaw, partialTicks));
      float pit = MathHelper.wrapDegrees(com.ferreusveritas.dynamictrees.util.MathHelper.angleDegreesInterpolate(entity.prevRotationPitch, entity.rotationPitch, partialTicks));
      Vec3d mc = entity.getMassCenter();
      GlStateManager.translate(mc.x, mc.y, mc.z);
      GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(pit, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-mc.x - 0.5D, -mc.y, -mc.z - 0.5D);
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldRender(EntityFallingTree entity) {
      return true;
   }

   class HandlerData extends AnimationHandlerData {
      float rotYaw = 0.0F;
      float rotPit = 0.0F;

      HandlerData() {
      }
   }
}
