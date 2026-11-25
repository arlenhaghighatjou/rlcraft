package vazkii.quark.api;

import net.minecraft.client.gui.GuiTextField;

public interface IItemSearchBar {
   void onSearchBarAdded(GuiTextField var1);

   default boolean renderBackground(int x, int y) {
      return false;
   }
}
