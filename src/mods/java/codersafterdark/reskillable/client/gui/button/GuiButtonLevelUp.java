package codersafterdark.reskillable.client.gui.button;

import codersafterdark.reskillable.base.ConfigHandler;
import codersafterdark.reskillable.client.gui.GuiSkillInfo;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GuiButtonLevelUp extends GuiButton {
   int cost = Integer.MAX_VALUE;
   float renderTicks;

   public GuiButtonLevelUp(int x, int y) {
      super(0, x, y, 14, 14, "");
   }

   public void setCost(int cost) {
      this.cost = cost;
   }

   public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float f) {
      this.enabled = mc.player.experienceLevel >= this.cost || mc.player.isCreative();
      if (ConfigHandler.enableLevelUp && this.enabled) {
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         mc.renderEngine.bindTexture(GuiSkillInfo.SKILL_INFO_RES);
         int x = this.x;
         int y = this.y;
         int u = 176;
         int v = 0;
         int w = this.width;
         int h = this.height;
         if (mouseX > this.x && mouseY > this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
            v += h;
         } else {
            float speedModifier = 4.0F;
            GlStateManager.color(1.0F, 1.0F, 1.0F, (float)(Math.sin((double)((float)mc.player.ticksExisted / speedModifier)) + 1.0D) / 2.0F);
         }

         this.drawTexturedModalRect(x, y, u, v, w, h);
      }

   }
}
