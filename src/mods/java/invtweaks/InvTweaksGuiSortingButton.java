package invtweaks;

import invtweaks.api.SortingMethod;
import invtweaks.api.container.ContainerSection;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class InvTweaksGuiSortingButton extends InvTweaksGuiIconButton {
   private final ContainerSection section = ContainerSection.CHEST;
   private SortingMethod algorithm;
   private int rowSize;

   public InvTweaksGuiSortingButton(InvTweaksConfigManager cfgManager_, int id_, int x, int y, int w, int h, String displayString_, String tooltip, SortingMethod algorithm_, int rowSize_, boolean useCustomTexture) {
      super(cfgManager_, id_, x, y, w, h, displayString_, tooltip, useCustomTexture);
      this.algorithm = algorithm_;
      this.rowSize = rowSize_;
   }

   public void drawButton(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      super.drawButton(mc, mouseX, mouseY, partialTicks);
      int textColor = this.getTextColor(mouseX, mouseY);
      String var6 = this.displayString;
      byte var7 = -1;
      switch(var6.hashCode()) {
      case 104:
         if (var6.equals("h")) {
            var7 = 0;
         }
         break;
      case 118:
         if (var6.equals("v")) {
            var7 = 1;
         }
      }

      switch(var7) {
      case 0:
         drawRect(this.x + 3, this.y + 3, this.x + this.width - 3, this.y + 4, textColor);
         drawRect(this.x + 3, this.y + 6, this.x + this.width - 3, this.y + 7, textColor);
         break;
      case 1:
         drawRect(this.x + 3, this.y + 3, this.x + 4, this.y + this.height - 3, textColor);
         drawRect(this.x + 6, this.y + 3, this.x + 7, this.y + this.height - 3, textColor);
         break;
      default:
         drawRect(this.x + 3, this.y + 3, this.x + this.width - 3, this.y + 4, textColor);
         drawRect(this.x + 5, this.y + 4, this.x + 6, this.y + 5, textColor);
         drawRect(this.x + 4, this.y + 5, this.x + 5, this.y + 6, textColor);
         drawRect(this.x + 3, this.y + 6, this.x + this.width - 3, this.y + 7, textColor);
      }

   }

   public boolean mousePressed(Minecraft minecraft, int i, int j) {
      if (minecraft.playerController.isSpectator()) {
         return false;
      } else if (super.mousePressed(minecraft, i, j)) {
         try {
            (new InvTweaksHandlerSorting(minecraft, this.cfgManager.getConfig(), this.section, this.algorithm, this.rowSize)).sort();
         } catch (Exception var5) {
            InvTweaks.logInGameErrorStatic("invtweaks.sort.chest.error", var5);
            var5.printStackTrace();
         }

         return true;
      } else {
         return false;
      }
   }
}
