package mezz.jei.gui.overlay.bookmarks;

import java.util.List;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.bookmarks.BookmarkList;
import mezz.jei.config.Config;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.GuiHelper;
import mezz.jei.gui.elements.GuiIconToggleButton;
import mezz.jei.util.Translator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextFormatting;

public class BookmarkButton extends GuiIconToggleButton {
   private final BookmarkOverlay bookmarkOverlay;
   private final BookmarkList bookmarkList;

   public static BookmarkButton create(BookmarkOverlay bookmarkOverlay, BookmarkList bookmarkList, GuiHelper guiHelper) {
      IDrawableStatic offIcon = guiHelper.getBookmarkButtonDisabledIcon();
      IDrawableStatic onIcon = guiHelper.getBookmarkButtonEnabledIcon();
      return new BookmarkButton(offIcon, onIcon, bookmarkOverlay, bookmarkList);
   }

   private BookmarkButton(IDrawable offIcon, IDrawable onIcon, BookmarkOverlay bookmarkOverlay, BookmarkList bookmarkList) {
      super(offIcon, onIcon);
      this.bookmarkOverlay = bookmarkOverlay;
      this.bookmarkList = bookmarkList;
   }

   protected void getTooltips(List<String> tooltip) {
      tooltip.add(Translator.translateToLocal("jei.tooltip.bookmarks"));
      KeyBinding bookmarkKey = KeyBindings.bookmark;
      if (bookmarkKey.getKeyCode() == 0) {
         tooltip.add(TextFormatting.RED + Translator.translateToLocal("jei.tooltip.bookmarks.usage.nokey"));
      } else if (!this.bookmarkOverlay.hasRoom()) {
         tooltip.add(TextFormatting.GOLD + Translator.translateToLocal("jei.tooltip.bookmarks.not.enough.space"));
      } else {
         tooltip.add(TextFormatting.GRAY + Translator.translateToLocalFormatted("jei.tooltip.bookmarks.usage.key", bookmarkKey.getDisplayName()));
      }

   }

   protected boolean isIconToggledOn() {
      return this.bookmarkOverlay.isListDisplayed();
   }

   protected boolean onMouseClicked(int mouseX, int mouseY) {
      if (!this.bookmarkList.isEmpty() && this.bookmarkOverlay.hasRoom()) {
         Config.toggleBookmarkEnabled();
         return true;
      } else {
         return false;
      }
   }
}
