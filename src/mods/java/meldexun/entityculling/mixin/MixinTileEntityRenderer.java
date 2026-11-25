package meldexun.entityculling.mixin;

import meldexun.entityculling.EntityCulling;
import meldexun.entityculling.config.EntityCullingConfig;
import meldexun.entityculling.util.ICullable;
import meldexun.entityculling.util.culling.CullingInstance;
import meldexun.renderlib.api.IBoundingBoxCache;
import meldexun.renderlib.renderer.tileentity.TileEntityRenderer;
import meldexun.renderlib.util.MutableAABB;
import meldexun.renderlib.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin({TileEntityRenderer.class})
public class MixinTileEntityRenderer {
   @Unique
   protected final MutableAABB aabb = new MutableAABB();

   public MixinTileEntityRenderer() {
   }

   @Overwrite(
      remap = false
   )
   protected <T extends TileEntity> void setCanBeOcclusionCulled(T tileEntity, boolean canBeOcclusionCulled) {
      ((ICullable)tileEntity).setCanBeOcclusionCulled(canBeOcclusionCulled);
   }

   @Overwrite(
      remap = false
   )
   protected <T extends TileEntity> boolean isOcclusionCulled(T tileEntity) {
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

               if (!EntityCullingConfig.tileEntity.skipHiddenTileEntityRendering) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               if (((Boolean)EntityCullingConfig.tileEntity.skipHiddenTileEntityRenderingBlacklistImpl.get(tileEntity)).booleanValue()) {
                  cullingInstance = false;
                  return cullingInstance;
               }

               CullingInstance cullingInstance = CullingInstance.getInstance();
               ICullable.CullInfo cullInfo = ((ICullable)tileEntity).getCullInfo();
               boolean culled = !cullingInstance.isVisible(cullInfo);
               this.aabb.set(((IBoundingBoxCache)tileEntity).getCachedBoundingBox());
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
         return ((ICullable)tileEntity).isCulled();
      }
   }
}
