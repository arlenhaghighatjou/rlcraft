package net.silentchaos512.lib.guidebook;

import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;

public interface IGuidePage {
   void getItemStacksForPage(List<ItemStack> var1);

   void getFluidStacksForPage(List<FluidStack> var1);

   IGuideChapter getChapter();

   void setChapter(IGuideChapter var1);

   @SideOnly(Side.CLIENT)
   String getInfoText();

   @SideOnly(Side.CLIENT)
   void mouseClicked(GuiGuideBase var1, int var2, int var3, int var4);

   @SideOnly(Side.CLIENT)
   void mouseReleased(GuiGuideBase var1, int var2, int var3, int var4);

   @SideOnly(Side.CLIENT)
   void mouseClickMove(GuiGuideBase var1, int var2, int var3, int var4, long var5);

   @SideOnly(Side.CLIENT)
   void actionPerformed(GuiGuideBase var1, GuiButton var2);

   @SideOnly(Side.CLIENT)
   void initGui(GuiGuideBase var1, int var2, int var3);

   @SideOnly(Side.CLIENT)
   void updateScreen(GuiGuideBase var1, int var2, int var3, int var4);

   @SideOnly(Side.CLIENT)
   void drawScreenPre(GuiGuideBase var1, int var2, int var3, int var4, int var5, float var6);

   @SideOnly(Side.CLIENT)
   void drawScreenPost(GuiGuideBase var1, int var2, int var3, int var4, int var5, float var6);

   boolean shouldBeOnLeftSide();

   String getIdentifier();

   String getWebLink();

   IGuidePage addTextReplacement(String var1, String var2);

   IGuidePage addTextReplacement(String var1, float var2);

   IGuidePage addTextReplacement(String var1, int var2);

   int getSortingPriority();
}
