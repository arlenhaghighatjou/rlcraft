package com.tmtravlr.potioncore;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class PotionCoreEntityRenderer extends EntityRenderer {
   private Minecraft mc;
   private Entity pointedEntity;

   public PotionCoreEntityRenderer(Minecraft mcIn, IResourceManager manager) {
      super(mcIn, manager);
      this.mc = mcIn;
   }

   public void getMouseOver(float partialTickTime) {
      Entity viewEntity = this.mc.getRenderViewEntity();
      if (viewEntity != null && this.mc.world != null) {
         this.mc.mcProfiler.startSection("pick");
         this.mc.pointedEntity = null;
         double blockReachDistance = PotionCoreAttributes.getPlayerReachDistance(this.mc.player);
         double entityReachDistance = blockReachDistance;
         this.mc.objectMouseOver = viewEntity.rayTrace(blockReachDistance, partialTickTime);
         Vec3d eyePosition = viewEntity.getPositionEyes(partialTickTime);
         if (this.mc.objectMouseOver != null) {
            entityReachDistance = this.mc.objectMouseOver.hitVec.distanceTo(eyePosition);
         }

         Vec3d viewLook = viewEntity.getLook(partialTickTime);
         Vec3d maxViewDistance = eyePosition.addVector(viewLook.x * blockReachDistance, viewLook.y * blockReachDistance, viewLook.z * blockReachDistance);
         this.pointedEntity = null;
         Vec3d entityLookedAtPos = null;
         List list = this.mc.world.getEntitiesInAABBexcluding(viewEntity, viewEntity.getEntityBoundingBox().expand(viewLook.x * blockReachDistance, viewLook.y * blockReachDistance, viewLook.z * blockReachDistance).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
            public boolean apply(@Nullable Entity entity) {
               return entity != null && entity.canBeCollidedWith();
            }
         }));
         double distanceToEntity = entityReachDistance;

         for(int i = 0; i < list.size(); ++i) {
            Entity entity1 = (Entity)list.get(i);
            if (entity1.canBeCollidedWith()) {
               AxisAlignedBB entityBoundingBox = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
               RayTraceResult entityLookedAtTraceResult = entityBoundingBox.calculateIntercept(eyePosition, maxViewDistance);
               if (entityBoundingBox.contains(eyePosition)) {
                  if (distanceToEntity >= 0.0D) {
                     this.pointedEntity = entity1;
                     entityLookedAtPos = entityLookedAtTraceResult == null ? eyePosition : entityLookedAtTraceResult.hitVec;
                     distanceToEntity = 0.0D;
                  }
               } else if (entityLookedAtTraceResult != null) {
                  double currentDistance = eyePosition.distanceTo(entityLookedAtTraceResult.hitVec);
                  if (currentDistance < distanceToEntity || distanceToEntity == 0.0D) {
                     if (entity1 == viewEntity.getRidingEntity() && !viewEntity.canRiderInteract()) {
                        if (distanceToEntity == 0.0D) {
                           this.pointedEntity = entity1;
                           entityLookedAtPos = entityLookedAtTraceResult.hitVec;
                        }
                     } else {
                        this.pointedEntity = entity1;
                        entityLookedAtPos = entityLookedAtTraceResult.hitVec;
                        distanceToEntity = currentDistance;
                     }
                  }
               }
            }
         }

         if (this.pointedEntity != null && (distanceToEntity < entityReachDistance || this.mc.objectMouseOver == null)) {
            this.mc.objectMouseOver = new RayTraceResult(this.pointedEntity, entityLookedAtPos);
            if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
               this.mc.pointedEntity = this.pointedEntity;
            }
         }

         this.mc.mcProfiler.endSection();
      }

   }
}
