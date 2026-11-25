package slimeknights.mantle.client.book.data.content;

import java.util.ArrayList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementText;

@SideOnly(Side.CLIENT)
public class ContentText extends PageContent {
   public String title = null;
   public TextData[] text;

   public ContentText() {
   }

   public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
      int y = 16;
      if (this.title != null && !this.title.isEmpty()) {
         this.addTitle(list, this.title);
      } else {
         y = 0;
      }

      if (this.text != null && this.text.length > 0) {
         list.add(new ElementText(0, y, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT - y, this.text));
      }

   }
}
