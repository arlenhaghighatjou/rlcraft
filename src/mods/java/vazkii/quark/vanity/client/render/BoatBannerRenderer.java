package vazkii.quark.vanity.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.vanity.feature.BoatSails;

public class BoatBannerRenderer {
   private static final TileEntityBanner BANNER_TE = new TileEntityBanner();

   public BoatBannerRenderer() {
   }

   public static void renderBanner(EntityBoat boat, float partialTicks) {
      if (ModuleLoader.isFeatureEnabled(BoatSails.class)) {
         ItemStack banner = BoatSails.getBanner(boat);
         if (!banner.isEmpty()) {
            BANNER_TE.setItemValues(banner, false);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5F, 0.15F, -1.45F);
            TileEntityRendererDispatcher.instance.render(BANNER_TE, 0.0D, 0.0D, 0.0D, partialTicks);
            GlStateManager.popMatrix();
         }

      }
   }
}
