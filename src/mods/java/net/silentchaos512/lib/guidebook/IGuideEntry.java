package net.silentchaos512.lib.guidebook;

import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuideEntry {
   List<IGuideChapter> getAllChapters();

   String getIdentifier();

   @SideOnly(Side.CLIENT)
   String getLocalizedName();

   @SideOnly(Side.CLIENT)
   String getLocalizedNameWithFormatting();

   void addChapter(IGuideChapter var1);

   @SideOnly(Side.CLIENT)
   List<IGuideChapter> getChaptersForDisplay(String var1);

   int getSortingPriority();

   @SideOnly(Side.CLIENT)
   boolean visibleOnFrontPage();
}
