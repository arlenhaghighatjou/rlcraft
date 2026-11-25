package familiarfauna.entities.render;

import familiarfauna.entities.EntityPixie;
import familiarfauna.entities.model.ModelPixie;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPixie extends RenderLiving<EntityPixie> {
   private static final ResourceLocation PINK = new ResourceLocation("familiarfauna:textures/entity/pixie/pink.png");
   private static final ResourceLocation BLUE = new ResourceLocation("familiarfauna:textures/entity/pixie/blue.png");
   private static final ResourceLocation PURPLE = new ResourceLocation("familiarfauna:textures/entity/pixie/purple.png");

   public RenderPixie(RenderManager renderManager) {
      super(renderManager, new ModelPixie(), 0.25F);
      this.shadowSize = 0.0F;
   }

   protected ResourceLocation getEntityTexture(EntityPixie entity) {
      switch(entity.getPixieType()) {
      case 0:
      default:
         return PINK;
      case 1:
         return BLUE;
      case 2:
         return PURPLE;
      }
   }

   public void doRender(EntityPixie entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(true);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
      GlStateManager.enableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }
}
