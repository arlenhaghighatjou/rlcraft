package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.client.render.entity.RenderPixie;
import com.github.alexthe666.iceandfire.entity.EntityPixie;
import com.github.alexthe666.iceandfire.entity.StoneEntityProperties;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerPixieGlow implements LayerRenderer<EntityPixie> {
   private final RenderPixie render;

   public LayerPixieGlow(RenderPixie renderIn) {
      this.render = renderIn;
   }

   public void doRenderLayer(EntityPixie pixie, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(pixie, StoneEntityProperties.class);
      if (properties != null && !properties.isStone || properties == null) {
         switch(pixie.getColor()) {
         case 1:
            this.render.bindTexture(RenderPixie.TEXTURE_1);
            break;
         case 2:
            this.render.bindTexture(RenderPixie.TEXTURE_2);
            break;
         case 3:
            this.render.bindTexture(RenderPixie.TEXTURE_3);
            break;
         case 4:
            this.render.bindTexture(RenderPixie.TEXTURE_4);
            break;
         case 5:
            this.render.bindTexture(RenderPixie.TEXTURE_5);
            break;
         default:
            this.render.bindTexture(RenderPixie.TEXTURE_0);
         }

         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(!pixie.isInvisible());
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
         GlStateManager.enableLighting();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.render.getMainModel().render(pixie, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         this.render.setLightmap(pixie);
         GlStateManager.depthMask(true);
         GlStateManager.disableBlend();
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
