package net.silentchaos512.lib.guidebook.button;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;
import net.silentchaos512.lib.util.AssetUtil;
import net.silentchaos512.lib.util.StringUtil;

@SideOnly(Side.CLIENT)
public class EntryButton extends GuiButton {
   private final GuiGuideBase gui;
   private final ItemStack stackToRender;

   public EntryButton(GuiGuideBase gui, int id, int x, int y, int width, int height, String text, @Nonnull ItemStack stackToRender) {
      super(id, x, y, width, height, text);
      this.gui = gui;
      this.stackToRender = stackToRender;
   }

   public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float par4) {
      if (this.visible) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.blendFunc(770, 771);
         this.mouseDragged(minecraft, mouseX, mouseY);
         int textOffsetX = 0;
         if (!this.stackToRender.isEmpty()) {
            GlStateManager.pushMatrix();
            AssetUtil.renderStackToGui(this.stackToRender, this.x - 4, this.y, 0.725F);
            GlStateManager.popMatrix();
            textOffsetX = 10;
         }

         float scale = this.gui.getMediumFontSize();
         if (this.hovered) {
            GlStateManager.pushMatrix();
            AssetUtil.drawHorizontalGradientRect(this.x + textOffsetX - 1, this.y + this.height - 1, this.x + (int)((float)minecraft.fontRenderer.getStringWidth(this.displayString) * scale) + textOffsetX + 1, this.y + this.height, -2147461377, 22271, this.zLevel);
            GlStateManager.popMatrix();
         }

         StringUtil.renderScaledAsciiString(minecraft.fontRenderer, this.displayString, this.x + textOffsetX, this.y + 2 + (this.height - 8) / 2, 0, false, scale);
      }

   }
}
