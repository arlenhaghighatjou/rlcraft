package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ElementInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.client.config.GuiUtils;

public class ElementDescriptionList extends GuiScrollingList {
   protected BeastiaryScreen parentGui;
   public ElementInfo elementInfo;

   public ElementDescriptionList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
      super(Minecraft.getMinecraft(), width, height, top, bottom, x, 10800, width, height);
      this.parentGui = parentGui;
   }

   protected int getSize() {
      return 1;
   }

   protected void elementClicked(int index, boolean doubleClick) {
      this.selectedIndex = index;
   }

   protected boolean isSelected(int index) {
      return false;
   }

   protected void drawBackground() {
   }

   protected int getContentHeight() {
      return this.parentGui.getFontRenderer().getWordWrappedHeight(this.getContent(), this.listWidth) + 10;
   }

   protected void drawSlot(int index, int boxRight, int boxTop, int boxBottom, Tessellator tessellator) {
      if (index == 0 && this.elementInfo != null) {
         this.parentGui.drawSplitString(this.getContent(), this.left + 6, boxTop, this.listWidth - 20, 16777215, true);
      }

   }

   public String getContent() {
      if (this.elementInfo == null) {
         return "";
      } else {
         String text = "\u00a7l" + this.elementInfo.getTitle() + ": \u00a7r";
         text = text + "\n" + this.elementInfo.getDescription();
         text = text + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.elements.buffs") + ": \u00a7r";

         for(String buff : this.elementInfo.buffs) {
            ResourceLocation effectResource = new ResourceLocation(buff);
            text = text + "\n" + LanguageManager.translate("effect." + effectResource.getResourcePath());
            text = text + ": " + LanguageManager.translate("effect." + effectResource.getResourcePath() + ".description");
         }

         text = text + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.elements.debuffs") + ": \u00a7r";

         for(String debuff : this.elementInfo.debuffs) {
            if ("burning".equals(debuff)) {
               text = text + "\n" + LanguageManager.translate("effect.burning");
               text = text + ": " + LanguageManager.translate("effect.burning.description");
            } else {
               ResourceLocation effectResource = new ResourceLocation(debuff);
               text = text + "\n" + LanguageManager.translate("effect." + effectResource.getResourcePath());
               text = text + ": " + LanguageManager.translate("effect." + effectResource.getResourcePath() + ".description");
            }
         }

         return text;
      }
   }

   protected void drawGradientRect(int left, int top, int right, int bottom, int color1, int color2) {
      color1 = 856690704;
      GuiUtils.drawGradientRect(0, left, top, right, bottom, color1, color1);
   }
}
