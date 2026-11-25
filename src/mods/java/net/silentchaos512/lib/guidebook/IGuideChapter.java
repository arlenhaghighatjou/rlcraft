package net.silentchaos512.lib.guidebook;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuideChapter {
   IGuidePage[] getAllPages();

   @SideOnly(Side.CLIENT)
   String getLocalizedName();

   @SideOnly(Side.CLIENT)
   String getLocalizedNameWithFormatting();

   IGuideEntry getEntry();

   @Nonnull
   ItemStack getDisplayItemStack();

   String getIdentifier();

   int getPageIndex(IGuidePage var1);

   int getSortingPriority();
}
