package meldexun.entityculling.mixin;

import meldexun.entityculling.util.ICullable;
import meldexun.renderlib.integration.Optifine;
import meldexun.renderlib.renderer.tileentity.TileEntityRenderer;
import meldexun.renderlib.renderer.tileentity.TileEntityRendererOptifine;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({TileEntityRendererOptifine.class})
public class MixinTileEntityRendererOptifine extends TileEntityRenderer {
   public MixinTileEntityRendererOptifine() {
   }

   protected <T extends TileEntity> boolean isOcclusionCulled(T tileEntity) {
      return Optifine.isShadowPass() ? ((ICullable)tileEntity).isShadowCulled() : super.isOcclusionCulled(tileEntity);
   }
}
