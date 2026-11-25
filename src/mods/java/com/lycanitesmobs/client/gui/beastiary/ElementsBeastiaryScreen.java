package com.lycanitesmobs.client.gui.beastiary;

import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.gui.beastiary.lists.ElementDescriptionList;
import com.lycanitesmobs.client.gui.beastiary.lists.ElementList;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.info.ElementInfo;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;

public class ElementsBeastiaryScreen extends BeastiaryScreen {
   public ElementInfo elementInfo;
   protected ElementList elementList;
   protected ElementDescriptionList descriptionList;

   public static void openToPlayer(EntityPlayer player) {
      if (player != null) {
         player.openGui(LycanitesMobs.instance, GuiHandler.GuiType.BEASTIARY.id, player.getEntityWorld(), GuiHandler.Beastiary.ELEMENTS.id, 0, 0);
      }

   }

   public ElementsBeastiaryScreen(EntityPlayer player) {
      super(player);
   }

   public String getTitle() {
      return this.elementInfo != null ? "" : LanguageManager.translate("gui.beastiary.elements");
   }

   public void initControls() {
      super.initControls();
      this.elementList = new ElementList(this, this.colLeftWidth, this.colLeftHeight, this.colLeftY, this.colLeftY + this.colLeftHeight, this.colLeftX);
      int descriptionListY = this.colRightY;
      this.descriptionList = new ElementDescriptionList(this, this.colRightWidth, this.colRightHeight, descriptionListY, this.colRightY + this.colRightHeight, this.colRightX);
   }

   public void drawBackground(int x, int y, float partialTicks) {
      super.drawBackground(x, y, partialTicks);
   }

   protected void updateControls(int x, int y, float partialTicks) {
      this.elementList.drawScreen(x, y, partialTicks);
      if (this.elementInfo != null) {
         this.descriptionList.elementInfo = this.elementInfo;
         this.descriptionList.drawScreen(x, y, partialTicks);
      }

   }

   public void drawForeground(int x, int y, float partialTicks) {
      super.drawForeground(x, y, partialTicks);
      if (this.elementInfo == null) {
         String info = LanguageManager.translate("gui.beastiary.elements.about");
         this.drawSplitString(info, this.colRightX + 1, this.colRightY + 12 + 1, this.colRightWidth, 16777215, true);
      }

   }

   protected void actionPerformed(GuiButton guiButton) throws IOException {
      super.actionPerformed(guiButton);
   }
}
