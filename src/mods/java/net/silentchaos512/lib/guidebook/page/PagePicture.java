package net.silentchaos512.lib.guidebook.page;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;

public class PagePicture extends GuidePage {
   private final int yTextOffset;
   private ResourceLocation resource;

   public PagePicture(GuideBook book, int localizationKey, ResourceLocation res, int yTextOffset, int priority) {
      super(book, localizationKey, priority);
      this.yTextOffset = yTextOffset;
      this.resource = res;
   }

   public PagePicture(GuideBook book, int localizationKey, ResourceLocation res, int yTextOffset) {
      super(book, localizationKey);
      this.yTextOffset = yTextOffset;
      this.resource = res;
   }

   @SideOnly(Side.CLIENT)
   public void drawScreenPre(GuiGuideBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks) {
      super.drawScreenPre(gui, startX, startY, mouseX, mouseY, partialTicks);
      gui.mc.getTextureManager().bindTexture(this.resource);
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GuiUtils.drawTexturedModalRect(startX - 6, startY - 7, 0, 0, 256, 256, 0.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.popMatrix();
      PageTextOnly.renderTextToPage(gui, this, startX + 6, startY - 7 + this.yTextOffset);
   }
}
