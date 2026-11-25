package vazkii.quark.world.client.render;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.world.client.layer.LayerAshenClothes;
import vazkii.quark.world.client.layer.LayerAshenHeldItem;

public class RenderAshen extends RenderSkeleton {
   private static final ResourceLocation TEXTURE = new ResourceLocation("quark", "textures/entity/ashen.png");
   public static final IRenderFactory<EntitySkeleton> FACTORY = RenderAshen::new;

   public RenderAshen(RenderManager renderManagerIn) {
      super(renderManagerIn);
      List<LayerRenderer> removals = new ArrayList();

      for(LayerRenderer layer : this.layerRenderers) {
         if (layer instanceof LayerHeldItem) {
            removals.add(layer);
         }
      }

      this.layerRenderers.removeAll(removals);
      this.addLayer(new LayerAshenHeldItem(this));
      this.addLayer(new LayerAshenClothes(this));
   }

   protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
      return TEXTURE;
   }
}
