package lykrast.defiledlands.client.render.entity;

import lykrast.defiledlands.client.model.ModelBookWyrm;
import lykrast.defiledlands.common.entity.passive.EntityBookWyrm;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderBookWyrm extends RenderLiving<EntityBookWyrm> {
   private static final ResourceLocation TEXTURES = new ResourceLocation("defiledlands", "textures/entity/book_wyrm.png");
   private static final ResourceLocation TEXTURES_GOLDEN = new ResourceLocation("defiledlands", "textures/entity/book_wyrm_golden.png");

   public RenderBookWyrm(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelBookWyrm(), 0.7F);
   }

   protected ResourceLocation getEntityTexture(EntityBookWyrm entity) {
      return entity.isGolden() ? TEXTURES_GOLDEN : TEXTURES;
   }
}
