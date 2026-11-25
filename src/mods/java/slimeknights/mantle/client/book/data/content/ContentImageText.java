package slimeknights.mantle.client.book.data.content;

import java.util.ArrayList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.gui.book.GuiBook;
import slimeknights.mantle.client.gui.book.element.BookElement;
import slimeknights.mantle.client.gui.book.element.ElementImage;
import slimeknights.mantle.client.gui.book.element.ElementText;

@SideOnly(Side.CLIENT)
public class ContentImageText extends PageContent {
   public String title = null;
   public ImageData image;
   public TextData[] text;

   public ContentImageText() {
   }

   public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
      int y = 16;
      if (this.title != null && !this.title.isEmpty()) {
         this.addTitle(list, this.title);
      } else {
         y = 0;
      }

      if (this.image != null && this.image.location != null) {
         list.add(new ElementImage(0, y, GuiBook.PAGE_WIDTH, 100, this.image));
      } else {
         list.add(new ElementImage(0, y, 32, 32, ImageData.MISSING));
      }

      if (this.text != null && this.text.length > 0) {
         list.add(new ElementText(0, y + 105, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT - 105 - y, this.text));
      }

   }
}
