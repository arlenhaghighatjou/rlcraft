package slimeknights.mantle.client.book.data.content;

import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementText;

public class ContentTableOfContents extends PageContent {
   public String title;
   public TextData[] data;

   public ContentTableOfContents(String title, TextData... contents) {
      this.title = title;
      this.data = contents;
   }

   public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
      int y = 0;
      if (this.title != null && !this.title.trim().isEmpty()) {
         this.addTitle(list, this.title);
         y += 16;
      }

      for(int i = 0; i < this.data.length; ++i) {
         TextData text = this.data[i];
         list.add(new ElementText(0, y + i * (int)((float)Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * text.scale), GuiBook.PAGE_WIDTH, Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT, new TextData[]{text}));
      }

   }
}
