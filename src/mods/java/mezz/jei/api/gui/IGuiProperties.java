package mezz.jei.api.gui;

import net.minecraft.client.gui.GuiScreen;

public interface IGuiProperties {
   Class<? extends GuiScreen> getGuiClass();

   int getGuiLeft();

   int getGuiTop();

   int getGuiXSize();

   int getGuiYSize();

   int getScreenWidth();

   int getScreenHeight();
}
