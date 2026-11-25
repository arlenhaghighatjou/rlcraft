package com.lycanitesmobs.client.gui.beastiary.lists;

import com.lycanitesmobs.client.gui.beastiary.BeastiaryScreen;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.VersionChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

public class BeastiaryIndexList extends GuiScrollingList {
   protected BeastiaryScreen parentGui;
   public VersionChecker.VersionInfo versionInfo;

   public BeastiaryIndexList(BeastiaryScreen parentGui, int width, int height, int top, int bottom, int x) {
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
      if (index == 0 && this.versionInfo != null) {
         this.parentGui.drawSplitString(this.getContent(), this.left + 6, boxTop, this.listWidth - 20, 16777215, true);
      }

   }

   public String getContent() {
      if (this.versionInfo == null) {
         return "";
      } else {
         String content = "\u00a7l\u00a7n" + LanguageManager.translate("gui.beastiary.index.changes") + "\u00a7r";
         content = content + "\n\u00a7l" + LanguageManager.translate("gui.beastiary.index.changes.name") + ":\u00a7r " + this.versionInfo.name;
         if (this.versionInfo.newFeatures.length() > 0) {
            content = content + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.index.changes.new") + ":\u00a7r\n" + this.versionInfo.newFeatures;
         }

         if (this.versionInfo.configChanges.length() > 0) {
            content = content + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.index.changes.config") + ":\u00a7r\n" + this.versionInfo.configChanges;
         }

         if (this.versionInfo.majorFixes.length() > 0) {
            content = content + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.index.changes.major") + ":\u00a7r\n" + this.versionInfo.majorFixes;
         }

         if (this.versionInfo.changes.length() > 0) {
            content = content + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.index.changes.gameplay") + ":\u00a7r\n" + this.versionInfo.changes;
         }

         if (this.versionInfo.balancing.length() > 0) {
            content = content + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.index.changes.balancing") + ":\u00a7r\n" + this.versionInfo.balancing;
         }

         if (this.versionInfo.minorFixes.length() > 0) {
            content = content + "\n\n\u00a7l" + LanguageManager.translate("gui.beastiary.index.changes.minor") + ":\u00a7r\n" + this.versionInfo.minorFixes;
         }

         return content;
      }
   }
}
