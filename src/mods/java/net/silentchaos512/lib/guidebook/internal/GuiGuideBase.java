package net.silentchaos512.lib.guidebook.internal;

import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public abstract class GuiGuideBase extends GuiScreen {
   public GuiGuideBase() {
   }

   public abstract void renderScaledAsciiString(String var1, int var2, int var3, int var4, boolean var5, float var6);

   public abstract void renderSplitScaledAsciiString(String var1, int var2, int var3, int var4, boolean var5, float var6, int var7);

   public abstract List<GuiButton> getButtonList();

   public abstract int getGuiLeft();

   public abstract int getGuiTop();

   public abstract int getSizeX();

   public abstract int getSizeY();

   public abstract void addOrModifyItemRenderer(ItemStack var1, int var2, int var3, float var4, boolean var5);

   public abstract float getSmallFontSize();

   public abstract float getMediumFontSize();

   public abstract float getLargeFontSize();
}
