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
public class ContentTextLeftImage extends PageContent {
   public String title = null;
   public ImageData image;
   public TextData[] text1;
   public TextData[] text2;

   public ContentTextLeftImage() {
   }

   public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
      int y = 16;
      if (this.title != null && !this.title.isEmpty()) {
         this.addTitle(list, this.title);
      } else {
         y = 0;
      }

      if (this.image != null && this.image.location != null) {
         list.add(new ElementImage(0, y, 50, 50, this.image));
      } else {
         list.add(new ElementImage(0, y, 50, 50, ImageData.MISSING));
      }

      if (this.text1 != null && this.text1.length > 0) {
         list.add(new ElementText(55, y, GuiBook.PAGE_WIDTH - 55, 50, this.text1));
      }

      if (this.text2 != null && this.text2.length > 0) {
         list.add(new ElementText(0, y + 55, GuiBook.PAGE_WIDTH, GuiBook.PAGE_HEIGHT - 55 - y, this.text2));
      }

   }
}
