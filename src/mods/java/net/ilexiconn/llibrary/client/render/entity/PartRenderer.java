package net.ilexiconn.llibrary.client.render.entity;

import net.ilexiconn.llibrary.server.entity.multipart.PartEntity;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PartRenderer extends Render<PartEntity> {
   protected PartRenderer(RenderManager renderManager) {
      super(renderManager);
   }

   protected ResourceLocation getEntityTexture(PartEntity entity) {
      return null;
   }

   public static class Factory implements IRenderFactory<PartEntity> {
      public Factory() {
      }

      public Render<? super PartEntity> createRenderFor(RenderManager manager) {
         return new PartRenderer(manager);
      }
   }
}
