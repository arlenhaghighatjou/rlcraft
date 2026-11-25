package vazkii.quark.base.client.gui.config;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import vazkii.quark.base.client.ContributorRewardHandler;
import vazkii.quark.base.client.gui.GuiButtonColor;
import vazkii.quark.base.lib.LibMisc;

public class GuiButtonQ extends GuiButtonColor {
   public GuiButtonQ(int x, int y) {
      super(-82392, x, y, 20, "q", 4775356);
   }

   public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      super.drawButton(mc, mouseX, mouseY, partialTicks);
      if (ContributorRewardHandler.localPatronTier > 0) {
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         int tier = Math.min(4, ContributorRewardHandler.localPatronTier);
         int u = 256 - tier * 9;
         int v = 26;
         mc.renderEngine.bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
         this.drawTexturedModalRect(this.x - 2, this.y - 2, u, v, 9, 9);
      }

   }
}
