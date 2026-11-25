package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.client.avatar.Avatar;
import com.creativemd.creativecore.client.avatar.AvatarItemStack;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.controls.container.client.GuiSlotControl;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;

public abstract class GuiAvatarLabelClickable extends GuiClickableLabel {
   public int avatarSize;
   public int spaceBetween;
   public Avatar avatar;
   public boolean avatarLeft;

   public GuiAvatarLabelClickable(String name, String title, int x, int y, int color, Avatar avatar, boolean avatarLeft) {
      super(name, title, x, y, GuiRenderHelper.instance.getStringWidth(title), 16, color);
      this.avatarSize = 16;
      this.spaceBetween = 6;
      this.width += this.getAdditionalSize();
      this.avatar = avatar;
      this.avatarLeft = avatarLeft;
   }

   public GuiAvatarLabelClickable(String title, int x, int y, int color, Avatar avatar) {
      this(title, title, x, y, color, avatar, true);
   }

   protected int getAdditionalSize() {
      return this.avatarSize + this.spaceBetween;
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      if (this.avatarLeft) {
         super.renderContent(helper, style, width, height);
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)(width / 2 - (helper.getStringWidth(this.caption) + this.getAdditionalSize()) / 2), (float)(height / 2 - this.avatarSize / 2), 0.0F);
         this.avatar.handleRendering(helper, this.avatarSize, this.avatarSize);
         GlStateManager.popMatrix();
      } else {
         helper.font.drawStringWithShadow(this.caption, 0.0F, 0.0F, this.getColor());
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)(helper.font.getStringWidth(this.caption) + this.spaceBetween), (float)(height / 2 - this.avatarSize / 2), 0.0F);
         this.avatar.handleRendering(helper, this.avatarSize, this.avatarSize);
         GlStateManager.popMatrix();
      }

   }

   public List<String> getTooltip() {
      return this.avatar instanceof AvatarItemStack ? GuiSlotControl.getTooltip(((AvatarItemStack)this.avatar).stack) : super.getTooltip();
   }
}
