package net.ilexiconn.llibrary.client.gui.survivaltab;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SurvivalTab {
   private int index;
   private String label;
   private Class<? extends GuiContainer> container;

   SurvivalTab(int index, String label, Class<? extends GuiContainer> container) {
      this.index = index;
      this.label = label;
      this.container = container;
   }

   public int getIndex() {
      return this.index;
   }

   public int getColumn() {
      return this.index > 7 ? (this.index - 8) % 8 % 5 : this.index % 9;
   }

   public int getPage() {
      return this.index > 7 ? (this.index - 8) / 8 + 1 : 0;
   }

   public String getLabel() {
      return this.label;
   }

   public Class<? extends GuiContainer> getContainer() {
      return this.container;
   }
}
