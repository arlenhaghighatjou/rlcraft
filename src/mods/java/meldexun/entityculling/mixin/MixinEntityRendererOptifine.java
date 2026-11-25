package meldexun.entityculling.mixin;

import meldexun.entityculling.util.ICullable;
import meldexun.renderlib.integration.Optifine;
import meldexun.renderlib.renderer.entity.EntityRenderer;
import meldexun.renderlib.renderer.entity.EntityRendererOptifine;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityRendererOptifine.class})
public class MixinEntityRendererOptifine extends EntityRenderer {
   public MixinEntityRendererOptifine() {
   }

   protected <T extends Entity> boolean isOcclusionCulled(T entity) {
      return Optifine.isShadowPass() ? ((ICullable)entity).isShadowCulled() : super.isOcclusionCulled(entity);
   }
}
