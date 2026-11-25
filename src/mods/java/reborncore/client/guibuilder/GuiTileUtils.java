package reborncore.client.guibuilder;

import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiTileUtils {
   public GuiTileUtils() {
   }

   @SideOnly(Side.CLIENT)
   public static void drawSlotsFromList(List<Slot> slotList, GuiBuilder builder, GuiScreen guiContainer, int guiLeft, int guiTop) {
      if (slotList != null) {
         for(Slot s : slotList) {
            builder.drawSlot(guiContainer, guiLeft + s.xPos - 1, guiTop + s.yPos - 1);
         }
      }

   }
}
