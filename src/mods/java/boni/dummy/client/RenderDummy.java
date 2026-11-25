package boni.dummy.client;

import boni.dummy.EntityDummy;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderDummy extends RenderBiped<EntityDummy> {
   private static final ResourceLocation texture = new ResourceLocation("testdummy", "textures/entity/dummy.png");
   public static final ModelDummy model = new ModelDummy(0.0F, 0.0F);

   public RenderDummy(RenderManager renderManager) {
      super(renderManager, model, 0.125F);
      this.addLayer(new LayerDummyArmor(this));
   }

   protected ResourceLocation getEntityTexture(EntityDummy p_110775_1_) {
      return texture;
   }

   protected void func_82421_b() {
   }
}
