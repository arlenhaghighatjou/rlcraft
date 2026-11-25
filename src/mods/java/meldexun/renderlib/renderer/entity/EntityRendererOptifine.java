package meldexun.renderlib.renderer.entity;

import meldexun.renderlib.integration.Optifine;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;

public class EntityRendererOptifine extends EntityRenderer {
   public EntityRendererOptifine() {
   }

   protected void renderEntities(float partialTicks, EntityRenderList entityList) {
      super.renderEntities(partialTicks, entityList);
      Minecraft mc = Minecraft.getMinecraft();
      if (!this.isRenderEntityOutlines() && (!entityList.getOutlineEntities().isEmpty() || mc.renderGlobal.entityOutlinesRendered)) {
         mc.world.profiler.endStartSection("entityOutlines");
         mc.renderGlobal.entityOutlinesRendered = !entityList.getOutlineEntities().isEmpty();
         if (!entityList.getOutlineEntities().isEmpty()) {
            if (Optifine.isShaders()) {
               Optifine.beginEntitiesGlowing();
            }

            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            mc.entityRenderer.disableLightmap();
            RenderHelper.disableStandardItemLighting();
            mc.getRenderManager().setRenderOutlines(true);

            for(Entity entity : entityList.getOutlineEntities()) {
               if (Optifine.isShaders()) {
                  Optifine.nextEntity(entity);
               }

               mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);
            }

            mc.getRenderManager().setRenderOutlines(false);
            RenderHelper.enableStandardItemLighting();
            mc.entityRenderer.enableLightmap();
            GlStateManager.enableDepth();
            GlStateManager.enableFog();
            if (Optifine.isShaders()) {
               Optifine.endEntitiesGlowing();
            }
         }
      }

   }

   protected void preRenderEntity(Entity entity) {
      if (Optifine.isShaders()) {
         Optifine.nextEntity(entity);
      }

      Optifine.setRenderedEntity(entity);
      super.preRenderEntity(entity);
   }

   protected void postRenderEntity() {
      super.postRenderEntity();
      Optifine.setRenderedEntity((Entity)null);
   }

   protected boolean isRenderEntityOutlines() {
      if (Optifine.isFastRender()) {
         return false;
      } else if (Optifine.isShaders()) {
         return false;
      } else {
         return Optifine.isAntialiasing() ? false : super.isRenderEntityOutlines();
      }
   }
}
