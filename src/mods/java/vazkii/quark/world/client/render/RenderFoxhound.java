package vazkii.quark.world.client.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.world.client.layer.LayerFoxhoundCollar;
import vazkii.quark.world.client.model.ModelFoxhound;
import vazkii.quark.world.entity.EntityFoxhound;

public class RenderFoxhound extends RenderLiving<EntityFoxhound> {
   private static final ResourceLocation FOXHOUND_IDLE = new ResourceLocation("quark", "textures/entity/foxhound_idle.png");
   private static final ResourceLocation FOXHOUND_ANGRY = new ResourceLocation("quark", "textures/entity/foxhound_angry.png");
   private static final ResourceLocation FOXHOUND_SLEEPING = new ResourceLocation("quark", "textures/entity/foxhound_sleeping.png");

   public RenderFoxhound(RenderManager render) {
      super(render, new ModelFoxhound(), 0.5F);
      this.addLayer(new LayerFoxhoundCollar(this));
   }

   @Nullable
   protected ResourceLocation getEntityTexture(@Nonnull EntityFoxhound entity) {
      return entity.isSleeping() ? FOXHOUND_SLEEPING : (entity.isAngry() ? FOXHOUND_ANGRY : FOXHOUND_IDLE);
   }
}
