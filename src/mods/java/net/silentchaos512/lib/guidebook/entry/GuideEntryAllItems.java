package net.silentchaos512.lib.guidebook.entry;

import java.util.List;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;

public class GuideEntryAllItems extends GuideEntry {
   public GuideEntryAllItems(GuideBook book, String identifier) {
      super(book, identifier, -2147483647);
   }

   public void addChapter(IGuideChapter chapter) {
   }

   public List<IGuideChapter> getAllChapters() {
      return this.book.getChapters();
   }
}
