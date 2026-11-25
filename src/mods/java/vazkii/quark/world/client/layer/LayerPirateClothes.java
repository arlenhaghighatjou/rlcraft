package vazkii.quark.world.client.layer;

import javax.annotation.Nonnull;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class LayerPirateClothes implements LayerRenderer<EntitySkeleton> {
   private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/pirate_overlay.png");
   private final RenderLivingBase<?> render;
   private final ModelSkeleton model;

   public LayerPirateClothes(RenderLivingBase<?> render) {
      this.render = render;
      this.model = new ModelSkeleton(0.25F, true);
   }

   public void doRenderLayer(@Nonnull EntitySkeleton entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.model.setModelAttributes(this.render.getMainModel());
      this.model.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
      this.render.bindTexture(TEXTURE);
      this.model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}
