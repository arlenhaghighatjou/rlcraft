package com.ferreusveritas.dynamictrees.entities.animation;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.entities.EntityFallingTree;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AnimationHandlerFallover implements IAnimationHandler {
   public AnimationHandlerFallover() {
   }

   public String getName() {
      return "fallover";
   }

   AnimationHandlerFallover.HandlerData getData(EntityFallingTree entity) {
      return entity.animationHandlerData instanceof AnimationHandlerFallover.HandlerData ? (AnimationHandlerFallover.HandlerData)entity.animationHandlerData : new AnimationHandlerFallover.HandlerData();
   }

   public void initMotion(EntityFallingTree entity) {
      entity.animationHandlerData = new AnimationHandlerFallover.HandlerData();
      EntityFallingTree.standardDropLeavesPayLoad(entity);
      BlockPos belowBlock = entity.getDestroyData().cutPos.down();
      if (entity.world.getBlockState(belowBlock).isSideSolid(entity.world, belowBlock, EnumFacing.UP)) {
         entity.onGround = true;
      }

   }

   public void handleMotion(EntityFallingTree entity) {
      float fallSpeed = this.getData(entity).fallSpeed;
      if (entity.onGround) {
         float height = (float)entity.getMassCenter().y * 2.0F;
         fallSpeed = (float)((double)fallSpeed + 0.2D / (double)height);
         this.addRotation(entity, fallSpeed);
      }

      entity.motionY -= 0.029999999329447746D;
      entity.posY += entity.motionY;
      World world = entity.world;
      int radius = 8;
      IBlockState state = entity.getDestroyData().getBranchBlockState(0);
      if (TreeHelper.isBranch(state)) {
         radius = ((BlockBranch)state.getBlock()).getRadius(state);
      }

      AxisAlignedBB fallBox = new AxisAlignedBB(entity.posX - (double)radius, entity.posY, entity.posZ - (double)radius, entity.posX + (double)radius, entity.posY + 1.0D, entity.posZ + (double)radius);
      BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);
      IBlockState collState = world.getBlockState(pos);
      AxisAlignedBB collBox = collState.getCollisionBoundingBox(world, pos);
      if (collBox != null) {
         collBox = collBox.offset(pos);
         if (fallBox.intersects(collBox)) {
            entity.motionY = 0.0D;
            entity.posY = collBox.maxY;
            entity.prevPosY = entity.posY;
            entity.onGround = true;
         }
      }

      if (fallSpeed > 0.0F && this.testCollision(entity)) {
         this.addRotation(entity, -fallSpeed);
         ++this.getData(entity).bounces;
         fallSpeed *= -0.25F;
         entity.landed = Math.abs(fallSpeed) < 0.02F;
      }

      world = entity.world;
      if (ModConfigs.enableFallingTreeDamage && !world.isRemote) {
         for(EntityLivingBase living : this.testEntityCollision(entity)) {
            if (!this.getData(entity).entitiesHit.contains(living)) {
               this.getData(entity).entitiesHit.add(living);
               float damage = entity.getDestroyData().woodVolume * Math.abs(fallSpeed) * 3.0F;
               if (this.getData(entity).bounces == 0 && damage > 2.0F) {
                  living.motionX += (double)(world.rand.nextFloat() * (float)entity.getDestroyData().toolDir.getOpposite().getFrontOffsetX() * damage * 0.2F);
                  living.motionX += (double)world.rand.nextFloat() - 0.5D;
                  living.motionY += (double)(world.rand.nextFloat() * fallSpeed * 0.25F);
                  living.motionZ += (double)(world.rand.nextFloat() * (float)entity.getDestroyData().toolDir.getOpposite().getFrontOffsetZ() * damage * 0.2F);
                  living.motionZ += (double)world.rand.nextFloat() - 0.5D;
                  damage = damage * ModConfigs.fallingTreeDamageMultiplier;
                  living.attackEntityFrom(AnimationConstants.TREE_DAMAGE, damage);
               }
            }
         }
      }

      this.getData(entity).fallSpeed = fallSpeed;
   }

   private boolean testCollision(EntityFallingTree entity) {
      EnumFacing toolDir = entity.getDestroyData().toolDir;
      float actingAngle = toolDir.getAxis() == Axis.X ? entity.rotationYaw : entity.rotationPitch;
      int offsetX = toolDir.getFrontOffsetX();
      int offsetZ = toolDir.getFrontOffsetZ();
      float h = MathHelper.sin((float)Math.toRadians((double)actingAngle)) * (float)(offsetX | offsetZ);
      float v = MathHelper.cos((float)Math.toRadians((double)actingAngle));
      float xbase = (float)(entity.posX + (double)((float)offsetX * (-0.5F + v * 0.5F + h * 0.5F)));
      float ybase = (float)(entity.posY - (double)(h * 0.5F) + (double)(v * 0.5F));
      float zbase = (float)(entity.posZ + (double)((float)offsetZ * (-0.5F + v * 0.5F + h * 0.5F)));
      int trunkHeight = entity.getDestroyData().trunkHeight;
      float maxRadius = (float)entity.getDestroyData().getBranchRadius(0) / 16.0F;
      trunkHeight = Math.min(trunkHeight, 24);

      for(int segment = 0; segment < trunkHeight; ++segment) {
         float segX = xbase + h * (float)segment * (float)offsetX;
         float segY = ybase + v * (float)segment;
         float segZ = zbase + h * (float)segment * (float)offsetZ;
         float tex = 0.0625F;
         float half = MathHelper.clamp(tex * (float)(segment + 1) * 2.0F, tex, maxRadius);
         AxisAlignedBB testBB = new AxisAlignedBB((double)(segX - half), (double)(segY - half), (double)(segZ - half), (double)(segX + half), (double)(segY + half), (double)(segZ + half));
         if (!entity.world.getCollisionBoxes(entity, testBB).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private void addRotation(EntityFallingTree entity, float delta) {
      EnumFacing toolDir = entity.getDestroyData().toolDir;
      switch(toolDir) {
      case NORTH:
         entity.rotationPitch += delta;
         break;
      case SOUTH:
         entity.rotationPitch -= delta;
         break;
      case WEST:
         entity.rotationYaw += delta;
         break;
      case EAST:
         entity.rotationYaw -= delta;
      }

      entity.rotationPitch = MathHelper.wrapDegrees(entity.rotationPitch);
      entity.rotationYaw = MathHelper.wrapDegrees(entity.rotationYaw);
   }

   public List<EntityLivingBase> testEntityCollision(EntityFallingTree entity) {
      World world = entity.world;
      EnumFacing toolDir = entity.getDestroyData().toolDir;
      float actingAngle = toolDir.getAxis() == Axis.X ? entity.rotationYaw : entity.rotationPitch;
      int offsetX = toolDir.getFrontOffsetX();
      int offsetZ = toolDir.getFrontOffsetZ();
      float h = MathHelper.sin((float)Math.toRadians((double)actingAngle)) * (float)(offsetX | offsetZ);
      float v = MathHelper.cos((float)Math.toRadians((double)actingAngle));
      float xbase = (float)(entity.posX + (double)((float)offsetX * (-0.5F + v * 0.5F + h * 0.5F)));
      float ybase = (float)(entity.posY - (double)(h * 0.5F) + (double)(v * 0.5F));
      float zbase = (float)(entity.posZ + (double)((float)offsetZ * (-0.5F + v * 0.5F + h * 0.5F)));
      int trunkHeight = entity.getDestroyData().trunkHeight;
      float segX = xbase + h * (float)(trunkHeight - 1) * (float)offsetX;
      float segY = ybase + v * (float)(trunkHeight - 1);
      float segZ = zbase + h * (float)(trunkHeight - 1) * (float)offsetZ;
      final float maxRadius = (float)entity.getDestroyData().getBranchRadius(0) / 16.0F;
      final Vec3d vec3d1 = new Vec3d((double)xbase, (double)ybase, (double)zbase);
      final Vec3d vec3d2 = new Vec3d((double)segX, (double)segY, (double)segZ);
      return (List)world.getEntitiesInAABBexcluding(entity, new AxisAlignedBB(vec3d1.x, vec3d1.y, vec3d1.z, vec3d2.x, vec3d2.y, vec3d2.z), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
         public boolean apply(@Nullable Entity apply) {
            if (apply instanceof EntityLivingBase && apply.canBeCollidedWith()) {
               AxisAlignedBB axisalignedbb = apply.getEntityBoundingBox().grow((double)maxRadius);
               return axisalignedbb.contains(vec3d1) || axisalignedbb.calculateIntercept(vec3d1, vec3d2) != null;
            } else {
               return false;
            }
         }
      })).stream().map((a) -> {
         return (EntityLivingBase)a;
      }).collect(Collectors.toList());
   }

   public void dropPayload(EntityFallingTree entity) {
      World world = entity.world;
      BlockPos cutPos = entity.getDestroyData().cutPos;
      entity.getPayload().forEach((i) -> {
         Block.spawnAsEntity(world, cutPos, i);
      });
   }

   public boolean shouldDie(EntityFallingTree entity) {
      boolean dead = Math.abs(entity.rotationPitch) >= 160.0F || Math.abs(entity.rotationYaw) >= 160.0F || entity.landed || entity.ticksExisted > 120 + entity.getDestroyData().trunkHeight;
      if (dead) {
         entity.cleanupRootyDirt();
      }

      return dead;
   }

   @SideOnly(Side.CLIENT)
   public void renderTransform(EntityFallingTree entity, float entityYaw, float partialTicks) {
      float yaw = MathHelper.wrapDegrees(com.ferreusveritas.dynamictrees.util.MathHelper.angleDegreesInterpolate(entity.prevRotationYaw, entity.rotationYaw, partialTicks));
      float pit = MathHelper.wrapDegrees(com.ferreusveritas.dynamictrees.util.MathHelper.angleDegreesInterpolate(entity.prevRotationPitch, entity.rotationPitch, partialTicks));
      int radius = entity.getDestroyData().getBranchRadius(0);
      EnumFacing toolDir = entity.getDestroyData().toolDir;
      Vec3d toolVec = (new Vec3d((double)toolDir.getFrontOffsetX(), (double)toolDir.getFrontOffsetY(), (double)toolDir.getFrontOffsetZ())).scale((double)((float)radius / 16.0F));
      GlStateManager.translate(-toolVec.x, -toolVec.y, -toolVec.z);
      GlStateManager.rotate(-yaw, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(pit, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(toolVec.x, toolVec.y, toolVec.z);
      GlStateManager.translate(-0.5D, 0.0D, -0.5D);
   }

   @SideOnly(Side.CLIENT)
   public boolean shouldRender(EntityFallingTree entity) {
      return true;
   }

   class HandlerData extends AnimationHandlerData {
      float fallSpeed = 0.0F;
      int bounces = 0;
      HashSet<EntityLivingBase> entitiesHit = new HashSet();

      HandlerData() {
      }
   }
}
