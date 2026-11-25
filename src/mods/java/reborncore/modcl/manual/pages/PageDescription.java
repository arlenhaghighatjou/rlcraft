package reborncore.modcl.manual.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import reborncore.modcl.manual.GuiTeamRebornManual;

public class PageDescription extends ManualPage {
   private String title = "Blank Title";
   private List<String> description = new ArrayList();

   public PageDescription() {
   }

   private static List<String> wrapDescriptionLines(List<String> descriptionLines) {
      Minecraft minecraft = Minecraft.getMinecraft();
      List<String> output = new ArrayList();

      for(String descriptionLine : descriptionLines) {
         List<String> textLines = minecraft.fontRenderer.listFormattedStringToWidth(descriptionLine, GuiTeamRebornManual.xSize - 12);
         output.addAll(textLines);
      }

      return output;
   }

   private void addStrings(String... strings) {
      for(String string : strings) {
         this.description.add(string);
      }

   }

   private List<String> expandLines(List<String> strings) {
      List<String> output = new ArrayList();

      for(String string : strings) {
         String[] expandedStrings = string.split("\\\\n");
         Collections.addAll(output, expandedStrings);
      }

      return output;
   }

   public ManualPage nextPage() {
      return null;
   }

   public String title() {
      return this.title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public List<String> getDescription() {
      return this.description;
   }

   public void setDescription(String... description) {
      this.addStrings(description);
   }

   public void setDescription(List<String> description) {
      this.description = description;
   }

   public void draw(Minecraft mc, GuiTeamRebornManual manual) {
      this.description = this.expandLines(this.getDescription());
      this.description = wrapDescriptionLines(this.getDescription());
      int y = 21;

      for(String string : this.getDescription()) {
         if (y > GuiTeamRebornManual.ySize - 6 - mc.fontRenderer.FONT_HEIGHT) {
            break;
         }

         drawString(string, 6, y, -11382190, manual);
         y += mc.fontRenderer.FONT_HEIGHT + 2;
      }

   }
}
