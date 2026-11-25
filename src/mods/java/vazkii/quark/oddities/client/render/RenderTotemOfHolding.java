package vazkii.quark.oddities.client.render;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.arl.client.AtlasSpriteHelper;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.oddities.entity.EntityTotemOfHolding;
import vazkii.quark.oddities.feature.TotemOfHolding;

public class RenderTotemOfHolding extends Render<EntityTotemOfHolding> {
   protected RenderTotemOfHolding(RenderManager renderManager) {
      super(renderManager);
   }

   public void doRender(@Nonnull EntityTotemOfHolding entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      int deathTicks = entity.getDeathTicks();
      boolean dying = entity.isDying();
      float time = (float)ClientTicker.ticksInGame + partialTicks;
      float scale = !dying ? 1.0F : Math.max(0.0F, 40.0F - ((float)deathTicks + partialTicks)) / 40.0F;
      float rotation = time + (!dying ? 0.0F : ((float)deathTicks + partialTicks) * 5.0F);
      double translation = !dying ? Math.sin((double)time * 0.03D) * 0.1D : (double)(((float)deathTicks + partialTicks) / 40.0F * 5.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(-0.5D, translation, 0.0D);
      GlStateManager.scale(scale, scale, scale);
      this.renderTotemIcon();
      GlStateManager.popMatrix();
   }

   protected boolean canRenderName(EntityTotemOfHolding entity) {
      if (!entity.hasCustomName()) {
         return false;
      } else {
         Minecraft mc = Minecraft.getMinecraft();
         return !mc.gameSettings.hideGUI && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == Type.ENTITY && mc.objectMouseOver.entityHit == entity;
      }
   }

   private void renderTotemIcon() {
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
      Minecraft mc = Minecraft.getMinecraft();
      mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      AtlasSpriteHelper.renderIconThicc(TotemOfHolding.totemSprite, 0.0625F);
   }

   protected ResourceLocation getEntityTexture(@Nonnull EntityTotemOfHolding entity) {
      return null;
   }

   public static IRenderFactory<EntityTotemOfHolding> factory() {
      return RenderTotemOfHolding::new;
   }
}
