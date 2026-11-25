package vazkii.quark.world.client.render;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.world.entity.EntityPickarang;

public class RenderPickarang extends Render<EntityPickarang> {
   public static final IRenderFactory<EntityPickarang> FACTORY = RenderPickarang::new;

   protected RenderPickarang(RenderManager renderManager) {
      super(renderManager);
   }

   public void doRender(@Nonnull EntityPickarang entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y + 0.20000000298023224D, z);
      GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
      Minecraft mc = Minecraft.getMinecraft();
      float time = (float)entity.ticksExisted + (mc.isGamePaused() ? 0.0F : partialTicks);
      GlStateManager.rotate(time * 20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.enableBlend();
      mc.getRenderItem().renderItem(entity.getStack(), TransformType.FIXED);
      GlStateManager.popMatrix();
   }

   protected ResourceLocation getEntityTexture(@Nonnull EntityPickarang entity) {
      return null;
   }
}
