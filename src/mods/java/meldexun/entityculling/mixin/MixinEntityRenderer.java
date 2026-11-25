package meldexun.entityculling.mixin;

import meldexun.entityculling.EntityCulling;
import meldexun.entityculling.config.EntityCullingConfig;
import meldexun.entityculling.integration.Hats;
import meldexun.entityculling.util.ICullable;
import meldexun.entityculling.util.culling.CullingInstance;
import meldexun.renderlib.api.IBoundingBoxCache;
import meldexun.renderlib.renderer.entity.EntityRenderer;
import meldexun.renderlib.util.MutableAABB;
import meldexun.renderlib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer {
   @Unique
   protected final MutableAABB aabb = new MutableAABB();

   public MixinEntityRenderer() {
   }

   @Overwrite(
      remap = false
   )
   protected <T extends Entity> void setCanBeOcclusionCulled(T entity, boolean canBeOcclusionCulled) {
      ((ICullable)entity).setCanBeOcclusionCulled(canBeOcclusionCulled);
   }

   @Overwrite(
      remap = false
   )
   protected <T extends Entity> boolean isOcclusionCulled(T entity) {
      if (RenderUtil.isRecursive()) {
         return false;
      } else if (EntityCulling.useOpenGlBasedCulling()) {
         EntityCulling.cpuTimer.start();

         boolean cullingInstance;
         try {
            if (EntityCullingConfig.enabled) {
               if (EntityCullingConfig.disabledInSpectator && Minecraft.getMinecraft().player.isSpectator()) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               if (!EntityCullingConfig.entity.skipHiddenEntityRendering) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               if (EntityCulling.isHatsInstalled && Hats.isHat(entity)) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               if (EntityCullingConfig.entity.alwaysRenderBosses && !entity.isNonBoss()) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               if (EntityCullingConfig.entity.alwaysRenderEntitiesWithName && entity.getAlwaysRenderNameTagForRender()) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               if (EntityCullingConfig.entity.alwaysRenderPlayers && entity instanceof EntityPlayer) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               if (EntityCullingConfig.entity.alwaysRenderViewEntity && entity == Minecraft.getMinecraft().getRenderViewEntity()) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               if (((Boolean)EntityCullingConfig.entity.skipHiddenEntityRenderingBlacklistImpl.get(entity)).booleanValue()) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               CullingInstance cullingInstance = CullingInstance.getInstance();
               ICullable.CullInfo cullInfo = ((ICullable)entity).getCullInfo();
               boolean culled = !cullingInstance.isVisible(cullInfo);
               this.aabb.set(((IBoundingBoxCache)entity).getCachedBoundingBox());
               this.aabb.expand(entity.posX - entity.lastTickPosX, entity.posY - entity.lastTickPosY, entity.posZ - entity.lastTickPosZ, RenderUtil.getPartialTickDelta());
               cullingInstance.addBox(cullInfo, this.aabb.minX(), this.aabb.minY(), this.aabb.minZ(), this.aabb.maxX(), this.aabb.maxY(), this.aabb.maxZ());
               boolean var5 = culled;
               return var5;
            }

            cullingInstance = false;
         } finally {
            EntityCulling.cpuTimer.stop();
         }

         return cullingInstance;
      } else {
         return ((ICullable)entity).isCulled();
      }
   }
}
