package net.silentchaos512.lib.guidebook.chapter;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;
import net.silentchaos512.lib.guidebook.IGuideEntry;
import net.silentchaos512.lib.guidebook.IGuidePage;

public class GuideChapter implements IGuideChapter {
   public final IGuidePage[] pages;
   public final IGuideEntry entry;
   public final ItemStack displayStack;
   private final GuideBook book;
   private final String identifier;
   private final int priority;
   public TextFormatting color;

   public GuideChapter(GuideBook book, String identifier, IGuideEntry entry, ItemStack displayStack, IGuidePage... pages) {
      this(book, identifier, entry, displayStack, 0, pages);
   }

   public GuideChapter(GuideBook book, String identifier, IGuideEntry entry, ItemStack displayStack, int priority, IGuidePage... pages) {
      this.book = book;
      this.pages = pages;
      this.identifier = identifier;
      this.entry = entry;
      this.displayStack = displayStack;
      this.priority = priority;
      this.color = TextFormatting.RESET;
      this.entry.addChapter(this);

      for(IGuidePage page : this.pages) {
         page.setChapter(this);
      }

   }

   public IGuidePage[] getAllPages() {
      return this.pages;
   }

   @SideOnly(Side.CLIENT)
   public String getLocalizedName() {
      return this.book.i18n.translate("guide", "chapter." + this.getIdentifier() + ".name");
   }

   @SideOnly(Side.CLIENT)
   public String getLocalizedNameWithFormatting() {
      return this.color + this.getLocalizedName();
   }

   public IGuideEntry getEntry() {
      return this.entry;
   }

   @Nonnull
   public ItemStack getDisplayItemStack() {
      return this.displayStack;
   }

   public String getIdentifier() {
      return this.identifier;
   }

   public int getPageIndex(IGuidePage page) {
      for(int i = 0; i < this.pages.length; ++i) {
         if (this.pages[i] == page) {
            return i;
         }
      }

      return -1;
   }

   public int getSortingPriority() {
      return this.priority;
   }

   public GuideChapter setImportant() {
      this.color = TextFormatting.DARK_GREEN;
      return this;
   }

   public GuideChapter setSpecial() {
      this.color = TextFormatting.DARK_PURPLE;
      return this;
   }
}
