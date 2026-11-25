package paulevs.betternether.entities.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import paulevs.betternether.entities.EntityFirefly;
import paulevs.betternether.entities.models.ModelFirefly;

public class RenderFirefly extends RenderLiving<EntityFirefly> {
   private ResourceLocation texture = new ResourceLocation("betternether", "textures/entities/firefly.png");

   public RenderFirefly(RenderManager rendermanagerIn) {
      super(rendermanagerIn, new ModelFirefly(), 0.1F);
   }

   protected ResourceLocation getEntityTexture(EntityFirefly entity) {
      return this.texture;
   }
}
