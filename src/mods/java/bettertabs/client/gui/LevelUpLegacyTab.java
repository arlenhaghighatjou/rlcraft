package bettertabs.client.gui;

import levelup.gui.GuiClasses;
import levelup.gui.GuiSkills;
import levelup.gui.LevelUpHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.lwjgl.opengl.GL11;

public class LevelUpLegacyTab extends GuiButton {
   public ResourceLocation texture = new ResourceLocation("bettertabs", "textures/icons/tabback.png");
   public ResourceLocation icon = new ResourceLocation("bettertabs", "textures/icons/levelupbook.png");

   public LevelUpLegacyTab(int id, int x, int y, String text) {
      super(id, x, y, 28, 28, text);
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float f) {
      this.enabled = !mc.player.getRecipeBook().isGuiOpen();
      if (this.enabled) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         mc.renderEngine.bindTexture(this.texture);
         this.drawTexturedModalRect(this.x, this.y, 0, 0, 26, 28);
         mc.renderEngine.bindTexture(this.icon);
         this.drawTexturedModalRect(this.x + 7, this.y + 6, 0, 0, 16, 16);
         this.hovered = this.x <= mouseX && mouseX < this.x + this.width && this.y <= mouseY && mouseY < this.y + this.height;
         if (this.hovered) {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            String txt = I18n.format("bettertabs.tab.leveluplegacy", new Object[0]);
            int i = mc.fontRenderer.getStringWidth(txt);
            int l1 = mouseX + 12;
            int i2 = mouseY - 12;
            int k = 8;
            if (l1 + i > mc.currentScreen.width) {
               l1 -= 28 + i;
            }

            if (i2 + k + 6 > mc.currentScreen.height) {
               i2 = mc.currentScreen.height - k - 6;
            }

            this.zLevel = 300.0F;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);
            this.zLevel = 0.0F;
            GlStateManager.disableDepth();
            mc.fontRenderer.drawStringWithShadow(txt, (float)l1, (float)i2, -1);
            GlStateManager.enableDepth();
            GlStateManager.enableRescaleNormal();
         }

      }
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.hovered && this.enabled) {
         if (LevelUpHUD.canShowSkills()) {
            mc.displayGuiScreen(new GuiSkills());
         } else if (LevelUpHUD.canSelectClass()) {
            mc.displayGuiScreen(new GuiClasses());
         } else {
            mc.displayGuiScreen((GuiScreen)null);
            mc.player.sendStatusMessage(new TextComponentTranslation("level.invalid", new Object[0]), true);
         }
      }

      return this.hovered;
   }
}
