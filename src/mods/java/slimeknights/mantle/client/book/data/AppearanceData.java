package slimeknights.mantle.client.book.data;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AppearanceData implements IDataItem {
   public int coverColor = 9127473;
   public int arrowColor = 16777171;
   public int arrowColorHover = 16733212;
   public int hoverColor = 2012107804;
   public int slotColor = 16745548;
   public int lockedSectionColor = 0;
   public float scale = 0.5F;
   public String title = "";
   public String subtitle = "";
   public boolean drawPageNumbers = true;
   public boolean drawSectionListText = false;
   public int structureButtonColor = 14934972;
   public int structureButtonColorHovered = 7786984;
   public int structureButtonColorToggled = 6801256;

   public AppearanceData() {
   }

   public void load() {
   }
}
