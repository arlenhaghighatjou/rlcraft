package lykrast.defiledlands.client.render.entity.layers;

import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import lykrast.defiledlands.client.render.entity.RenderSlimeDefiled;
import lykrast.defiledlands.common.entity.monster.EntitySlimeDefiled;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerSlimeDefiledGel implements LayerRenderer<EntitySlimeDefiled> {
   private final RenderSlimeDefiled slimeRenderer;
   private final ModelBase slimeModel = new ModelSlime(0);

   public LayerSlimeDefiledGel(RenderSlimeDefiled slimeRendererIn) {
      this.slimeRenderer = slimeRendererIn;
   }

   public void doRenderLayer(EntitySlimeDefiled entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!entitylivingbaseIn.isInvisible()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA);
         this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
         this.slimeModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         GlStateManager.disableBlend();
      }

   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
