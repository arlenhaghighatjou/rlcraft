package lykrast.defiledlands.client.render.entity.layers;

import lykrast.defiledlands.client.render.entity.RenderShamblerTwisted;
import lykrast.defiledlands.common.entity.monster.EntityShamblerTwisted;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerShamblerTwistedEyes implements LayerRenderer<EntityShamblerTwisted> {
   private static final ResourceLocation RES_ENDERMAN_EYES = new ResourceLocation("defiledlands", "textures/entity/shambler_twisted_eyes.png");
   private final RenderShamblerTwisted endermanRenderer;

   public LayerShamblerTwistedEyes(RenderShamblerTwisted endermanRendererIn) {
      this.endermanRenderer = endermanRendererIn;
   }

   public void doRenderLayer(EntityShamblerTwisted entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.endermanRenderer.bindTexture(RES_ENDERMAN_EYES);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
      if (entitylivingbaseIn.isInvisible()) {
         GlStateManager.depthMask(false);
      } else {
         GlStateManager.depthMask(true);
      }

      int i = 61680;
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
      this.endermanRenderer.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
      i = entitylivingbaseIn.getBrightnessForRender();
      j = i % 65536;
      k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
      this.endermanRenderer.setLightmap(entitylivingbaseIn);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
