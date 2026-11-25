package vazkii.quark.world.client.layer;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import vazkii.quark.world.client.model.ModelArchaeologistHat;
import vazkii.quark.world.entity.EntityArchaeologist;
import vazkii.quark.world.item.ItemArchaeologistHat;

public class LayerArchaeologistHat implements LayerRenderer<EntityArchaeologist> {
   private final RenderLivingBase<?> render;

   public LayerArchaeologistHat(RenderLivingBase<?> render) {
      this.render = render;
   }

   public void doRenderLayer(@Nonnull EntityArchaeologist archaeologist, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.render.bindTexture(ItemArchaeologistHat.TEXTURE);
      if (ItemArchaeologistHat.headModel == null) {
         ItemArchaeologistHat.headModel = new ModelArchaeologistHat();
      }

      ItemArchaeologistHat.headModel.render(archaeologist, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
