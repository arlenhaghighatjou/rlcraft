package vazkii.quark.misc.client.render;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.misc.entity.EntityArrowEnder;
import vazkii.quark.misc.entity.EntityArrowExplosive;
import vazkii.quark.misc.entity.EntityArrowTorch;

public class RenderExtraArrow<T extends EntityArrow> extends RenderArrow<T> {
   public static final IRenderFactory<EntityArrowEnder> FACTORY_ENDER = (manager) -> {
      return new RenderExtraArrow(manager, new ResourceLocation("quark", "textures/entity/arrow_ender.png"));
   };
   public static final IRenderFactory<EntityArrowExplosive> FACTORY_EXPLOSIVE = (manager) -> {
      return new RenderExtraArrow(manager, new ResourceLocation("quark", "textures/entity/arrow_explosive.png"));
   };
   public static final IRenderFactory<EntityArrowTorch> FACTORY_TORCH = (manager) -> {
      return new RenderExtraArrow(manager, new ResourceLocation("quark", "textures/entity/arrow_torch.png"));
   };
   private final ResourceLocation res;

   public RenderExtraArrow(RenderManager renderManagerIn, ResourceLocation res) {
      super(renderManagerIn);
      this.res = res;
   }

   protected ResourceLocation getEntityTexture(@Nonnull EntityArrow entity) {
      return this.res;
   }
}
