package svenhjol.charm.world.client.layer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.world.client.render.RenderSpectre;
import svenhjol.charm.world.entity.EntitySpectre;

public class LayerSpectreEyes implements LayerRenderer<EntitySpectre> {
   public static final ResourceLocation TEXTURE = new ResourceLocation("charm", "textures/entity/spectre/spectre_eyes.png");
   public final RenderSpectre spectreRenderer;

   public LayerSpectreEyes(RenderSpectre spectreRenderer) {
      this.spectreRenderer = spectreRenderer;
   }

   public void doRenderLayer(EntitySpectre spectre, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      boolean blinking = ageInTicks % 200.0F < 6.0F;
      if (!blinking) {
         this.spectreRenderer.bindTexture(TEXTURE);
         GlStateManager.enableBlend();
         GlStateManager.disableAlpha();
         GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
         if (spectre.isInvisible()) {
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
         this.spectreRenderer.getMainModel().render(spectre, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
         i = spectre.getBrightnessForRender();
         j = i % 65536;
         k = i / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
         this.spectreRenderer.setLightmap(spectre);
         GlStateManager.disableBlend();
         GlStateManager.enableAlpha();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
