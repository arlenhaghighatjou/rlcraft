package vazkii.quark.world.client.layer;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.world.client.render.RenderFoxhound;
import vazkii.quark.world.entity.EntityFoxhound;

public class LayerFoxhoundCollar implements LayerRenderer<EntityFoxhound> {
   private static final ResourceLocation WOLF_COLLAR = new ResourceLocation("quark", "textures/entity/foxhound_collar.png");
   private final RenderFoxhound wolfRenderer;

   public LayerFoxhoundCollar(RenderFoxhound wolfRendererIn) {
      this.wolfRenderer = wolfRendererIn;
   }

   public void doRenderLayer(@Nonnull EntityFoxhound foxhound, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (foxhound.isTamed() && !foxhound.isInvisible()) {
         this.wolfRenderer.bindTexture(WOLF_COLLAR);
         float[] afloat = foxhound.getCollarColor().getColorComponentValues();
         GlStateManager.color(afloat[0], afloat[1], afloat[2]);
         this.wolfRenderer.getMainModel().render(foxhound, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      }

   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
